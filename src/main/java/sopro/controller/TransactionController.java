package sopro.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.ui.Model;

import sopro.model.Action;
import sopro.model.Transaction;
import sopro.model.User;
import sopro.repository.ActionRepository;
import sopro.repository.ActionTypeRepository;
import sopro.repository.CompanyRepository;
import sopro.repository.TransactionRepository;

@Controller
public class TransactionController {

    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    ActionRepository actionRepository;
    @Autowired
    ActionTypeRepository actionTypeRepository;
    @Autowired
    CompanyRepository companyRepository;

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
    
    
    @GetMapping("/transaction/{companyID}/create")
    public String createTransaction(@PathVariable Long companyID, @AuthenticationPrincipal User user, Model model) {
        
        Transaction newTransaction = new Transaction();
        
        Action newAction = new Action();
        model.addAttribute("companyID", companyID);
        model.addAttribute("action", newAction);
        model.addAttribute("transaction", newTransaction);
        return "transaction-add";
        
    }
    
    @PostMapping("/transaction/{companyID}/save")
    public String createTransaction(Action action, Transaction transaction, @PathVariable Long companyID, @AuthenticationPrincipal User user, Model model) {
        
        transaction.setBuyer(user.getCompany());
        transaction.setSeller(companyRepository.findById(companyID).get());
        
        action.setInitiator(user);
        action.setTransaction(transaction);
        action.setActiontype(actionTypeRepository.findByName("Request"));
        
        transactionRepository.save(transaction);
        actionRepository.save(action);
        
        return "redirect:/transactions";
        
    }
    //TODO rechte einstellen 
    @GetMapping("/transaction/{id}")
    public String transactionDetail(Model model, @PathVariable Long id) {
        Action newAction = new Action();
        model.addAttribute("actiontypes", actionTypeRepository.findAll());
        model.addAttribute("action", newAction);
        model.addAttribute("actions", actionRepository.findByTransaction(transactionRepository.findById(id).get()));
        model.addAttribute("transactionID", id);
        return "transaction-view";
    }
    
    @PostMapping("/transaction/{transactionID}/addAction")
    public String createAction(Action action, @PathVariable Long transactionID, @AuthenticationPrincipal User user, Model model) {
        // ActionType actionType = actionTypeRepository.findByName(actionTypeName);

        // action.setActiontype(actionType);
        action.setTransaction(transactionRepository.findById(transactionID).get());
        action.setInitiator(user);
        
        actionRepository.save(action);
        

        return "redirect:/transaction/" + transactionID;

    }
    
}