package org.covid.dashboard.model;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CountryCasesWithAllData{
    private String country;
    private long confirmedCases;
    private long deathCases;
    private long recoveredCases;
    private double deathsPercents;
    private double recoveryPercents;
}
