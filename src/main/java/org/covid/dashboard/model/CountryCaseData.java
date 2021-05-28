package org.covid.dashboard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class CountryCaseData {

    private String country;
    private String state;
    private Map<String, Long> confirmedCasesMap;
    private Map<String, Long> recoveredCasesMap;
    private Map<String, Long> deathCasesMap;

    public CountryCaseData(){
        this.confirmedCasesMap = new HashMap<>();
        this.recoveredCasesMap = new HashMap<>();
        this.deathCasesMap = new HashMap<>();
    }
}
