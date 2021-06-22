package org.covid.dashboard.servcies;

import static org.covid.dashboard.util.DateFormatter.*;

import org.apache.commons.math3.util.Precision;
import org.covid.dashboard.model.CountryCases;
import org.covid.dashboard.type.Type;
import org.covid.dashboard.util.DateFormatter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class VirusDataStatsService extends DateFormatter {
    @Autowired
    private VirusDataFetcherService virusDataFetcherService;

    private Map<String, Object> globalTotalsObject = new HashMap<>();
    private Map<String, Object> countryTotalsObject = new HashMap<>();

    public Map<String, Object> getGlobalTotalsObject(){
        return new HashMap<>(globalTotalsObject);
    }

    public Map<String, Object> getCountryTotalsObject(){
        return new HashMap<>(countryTotalsObject);
    }

    private long calculateGlobalTotal(Type type){
        List<CountryCases> selectedListType = getCountryCasesListForType(type);

        return selectedListType.stream()
                .mapToLong(ml -> ml.getCasesByDate().get(formatLocalDate(LocalDate.now().minusDays(1))))
                .summaryStatistics()
                .getSum();
    }

    private List<CountryCases> getCountryCasesListForType(Type type) {
        List<CountryCases> selectedListType = null;

        switch (type) {
            case CONFIRMED:
                selectedListType = new ArrayList<>(virusDataFetcherService.getConfirmedCasesList());
                break;
            case DEATHS:
                selectedListType = new ArrayList<>(virusDataFetcherService.getDeathCasesList());
                break;
            case RECOVERIES:
                selectedListType = new ArrayList<>(virusDataFetcherService.getRecoveredCasesList());
                break;
        }
        return selectedListType;
    }

    private List<Map<String, Object>> getAllCountriesTotalData(Type type){
        List<Map<String, Object>> dataMap = new ArrayList<>();

        String currentDateFormatted = formatLocalDate(virusDataFetcherService.getCurrentDay().minusDays(1));

        //populate name and confirmed cases for current day
        virusDataFetcherService.getConfirmedCasesList()
                .forEach(fe -> {
                    Map<String, Object> map = new HashMap<>();

                    map.put("country", fe.getCountry());
                    map.put("confirmed", fe.getCasesByDate().get(currentDateFormatted));
                    dataMap.add(map);
                });

        //populate deaths cases for current day
        dataMap.stream().forEach( x -> {
            virusDataFetcherService.getDeathCasesList().stream()
            .forEach(y -> {
                if (((String)x.get("country")).equals(y.getCountry()))
                    x.put("deaths", y.getCasesByDate().get(currentDateFormatted));
            });
        });

        //populate recovered cases for current day
        dataMap.stream().forEach( x -> {
            virusDataFetcherService.getRecoveredCasesList().stream()
                    .forEach(y -> {
                        if (((String)x.get("country")).equals(y.getCountry()))
                            x.put("recovered", y.getCasesByDate().get(currentDateFormatted));
                    });
        });

        System.out.println(dataMap);

        //populate death percent cases for current day
        dataMap.stream().forEach( fe -> {
           fe.put("deathsPercent", Precision.round(Long.valueOf((Long)fe.get("deaths")).doubleValue()/
                   Long.valueOf((Long)fe.get("confirmed")).doubleValue() *100 ,1));
        });

        //sort
        switch (type){
            case CONFIRMED:Collections.sort(dataMap, (d1, d2) -> {
                return Long.valueOf((long)(long)d2.get("confirmed")).intValue() - Long.valueOf((long)d1.get("confirmed")).intValue();
            });
            break;
            case RECOVERIES:Collections.sort(dataMap, (d1, d2) -> {
                return Long.valueOf((long)(long)d2.get("recovered")).intValue() - Long.valueOf((long)d1.get("recovered")).intValue();
            });
            break;
            case DEATHS:Collections.sort(dataMap, (d1, d2) -> {
                return Long.valueOf((long)(long)d2.get("deaths")).intValue() - Long.valueOf((long)d1.get("deaths")).intValue();
            });
            break;
            case DEATHS_PERCENT:Collections.sort(dataMap, (d1, d2) -> {
                return Double.valueOf((double)d2.get("deathsPercent")).intValue() - Double.valueOf((double)d1.get("deathsPercent")).intValue();
            });
                break;
        }

        return dataMap;
    }

    public Map<String, List<Long>> getGlobalTotalEachDay(){
        Map<String, List<Long>> result = new HashMap<>();

        result.put("confirmed", new ArrayList<>());
        result.put("deaths", new ArrayList<>());
        result.put("recovered", new ArrayList<>());

        virusDataFetcherService.dateKeys().stream()
                .forEach(x -> {
                    long totalForDate = virusDataFetcherService.getConfirmedCasesList().stream()
                            .mapToLong(m -> m.getCasesByDate().get(x))
                            .summaryStatistics()
                            .getSum();

                    List<Long> confirmedMap = result.get("confirmed");
                    confirmedMap.add(totalForDate);
                    result.put("confirmed", confirmedMap);
                });

        virusDataFetcherService.dateKeys().stream()
                .forEach(x -> {
                    long totalForDate = virusDataFetcherService.getDeathCasesList().stream()
                            .mapToLong(m -> m.getCasesByDate().get(x))
                            .summaryStatistics()
                            .getSum();

                    List<Long> deathMap = result.get("deaths");
                    deathMap.add(totalForDate);
                    result.put("deaths", deathMap);
                });

        virusDataFetcherService.dateKeys().stream()
                .forEach(x -> {
                    long totalForDate = virusDataFetcherService.getRecoveredCasesList().stream()
                            .mapToLong(m -> m.getCasesByDate().get(x))
                            .summaryStatistics()
                            .getSum();

                    List<Long> recoveredMap = result.get("recovered");
                    recoveredMap.add(totalForDate);
                    result.put("recovered", recoveredMap);
                });

        Collections.sort(result.get("recovered"));
        Collections.sort(result.get("deaths"));
        Collections.sort(result.get("confirmed"));

        return result;
    }

    public Map<String, Map<String, Long>> individualCountryTotals(int limit) {
        Map<String, Map<String, Long>> result = new HashMap<>();

        //populate with name and new hashmap
        virusDataFetcherService.getConfirmedCasesList().stream()
                .forEach(fe -> {
                    result.put(fe.getCountry(), new HashMap<>());
                });

        virusDataFetcherService.getConfirmedCasesList().stream()
                .forEach(fe -> {
                   long total = fe.getCasesByDate().get(formatLocalDate(virusDataFetcherService.getCurrentDay().minusDays(1)));

                   Map<String, Long> mapForCountry = result.get(fe.getCountry());
                   mapForCountry.put("confirmed", total);
                   result.put(fe.getCountry(), mapForCountry);
                });

        virusDataFetcherService.getDeathCasesList().stream()
                .forEach(fe -> {
                    long total = fe.getCasesByDate().get(formatLocalDate(virusDataFetcherService.getCurrentDay().minusDays(1)));

                    Map<String, Long> mapForCountry = result.get(fe.getCountry());
                    mapForCountry.put("deaths", total);
                    result.put(fe.getCountry(), mapForCountry);
                });

        virusDataFetcherService.getRecoveredCasesList().stream()
                .forEach(fe -> {
                    long total = fe.getCasesByDate().get(formatLocalDate(virusDataFetcherService.getCurrentDay().minusDays(1)));

                    Map<String, Long> mapForCountry = result.get(fe.getCountry());
                    mapForCountry.put("recovered", total);
                    result.put(fe.getCountry(), mapForCountry);
                });

        LinkedHashMap<String, Map<String, Long>> reverseSortedMap = new LinkedHashMap<>();
        result.entrySet().stream()
                .sorted(Map.Entry.comparingByValue((c1, c2) ->
                        Long.valueOf(c2.get("confirmed")).intValue() - Long.valueOf(c1.get("confirmed")).intValue()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

        LinkedHashMap<String, Map<String, Long>> limited = new LinkedHashMap<>();

        reverseSortedMap.keySet().stream()
                .limit(limit)
                .forEachOrdered(f -> limited.put(f, reverseSortedMap.get(f)));

        return limited;
    }

    public Map<String, Map<String, Map<String,Long>>> allCountryDataForEachDay(int limit, Type type){
        Map<String, Map<String, Map<String,Long>>> result = new HashMap<>();

        virusDataFetcherService.getConfirmedCasesList().stream()
                .forEach(f -> {
                    Map<String, Map<String,Long>> map = new HashMap<>();
                    map.put("confirmed", f.getCasesByDate());
                    result.put(f.getCountry(), map);
                });

        virusDataFetcherService.getDeathCasesList().stream()
                .forEach(f -> {
                    Map<String, Map<String,Long>> map = result.get(f.getCountry());
                    map.put("deaths", f.getCasesByDate());
                    result.put(f.getCountry(), map);
                });

        LinkedHashMap<String, Map<String, Map<String,Long>>>  reverseSortedMap = new LinkedHashMap<>();
        result.entrySet().stream()
                .sorted(Map.Entry.comparingByValue((c1, c2) ->
                        Long.valueOf(c2.get(type.toString().toLowerCase()).get(formatLocalDate(LocalDate.now().minusDays(1)))).intValue() -
                                Long.valueOf(c1.get(type.toString().toLowerCase()).get(formatLocalDate(LocalDate.now().minusDays(1)))).intValue()))
                .forEachOrdered(x -> reverseSortedMap.put(x.getKey(), x.getValue()));

        LinkedHashMap<String, Map<String, Map<String,Long>>>  limited = new LinkedHashMap<>();

        reverseSortedMap.keySet().stream()
                .limit(limit)
                .forEachOrdered(f -> limited.put(f, reverseSortedMap.get(f)));

        return limited;
    }

    public List<String> getCountryNamesSorted(){
        List<String> names = new ArrayList<>();

        names = virusDataFetcherService.getConfirmedCasesList().stream()
                .map(m -> m.getCountry())
                .collect(Collectors.toList());

        Collections.sort(names);

        return names;
    }


    public void filterType(Type type){
        initializeData(type);
    }

    private void initializeData(Type type){
        globalTotalsObject.put("confirmed", calculateGlobalTotal(Type.CONFIRMED));
        globalTotalsObject.put("deaths", calculateGlobalTotal(Type.DEATHS));
        globalTotalsObject.put("recovered", calculateGlobalTotal(Type.RECOVERIES));
        globalTotalsObject.put("deathsPercent",
                Precision.round(Long.valueOf((Long)globalTotalsObject.get("deaths")).doubleValue()/
                        Long.valueOf((Long)globalTotalsObject.get("confirmed")).doubleValue() *100 ,1));
        globalTotalsObject.put("countriesTotalData", getAllCountriesTotalData(type));
        globalTotalsObject.put("countriesTotalDataEachDay", getGlobalTotalEachDay());
        globalTotalsObject.put("individualCountryTotals", individualCountryTotals(20));
        globalTotalsObject.put("allCountryDataForEachDay", allCountryDataForEachDay(10, Type.CONFIRMED));
        globalTotalsObject.put("allCountryDataForEachDaySortByDeaths", allCountryDataForEachDay(10, Type.DEATHS));
        globalTotalsObject.put("countryNames", getCountryNamesSorted());


    }

    private long calculateCountryTotal(String countryName, Type type){
        List<CountryCases> selectedListType = getCountryCasesListForType(type);

        CountryCases cc = selectedListType.stream()
                .filter(f -> f.getCountry().equals(countryName))
                .findFirst()
                .get();

        return cc.getCasesByDate().get(formatLocalDate(LocalDate.now().minusDays(1)));
    }

    private CountryCases getCountryCaseByCountry(String countryName, Type type){
        CountryCases cc = null;

        switch (type){
            case CONFIRMED:
                cc = virusDataFetcherService.getConfirmedCasesList().stream()
                    .filter(f -> f.getCountry().equals(countryName))
                    .findFirst()
                    .get();
                break;
            case DEATHS:
                cc = virusDataFetcherService.getDeathCasesList().stream()
                    .filter(f -> f.getCountry().equals(countryName))
                    .findFirst()
                    .get();
                break;
            case RECOVERIES:
                cc = virusDataFetcherService.getRecoveredCasesList().stream()
                        .filter(f -> f.getCountry().equals(countryName))
                        .findFirst()
                        .get();
                break;
        }

        return cc;
    }


    private Map<String, List<Long>> getCountriesTotalDataEachDayForACountry(String countryName){
        Map<String, List<Long>> result = new HashMap<>();

        result.put("confirmed", new ArrayList<>());
        result.put("recovered", new ArrayList<>());
        result.put("deaths", new ArrayList<>());

        CountryCases cc = getCountryCaseByCountry(countryName, Type.CONFIRMED);

        dateKeys().stream()
                .forEach(f -> {
                    List<Long> list = result.get("confirmed");
                    list.add(cc.getCasesByDate().get(f));
                    result.put("confirmed", list);
                });

        CountryCases cc2 = getCountryCaseByCountry(countryName, Type.DEATHS);

        dateKeys().stream()
                .forEach(f -> {
                    List<Long> list = result.get("deaths");
                    list.add(cc2.getCasesByDate().get(f));
                    result.put("deaths", list);
                });


        CountryCases cc3 = getCountryCaseByCountry(countryName, Type.RECOVERIES);

        dateKeys().stream()
                .forEach(f -> {
                    List<Long> list = result.get("recovered");
                    list.add(cc3.getCasesByDate().get(f));
                    result.put("recovered", list);
                });

        return result;
    }

    public List<Long> getActiveCasesForCountry(String countryName){
        CountryCases confirmedCases = getCountryCaseByCountry(countryName, Type.CONFIRMED);
        CountryCases recoveredCases = getCountryCaseByCountry(countryName, Type.RECOVERIES);
        CountryCases deathsCases = getCountryCaseByCountry(countryName, Type.DEATHS);

        List<Long> result = new ArrayList<>();

        dateKeys().stream()
                .forEach(f ->  {
                    long activeTotalForDate = confirmedCases.getCasesByDate().get(f) - deathsCases.getCasesByDate().get(f) - recoveredCases.getCasesByDate().get(f);
                    result.add(activeTotalForDate);
                });

        return result;
    }

    public void byCountry(String countryName){
        initializeCountryData(countryName);
    }

    private void initializeCountryData(String countryName){
        countryTotalsObject.put("confirmed", calculateCountryTotal(countryName, Type.CONFIRMED));
        countryTotalsObject.put("deaths", calculateCountryTotal(countryName, Type.DEATHS));
        countryTotalsObject.put("recovered", calculateCountryTotal(countryName, Type.RECOVERIES));
        countryTotalsObject.put("deathsPercent",
                Precision.round(Long.valueOf((Long)countryTotalsObject.get("deaths")).doubleValue()/
                        Long.valueOf((Long)countryTotalsObject.get("confirmed")).doubleValue() *100 ,1));
        countryTotalsObject.put("countriesTotalDataEachDay", getCountriesTotalDataEachDayForACountry(countryName));
        countryTotalsObject.put("activeCasesPerDay", getActiveCasesForCountry(countryName));
        countryTotalsObject.put("countryNames", getCountryNamesSorted());

    }

    public List<String> dateKeys(){
        List<String> list = new ArrayList<String>(virusDataFetcherService.dateKeys());
        return list;
    }

}
