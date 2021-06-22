package org.covid.dashboard.util;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class DateFormatter {
    public String getFormattedDateHeaderColumnsForDate(int day){
        LocalDate dateforDay = LocalDate.now().minusDays(day);

        String formattedDate = dateforDay.format(DateTimeFormatter.ofPattern("M/d/yy")).toString();

        return formattedDate;
    }

    public static String formatLocalDate(LocalDate day){
        String formattedDate = day.format(DateTimeFormatter.ofPattern("M/d/yy")).toString();

        return formattedDate;
    }
}
