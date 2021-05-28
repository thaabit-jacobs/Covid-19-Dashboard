package org.covid.dashboard.servcies;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.covid.dashboard.model.Cases;
import org.covid.dashboard.model.VaccinatedCases;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class VirusDataFetcherService {
    private List<Cases> confirmedCasesList = new ArrayList<>();
    private List<Cases> deathCasesList = new ArrayList<>();
    private List<Cases> recoveredCasesList = new ArrayList<>();
    private List<VaccinatedCases> vaccinatedCasesList = new ArrayList<>();

    private static String ConfirmedCases=
            "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private static String DeathCases=
            "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv";
    private static String RecoveredCases=
            "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv";
    private static String VaccinatedCases=
            "https://raw.githubusercontent.com/govex/COVID-19/master/data_tables/vaccine_data/global_data/vaccine_data_global.csv";

    public List<Cases> getConfirmedCasesList(){
        return new ArrayList<>(confirmedCasesList);
    }

    public List<Cases> getRecoveredCasesList(){
        return new ArrayList<>(recoveredCasesList);
    }

    public List<Cases> getDeathCasesList(){
        return new ArrayList<>(deathCasesList);
    }

    public List<VaccinatedCases> getVaccinatedCasesList(){
        return new ArrayList<>(vaccinatedCasesList);
    }

    @PostConstruct
    public void fetchAllVirusData() throws IOException, InterruptedException {
        fetchVirusData(ConfirmedCases, confirmedCasesList);
        fetchVirusData(RecoveredCases, recoveredCasesList);
        fetchVirusData(DeathCases, deathCasesList);
        fetchVirusDataForVaccinated(VaccinatedCases, vaccinatedCasesList);
    }

    private void fetchVirusData(String dataURI, List<Cases> casesList) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(dataURI))
                .build();

        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        populateConfirmedCases(httpResponse.body(), casesList);
    }

    private void populateConfirmedCases(String csvFile, List<Cases> casesList) throws IOException {
        String[] formattedDateHeaderColumns = getFormattedDateHeaderColumns();

        Reader reader = new StringReader(csvFile);

        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);

        for (CSVRecord record : records) {
            String country = record.get("Country/Region");
            String state = record.get("Province/State");
            Long currentDaysCases = Long.valueOf(record.get(formattedDateHeaderColumns[0]));
            Long previousDaysCases = Long.valueOf(record.get(formattedDateHeaderColumns[1]));

            casesList.add(new Cases(country, state, currentDaysCases, previousDaysCases));
        }

        reader.close();
    }

    private void fetchVirusDataForVaccinated(String dataURI, List<VaccinatedCases> vaccinatedCases) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(dataURI))
                .build();

        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        populateVaccinatedCases(httpResponse.body(), vaccinatedCases);
    }

    private void populateVaccinatedCases(String csvFile, List<VaccinatedCases> casesList) throws IOException {
        Reader reader = new StringReader(csvFile);

        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);

        for (CSVRecord record : records) {
            String country = record.get("Country_Region");
            String state = record.get("Province_State");
            String people_fully_vaccinated = record.get("People_fully_vaccinated");

            if (people_fully_vaccinated.equalsIgnoreCase(""))
                continue;

            long vaccinatedTotal = Long.valueOf(people_fully_vaccinated);

            vaccinatedCasesList.add(new VaccinatedCases(country, state, vaccinatedTotal));
        }

        reader.close();
    }

    private String[] getFormattedDateHeaderColumns(){
        String currentDayDateHeader = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("M/dd/yy")).toString();
        String previousDayDateHeader = LocalDate.now().minusDays(2).format(DateTimeFormatter.ofPattern("M/dd/yy")).toString();

        return new String[] {currentDayDateHeader, previousDayDateHeader};
    }

}
