package sopro.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import sopro.repository.ActionRepository;
import sopro.repository.ActionTypeRepository;
import sopro.repository.CompanyRepository;
import sopro.repository.TransactionRepository;
import sopro.repository.UserRepository;
import sopro.model.Action;
import sopro.model.Company;
import sopro.model.Transaction;
import sopro.model.User;
import org.springframework.ui.Model;

@Controller
public class StatisticController {
    
    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    ActionRepository actionRepository;

    @Autowired
    ActionTypeRepository actionTypeRepository;



    @GetMapping("/statistics")
    public String showStatistics(Model model, @AuthenticationPrincipal User user){
        
        if(user.getRole().equals("ADMIN")){
            
        }else{
            int amount = 0;
            double totalCost = 0;
            List<Transaction> transactions = new ArrayList<>();
            List <Action> actions = new ArrayList<>();
            transactions.addAll(transactionRepository.findByBuyer(user.getCompany()));
            transactions.addAll(transactionRepository.findBySeller(user.getCompany()));
            for (Transaction transaction : transactions) {
                actions.addAll(transaction.getActions());
            }
            for (Action action : actions) {
                amount += action.getAmount();
                totalCost += action.getPricePerPiece()*action.getAmount();
            }
            model.addAttribute("amount",amount);
            model.addAttribute("totalCost", totalCost);
        }
        
        return "statistics";
    }
}
