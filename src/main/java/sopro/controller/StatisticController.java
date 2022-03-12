package sopro.controller;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import sopro.model.Company;
import sopro.model.User;
import sopro.repository.CompanyRepository;
import sopro.repository.UserRepository;
import sopro.service.StatisticsService;

@Controller
public class StatisticController {

    @Autowired
    UserRepository userRepository;

    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    StatisticsService statisticsService;


    @GetMapping("/statistics/{companyID}")
    public String showStatistics(Model model, @AuthenticationPrincipal User user, @PathVariable Long companyID){

        if(user.getRole().equals("STUDENT")) {  //TODO Admin
            if(user.getCompany().getId() != companyID) {
                return "redirect:/home";        //not allowed
            }
        }

        Company company = companyRepository.findById(companyID).get();      //TODO: deal with possibilty of non existing company
        model.addAttribute("company", company);


        model.addAttribute("numberDistinctBuyers", statisticsService.getNumberDistinctBuyers(company));
        model.addAttribute("numberDistinctSellers", statisticsService.getNumberDistinctSellers(company));
        model.addAttribute("totalTransationBuyerVolume", statisticsService.getTotalTransactionBuyerVolume(company));
        model.addAttribute("totalTransationSellerVolume", statisticsService.getTotalTransactionSellerVolume(company));
        model.addAttribute("numberNonConfirmedBuyer", statisticsService.getNumberNonConfirmedTransactionBuyer(company));
        model.addAttribute("numberNonConfirmedSeller", statisticsService.getNumberNonConfirmedTransactionSeller(company));
        model.addAttribute("numberConfirmed", statisticsService.getNumberConfirmedTransactions(company));

        return "statistics";
    }
    
    @GetMapping("/statistic")
    public String viewOwnCompany(Model model, @AuthenticationPrincipal User user) {
        return "redirect:/statistics/" + user.getCompany().getId();
    }
}
