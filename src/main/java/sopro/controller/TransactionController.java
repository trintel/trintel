package sopro.controller;

import java.util.ArrayList;
import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import sopro.model.Action;
import sopro.model.ActionType;
import sopro.model.InitiatorType;
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

        //if(user.getCompany().getId() != companyID && user.getRole() != "ADMIN") {
        //    return "redirect:/transactions";
        //}

        Transaction newTransaction = new Transaction();
        //added by @philo to pre set the seller known by the id to print the name in the formular
        newTransaction.setSeller(companyRepository.findById(companyID).get());

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
    //TODO enums berücksichtigen
    @GetMapping("/transaction/{id}")
    public String transactionDetail(Model model, @PathVariable Long id, @AuthenticationPrincipal User user) {
        Action newAction = new Action();
        Transaction transaction = transactionRepository.findById(id).get();
        List<ActionType> actionTypes = new ArrayList<>();
        if(!user.getRole().equals("ADMIN")){
            InitiatorType initiatorType = InitiatorType.SELLER;
            if(user.getCompany().equals(transaction.getBuyer())) {      //findout if current user is Buyer or seller.
                initiatorType = InitiatorType.BUYER;
            }
            actionTypes = actionTypeRepository.findByInitiatorType(initiatorType);
        }
        model.addAttribute("actiontypes", actionTypes);     //only find the available actiontypes for that user.
        model.addAttribute("action", newAction);
        model.addAttribute("actions", actionRepository.findByTransaction(transaction));
        model.addAttribute("transactionID", id);
        return "transaction-view";

    }

    @GetMapping("/transaction/{transactionID}/addAction")
    public String showAction(Action action, @PathVariable Long transactionID, @AuthenticationPrincipal User user, Model model) {
        Action newAction = new Action();
        InitiatorType initiatorType = InitiatorType.SELLER;

        if(user.getCompany().equals(transactionRepository.findById(transactionID).get().getBuyer())) {      //findout if current user is Buyer or seller.
            initiatorType = InitiatorType.BUYER;
        }

        //a ArrayList for all available actions for the current Initiator
        List<ActionType> availableActions = new ArrayList<>();

        //check on all available actions to exclude the standart actions to only have the special actions
        for(int i = 0; i < actionTypeRepository.findByInitiatorType(initiatorType).size(); i++){
            if(actionTypeRepository.findByInitiatorType(initiatorType).get(i).equals(actionTypeRepository.findByName("Offer"))
            || actionTypeRepository.findByInitiatorType(initiatorType).get(i).equals(actionTypeRepository.findByName("Accept"))
            || actionTypeRepository.findByInitiatorType(initiatorType).get(i).equals(actionTypeRepository.findByName("Shipped"))
            || actionTypeRepository.findByInitiatorType(initiatorType).get(i).equals(actionTypeRepository.findByName("Paid"))
            || actionTypeRepository.findByInitiatorType(initiatorType).get(i).equals(actionTypeRepository.findByName("Completed"))
            || actionTypeRepository.findByInitiatorType(initiatorType).get(i).equals(actionTypeRepository.findByName("Invoicing"))
            || actionTypeRepository.findByInitiatorType(initiatorType).get(i).equals(actionTypeRepository.findByName("End"))){
            }
            else{
                //if the type is none of the standard actiontypes it will be transmitted to the List of available special actions
                availableActions.add(actionTypeRepository.findByInitiatorType(initiatorType).get(i));
            }
        }

        //add the list of special actions
        model.addAttribute("actiontypes", availableActions);

        model.addAttribute("action", newAction);
        model.addAttribute("transactionID", transactionID);
        return "transaction-addSpecialAction";
    }

    @GetMapping("/transaction/{transactionID}/addOffer")
    public String showOffer(Action action, @PathVariable Long transactionID, @AuthenticationPrincipal User user, Model model) {
        InitiatorType initiatorType = InitiatorType.SELLER;
        Action newAction = new Action();

        if(user.getCompany().equals(transactionRepository.findById(transactionID).get().getBuyer())) {      //findout if current user is Buyer or seller.
            initiatorType = InitiatorType.BUYER;
        }

        //set specific Actiontype Offer because in the case of an Offer Action it can only be the Offertype
        model.addAttribute("actiontypes", actionTypeRepository.findByName("Offer"));
        model.addAttribute("action", newAction);
        model.addAttribute("transactionID", transactionID);
        return "transaction-addOffer";
    }

    @PostMapping("/transaction/{transactionID}/addAction")
    public String createAction(Action action, @PathVariable Long transactionID, @AuthenticationPrincipal User user, Model model) {
        // ActionType actionType = actionTypeRepository.findByName(actionTypeName);
        // action.setActiontype(actionType);
        Transaction transaction = transactionRepository.findById(transactionID).get();

        if (action.getActiontype().getName().equals("ACCEPT")){
            transaction.setConfirmed(true);
        }else if(action.getActiontype().getName().equals("PAID")){
            transaction.setPaid(true);
        }

        action.setTransaction(transaction);
        action.setInitiator(user);
        actionRepository.save(action);


        return "redirect:/transaction/" + transactionID;
    }

    @GetMapping("/actions")
    public String showActions(Model model) {
        model.addAttribute("actionTypes", actionTypeRepository.findAll());
        return "action-list";
    }

    @GetMapping("/action/add")
    public String addAction(Model model) {
        ActionType actionType = new ActionType();
        model.addAttribute("actionType", actionType);
        model.addAttribute("initiatorTypes", InitiatorType.values());
        return "action-add";

    }

    @PostMapping("/action/save")
    public String saveAction(@Valid ActionType actionType, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("actionType", actionType);
            model.addAttribute("initiatorTypes", InitiatorType.values());
            return "action-add";
        }

        actionTypeRepository.save(actionType);

        return "redirect:/actions";
    }

    @GetMapping("action/edit/{actionTypeID}")
    public String editActionType(Model model, @PathVariable Long actionTypeID) {
        model.addAttribute("actionType", actionTypeRepository.findById(actionTypeID).get());
        model.addAttribute("initiatorTypes", InitiatorType.values());
        return "action-edit";
    }

    @PostMapping("action/edit/{actionTypeID}")
    public String editAction(@Valid ActionType actionType, @PathVariable Long actionTypeID, BindingResult bindingResult, Model model) {

        if (bindingResult.hasErrors()) {
            model.addAttribute("actionType", actionType);
            return "action-edit";
        }
        actionType.setId(actionTypeID);
        actionTypeRepository.save(actionType);
        return "redirect:/actions";

    }

}
