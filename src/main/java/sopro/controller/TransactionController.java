package sopro.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.ui.Model;

import sopro.model.Transaction;
import sopro.model.User;
import sopro.repository.ActionRepository;
import sopro.repository.ActionTypeRepository;
import sopro.repository.TransactionRepository;

@Controller
public class TransactionController {

    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    ActionRepository actionRepository;
    @Autowired
    ActionTypeRepository actionTypeRepository;

    @GetMapping("/transactions")
    public String listTransactions(Model model, @AuthenticationPrincipal User user) {
        if (user.getRole().equals("ADMIN")) {   //Admins can see all transactions 
            model.addAttribute("transactions", transactionRepository.findAll());
        }
        if (user.getRole().equals("STUDENT")) { //Students can only see transactions, where they are involved
            List<Transaction> transactions = new ArrayList<>();
            transactions.addAll(transactionRepository.findByBuyer(user.getCompany()));
            transactions.addAll(transactionRepository.findBySeller(user.getCompany()));
            model.addAttribute("transactions", transactions);
        }
        return "transactions-list";

    }
    
    //TODO rechte einstellen 
    @GetMapping("/transaction/{id}")
    public String transactionDetail(Model model, @PathVariable Long id) {
        model.addAttribute("actions", actionRepository.findByTransaction(transactionRepository.findById(id).get()));
        return "transaction-view";
    }
    
}