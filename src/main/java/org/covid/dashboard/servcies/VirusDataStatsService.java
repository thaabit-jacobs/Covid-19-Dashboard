package org.covid.dashboard.servcies;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class VirusDataStatsService {

    @Autowired
    private VirusDataFetcherService virusDataFetcherService;

    public long getGlobalTotalConfirmedCases(){
        return virusDataFetcherService.getConfirmedCasesList().stream()
                .map((c) -> c.getCurrentDaysCases())
                .reduce((a, b) -> a + b)
                .get();
    }

    public long getGlobalTotalDeathCases(){
        return virusDataFetcherService.getDeathCasesList().stream()
                .map((c) -> c.getCurrentDaysCases())
                .reduce((a, b) -> a +b)
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
