package org.covid.dashboard.model;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@AllArgsConstructor
@ToString
public class Cases {

    private String country;
    private String state;
    private long currentDaysCases;
    private long previousDaysCases;

}
