package org.covid.dashboard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class VaccinatedCases{
    private String country;
    private String state;
    private long totalNumberOfVaccinations;
}
