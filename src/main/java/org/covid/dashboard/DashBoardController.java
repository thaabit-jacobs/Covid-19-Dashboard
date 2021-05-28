package org.covid.dashboard;

import org.covid.dashboard.servcies.VirusDataStatsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
public class DashBoardController {

    @Autowired
    private VirusDataStatsService virusDataStatsService;

    @GetMapping("/")
    public String home(Model model){
        model.addAttribute("total", virusDataStatsService.getGlobalTotalConfirmedCases());
        model.addAttribute("death", virusDataStatsService.getGlobalTotalDeathCases());
/*        model.addAttribute("recover", virusDataStatsService.getGlobalTotalRecoveriesCases());
        model.addAttribute("confirmedCases", virusDataStatsService.getCountryTotalConfirmedCases("South Africa"));
        model.addAttribute("deathCases", virusDataStatsService.getCountryTotalDeathCases("South Africa"));
        model.addAttribute("recoveredCases", virusDataStatsService.getCountryTotalRecoverCases("South Africa"));
        model.addAttribute("vacc", virusDataStatsService.getGlobalTotalVaccinatedCases());
        model.addAttribute("vaccCo", virusDataStatsService.getCountryTotalVaccinatedCases("South Africa"));*/
        return "index";
    }
}
