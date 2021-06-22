package org.covid.dashboard.model;

import lombok.*;

import java.util.HashMap;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class VaccinatedCases{
    private String country;
    private Map<String, Long> vaccinationsByDate = new HashMap<>();;
}
