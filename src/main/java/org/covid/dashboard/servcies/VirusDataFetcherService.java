package org.covid.dashboard.servcies;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.covid.dashboard.model.Cases;
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

@Service
public class VirusDataFetcherService {
    private List<Cases> confirmedCasesList = new ArrayList<>();
    private List<Cases> deathCasesList = new ArrayList<>();
    private List<Cases> recoveredCasesList = new ArrayList<>();

    private static String ConfirmedCases=
            "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private static String DeathCases=
            "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv";
    private static String RecoveredCases=
            "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv";

    @PostConstruct
    public void fetchAllVirusData() throws IOException, InterruptedException {
        fetchVirusData(ConfirmedCases, confirmedCasesList);
        fetchVirusData(DeathCases, recoveredCasesList);
        fetchVirusData(RecoveredCases, deathCasesList);
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

    private String[] getFormattedDateHeaderColumns(){
        String currentDayDateHeader = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("M/dd/yy")).toString();
        String previousDayDateHeader = LocalDate.now().minusDays(2).format(DateTimeFormatter.ofPattern("M/dd/yy")).toString();

        return new String[] {currentDayDateHeader, previousDayDateHeader};
    }

}
