package org.covid.dashboard.model;

import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class CasesTwo {
        private String country;
        private String state;
        private List<Long> currentDaysCases = new ArrayList<>();
}
