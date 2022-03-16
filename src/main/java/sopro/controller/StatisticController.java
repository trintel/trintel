package sopro.controller;



import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
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

    @PreAuthorize("isInCompany(#companyID)")
    @GetMapping("/statistics/{companyID}")
    public String showStatistics(Model model, @AuthenticationPrincipal User user, @PathVariable Long companyID){

        Company company = companyRepository.findById(companyID).get();      //user has to be in the correct company because of preauthorize.
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

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/admin-statistics")
    public String showAdminStatistics(Model model) {

        List<Company> companies = companyRepository.findAll();
        
        model.addAttribute("companies", companies);
        
        // List<Long> distinctBuyers = companies.stream().map(c -> statisticsService.getNumberDistinctBuyers(c)).collect(Collectors.toList());

        model.addAttribute("numberDistinctBuyers", companies.stream().map(c -> statisticsService.getNumberDistinctBuyers(c)).collect(Collectors.toList()));
        model.addAttribute("numberDistinctSellers", companies.stream().map(c -> statisticsService.getNumberDistinctSellers(c)).collect(Collectors.toList()));
        model.addAttribute("totalTransationBuyerVolume", companies.stream().map(c -> statisticsService.getTotalTransactionBuyerVolume(c)).collect(Collectors.toList()));
        model.addAttribute("totalTransationSellerVolume", companies.stream().map(c -> statisticsService.getTotalTransactionSellerVolume(c)).collect(Collectors.toList()));
        model.addAttribute("numberNonConfirmedBuyer", companies.stream().map(c -> statisticsService.getNumberNonConfirmedTransactionBuyer(c)).collect(Collectors.toList()));
        model.addAttribute("numberNonConfirmedSeller", companies.stream().map(c -> statisticsService.getNumberNonConfirmedTransactionSeller(c)).collect(Collectors.toList()));
        model.addAttribute("numberConfirmed", companies.stream().map(c -> statisticsService.getNumberConfirmedTransactions(c)).collect(Collectors.toList()));

        return "statistics";
    }
}
