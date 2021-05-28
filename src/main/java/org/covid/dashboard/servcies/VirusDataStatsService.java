package org.covid.dashboard.servcies;

import org.covid.dashboard.model.Cases;
import org.covid.dashboard.model.CountryCaseData;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

@Service
public class VirusDataStatsService {

    @Autowired
    private VirusDataFetcherService virusDataFetcherService;

    private List<CountryCaseData> countryCaseDataList = new ArrayList<>();

    @PostConstruct
    public void populateCountryCaseDataList(){
        virusDataFetcherService.getConfirmedCasesList().stream()
        .forEach(c -> {
            CountryCaseData ccd = new CountryCaseData();
            ccd.setCountry(c.getCountry());
            ccd.setState(c.getState());
            ccd.getConfirmedCasesMap().put("currentDaysCases",c.getCurrentDaysCases());
            ccd.getConfirmedCasesMap().put("previousDaysCases",c.getPreviousDaysCases());
            countryCaseDataList.add(ccd);
        });

        for (Cases c: virusDataFetcherService.getDeathCasesList()) {
            for (CountryCaseData ccd: countryCaseDataList) {
                ccd.getDeathCasesMap().put("currentDaysCases",c.getCurrentDaysCases());
                ccd.getDeathCasesMap().put("previousDaysCases",c.getPreviousDaysCases());
                break;
            }
        }

        for (Cases c: virusDataFetcherService.getRecoveredCasesList()) {
            for (CountryCaseData ccd: countryCaseDataList) {
                ccd.getRecoveredCasesMap().put("currentDaysCases",c.getCurrentDaysCases());
                ccd.getRecoveredCasesMap().put("previousDaysCases",c.getPreviousDaysCases());
                break;
            }
        }
    }

    public long getGlobalTotalConfirmedCases(){
        return countryCaseDataList.stream()
                .map((ccd) -> ccd.getConfirmedCasesMap().get("currentDaysCases"))
                .reduce((a, b) -> a + b)
                .get();
    }

    public long getGlobalTotalDeathCases(){
        return countryCaseDataList.stream()
                .map((ccd) -> ccd.getConfirmedCasesMap().get("currentDaysCases"))
                .reduce((a, b) -> a + b)
                .get();
    }

    public long getGlobalTotalRecoveriesCases(){
        return virusDataFetcherService.getRecoveredCasesList().stream()
                .map((c) -> c.getCurrentDaysCases())
                .reduce((a, b) -> a +b)
                .get();
    }

    public long getGlobalTotalVaccinatedCases(){
        return virusDataFetcherService.getVaccinatedCasesList().stream()
                .map((c) -> c.getTotalNumberOfVaccinations())
                .reduce((a, b) -> a +b)
                .get();
    }

    public long getCountryTotalConfirmedCases(String countryName){
        return virusDataFetcherService.getConfirmedCasesList().stream()
                .filter(cn -> cn.getCountry().equalsIgnoreCase(countryName))
                .map((c) -> c.getCurrentDaysCases())
                .reduce((a, b) -> a + b)
                .get();
    }

    public long getCountryTotalDeathCases(String countryName){
        return virusDataFetcherService.getDeathCasesList().stream()
                .filter(cn -> cn.getCountry().equalsIgnoreCase(countryName))
                .map((c) -> c.getCurrentDaysCases())
                .reduce((a, b) -> a + b)
                .get();
    }

    public long getCountryTotalRecoverCases(String countryName){
        return virusDataFetcherService.getRecoveredCasesList().stream()
                .filter(cn -> cn.getCountry().equalsIgnoreCase(countryName))
                .map((c) -> c.getCurrentDaysCases())
                .reduce((a, b) -> a + b)
                .get();
    }

    public long getCountryTotalVaccinatedCases(String countryName){
        return virusDataFetcherService.getVaccinatedCasesList().stream()
                .filter(cn -> cn.getCountry().equalsIgnoreCase(countryName))
                .map((c) -> c.getTotalNumberOfVaccinations())
                .reduce((a, b) -> a + b)
                .get();
    }


}
