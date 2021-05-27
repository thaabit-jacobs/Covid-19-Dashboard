package org.covid.dashboard.servcies;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.covid.dashboard.model.ConfirmedCases;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import javax.print.DocFlavor;
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
public class VirusDataService {
    private List<ConfirmedCases> confirmedCasesList = new ArrayList<>();

    private static String ConfirmedCases="https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";

    @PostConstruct
    public void fetchVirusData() throws IOException, InterruptedException {
        HttpClient client = HttpClient.newHttpClient();

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(ConfirmedCases))
                .build();

        HttpResponse<String> httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());

        //System.out.println(httpResponse.body());

        System.out.println("OK");

        getData(httpResponse.body());
    }

    private void getData(String csvFile) throws IOException {
        String currentDayDateHeader = LocalDate.now().minusDays(1).format(DateTimeFormatter.ofPattern("M/dd/yy")).toString();
        String previousDayDateHeader = LocalDate.now().minusDays(2).format(DateTimeFormatter.ofPattern("M/dd/yy")).toString();

        Reader reader = new StringReader(csvFile);

        Iterable<CSVRecord> records = CSVFormat.RFC4180.withFirstRecordAsHeader().parse(reader);

        for (CSVRecord record : records) {
            String country = record.get("Country/Region");
            String state = record.get("Province/State");
            Long currentDaysCases = Long.valueOf(record.get(currentDayDateHeader));
            Long previousDaysCases = Long.valueOf(record.get(previousDayDateHeader));

            confirmedCasesList.add(new ConfirmedCases(country, state, currentDaysCases, previousDaysCases));
        }

        reader.close();
    }

}
