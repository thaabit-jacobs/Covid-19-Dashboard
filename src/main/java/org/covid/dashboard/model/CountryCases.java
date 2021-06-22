package org.covid.dashboard.model;

import lombok.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
@Setter
@NoArgsConstructor
@ToString
public class CountryCases {
        private String country;
        private Map<String, Long> casesByDate = new HashMap<>();
}
