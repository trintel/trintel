package sopro.controller;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class SatisticController {


    /**
     * GET routing für Index.
     *
     * @return statistic page
     */
    @GetMapping("/statistics")
    public String showStatistic() {
        return "statistic-viewCompany";
    }

}