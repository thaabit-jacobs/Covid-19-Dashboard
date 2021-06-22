package org.covid.dashboard.servcies;

import static org.covid.dashboard.util.DateFormatter.*;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.apache.tomcat.jni.Local;
import org.covid.dashboard.model.CountryCases;
import org.covid.dashboard.model.VaccinatedCases;
import org.covid.dashboard.util.DateFormatter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import javax.annotation.PostConstruct;
import java.io.*;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;
import java.time.Year;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VirusDataFetcherService extends DateFormatter {
    private static String ConfirmedCases=
            "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private static String DeathCases=
            "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv";
    private static String RecoveredCases=
            "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv";
    private static final LocalDate startingDate = LocalDate.of(2020, 1,22);

    private List<CountryCases> confirmedCasesList = new ArrayList<>();
    private List<CountryCases> deathCasesList = new ArrayList<>();
    private List<CountryCases> recoveredCasesList = new ArrayList<>();


    public LocalDate getStartingDate(){
        return startingDate;
    }
    public static LocalDate  getCurrentDay(){
        return LocalDate.now();
    }

    public List<CountryCases> getConfirmedCasesList() {
        return new ArrayList<>(confirmedCasesList);
    }

    public List<CountryCases> getDeathCasesList() {
        return new ArrayList<>(deathCasesList);
    }

    public List<CountryCases> getRecoveredCasesList() {
        return new ArrayList<>(recoveredCasesList);
    }

    @Autowired
    RestTemplate restTemplate;

    @PostConstruct
    public void fetchAllVirusData() throws IOException, InterruptedException {

        fetchVirusData(ConfirmedCases, confirmedCasesList);

        fetchVirusData(RecoveredCases, recoveredCasesList);

        fetchVirusData(DeathCases, deathCasesList);
    }

    private void fetchVirusData(String dataURI, List<CountryCases> countryCasesList) throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        String result = restTemplate.getForObject(dataURI, String.class);

        populatedCases(result, countryCasesList);
    }

    private void populatedCases(String csvFile, List<CountryCases> countryCasesList) throws IOException {
        Reader reader = new StringReader(csvFile);

        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);


        for (CSVRecord record : records) {
            CountryCases countryCases = new CountryCases();

            String country = record.get("Country/Region");
            String state = record.get("Province/State");

            LocalDate currentDateTwo = startingDate;

            if (isCountryInCountryList(countryCasesList,country) && !state.equals(" ")){
                int countryIndex = getIndexOfCountry(countryCasesList, country);

                CountryCases countryCaseForState = countryCasesList.get(countryIndex);

                while (!currentDateTwo.equals(getCurrentDay())){
                    String currentDateAtIndex = formatLocalDate(currentDateTwo);

                    countryCaseForState.getCasesByDate().put(currentDateAtIndex,
                            countryCaseForState.getCasesByDate().get(currentDateAtIndex) + Long.valueOf(record.get(currentDateAtIndex)));

                    currentDateTwo = currentDateTwo.plusDays(1);
                }

                countryCasesList.set(countryIndex, countryCaseForState);

                continue;
            }

            countryCases.setCountry(country);

            while (!currentDateTwo.equals(getCurrentDay())){
                String currentDateAtIndex = formatLocalDate(currentDateTwo);

                countryCases.getCasesByDate().put(currentDateAtIndex, Long.valueOf(record.get(currentDateAtIndex)));

                currentDateTwo = currentDateTwo.plusDays(1);
        }

            countryCasesList.add(countryCases);
    }
        reader.close();
    }



    public boolean isCountryInCountryList(List<CountryCases> countryCasesList, String countryName) {
        for (CountryCases cc:countryCasesList) {
            if (cc.getCountry().equals(countryName))
                return true;
        }

        return false;
    }

    public int getIndexOfCountry(List<CountryCases> countryCasesList, String countryName) {
        for (int i = 0; i < countryCasesList.size(); i++) {
            if (countryCasesList.get(i).getCountry().equals(countryName))
                return i;
        }
        return -1;
    }

    public static List<String> dateKeys(){
        List<String> dates = new ArrayList<>();

        LocalDate currentDateTwo = startingDate;

            while (!currentDateTwo.equals(getCurrentDay())){
                String currentDateAtIndex = formatLocalDate(currentDateTwo);
                dates.add(currentDateAtIndex);

                currentDateTwo = currentDateTwo.plusDays(1);
            }

            return dates;
    }

    public int getNmberOfCountries(){
        return getConfirmedCasesList().size();
    }
}

