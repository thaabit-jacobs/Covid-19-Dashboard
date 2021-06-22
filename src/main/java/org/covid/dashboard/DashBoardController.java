package org.covid.dashboard;

import org.covid.dashboard.servcies.VirusDataStatsService;
import org.covid.dashboard.type.Type;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
public class DashBoardController {

    @Autowired
    private VirusDataStatsService virusDataStatsService;

    @GetMapping("/")
    public String redirectHome(){
        return "redirect:/dashboard?type=CONFIRMED";
    }

    @GetMapping("/dashboard")
    public String home(Model model, @RequestParam(name="type") String type){
        virusDataStatsService.filterType(Type.valueOf(type));

        model.addAttribute("countryName", "Global");

        Map<String,Object> globalTotalsObject = virusDataStatsService.getGlobalTotalsObject();

        model.addAttribute("confirmedTotal", globalTotalsObject.get("confirmed"));
        model.addAttribute("deathsTotal", globalTotalsObject.get("deaths"));
        model.addAttribute("recoveredTotal", globalTotalsObject.get("recovered"));

        model.addAttribute("deathsPercent", globalTotalsObject.get("deathsPercent"));

        model.addAttribute("countriesData", globalTotalsObject.get("countriesTotalData"));

        model.addAttribute("dateLabels", virusDataStatsService.dateKeys());
        model.addAttribute("countriesTotalDataEachDay", ((Map)(globalTotalsObject.get("countriesTotalDataEachDay"))));

        model.addAttribute("individualCountryTotalsLabels", ((Map)(globalTotalsObject.get("individualCountryTotals"))).keySet());
        model.addAttribute("individualCountryTotals", (Map)(globalTotalsObject.get("individualCountryTotals")));

        model.addAttribute("allCountryDataForEachDay", (Map)(globalTotalsObject.get("allCountryDataForEachDay")));

        model.addAttribute("allCountryDataForEachDaySortByDeaths", (Map)(globalTotalsObject.get("allCountryDataForEachDaySortByDeaths")));

        model.addAttribute("countryNames", (List)(globalTotalsObject.get("countryNames")));
        return "index";
    }

    @GetMapping("/dashboard/{country}")
    public String displayCountryStats(Model model, @PathVariable("country") String country){
        virusDataStatsService.byCountry(country);

        model.addAttribute("countryName", country);

        Map<String,Object> countryTotalsObject = virusDataStatsService.getCountryTotalsObject();

        model.addAttribute("dateLabels", virusDataStatsService.dateKeys());

        model.addAttribute("confirmedTotal", countryTotalsObject.get("confirmed"));
        model.addAttribute("deathsTotal", countryTotalsObject.get("deaths"));
        model.addAttribute("recoveredTotal", countryTotalsObject.get("recovered"));
        model.addAttribute("deathsPercent", countryTotalsObject.get("deathsPercent"));

        model.addAttribute("countriesTotalDataEachDay", ((Map)(countryTotalsObject.get("countriesTotalDataEachDay"))));

        model.addAttribute("activeCasesPerDay", countryTotalsObject.get("activeCasesPerDay"));

        model.addAttribute("countryNames", (List)(countryTotalsObject.get("countryNames")));

        return "country";
    }
}
