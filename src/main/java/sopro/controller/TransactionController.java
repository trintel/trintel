package sopro.controller;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

import sopro.model.Action;
import sopro.model.ActionType;
import sopro.model.Transaction;
import sopro.model.User;
import sopro.model.util.InitiatorType;
import sopro.repository.ActionRepository;
import sopro.repository.ActionTypeRepository;
import sopro.repository.CompanyRepository;
import sopro.repository.TransactionRepository;
import sopro.service.ActionTypeService;

@Controller
public class TransactionController {

    @Autowired
    TransactionRepository transactionRepository;
    @Autowired
    ActionRepository actionRepository;
    @Autowired
    ActionTypeRepository actionTypeRepository;      //TODO maybe own Controller
    @Autowired
    CompanyRepository companyRepository;
    @Autowired
    ActionTypeService actionTypeService;

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


    @PreAuthorize("hasRole('STUDENT')")
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

    @PreAuthorize("hasRole('STUDENT')")
    @PostMapping("/transaction/{companyID}/save")
    public String createTransaction(Action action, Transaction transaction, @PathVariable Long companyID, @AuthenticationPrincipal User user, Model model) {

        transaction.setBuyer(user.getCompany());
        transaction.setSeller(companyRepository.findById(companyID).get());

        action.setInitiator(user);
        action.setTransaction(transaction);
        // action.setActiontype(actionTypeRepository.findByName("Request"));
        action.setActiontype(actionTypeService.getInitialActionType());

        transactionRepository.save(transaction);
        actionRepository.save(action);

        return "redirect:/transactions";

    }
    //TODO rechte einstellen
    //TODO enums berücksichtigen
    @PreAuthorize("hasPermission(#id, 'transaction') and hasRole('STUDENT')")
    @GetMapping("/transaction/{id}")
    public String transactionDetail(Model model, @PathVariable Long id, @AuthenticationPrincipal User user) {
        Action newAction = new Action();
        Transaction transaction = transactionRepository.findById(id).get();
        // List<ActionType> actionTypes = new ArrayList<>();
        // if(!user.getRole().equals("ADMIN")){
        //     InitiatorType initiatorType = InitiatorType.SELLER;
        //     if(user.getCompany().equals(transaction.getBuyer())) {      //findout if current user is Buyer or seller.
        //         initiatorType = InitiatorType.BUYER;
        //     }
        //     actionTypes = actionTypeRepository.findByInitiatorType(initiatorType);
        // }
        List<ActionType> actionTypes = actionTypeService.getAvailableActions(transaction, user);
        model.addAttribute("actiontypes", actionTypes);     //only find the available actiontypes for that user.
        model.addAttribute("action", newAction);
        model.addAttribute("actions", actionRepository.findByTransaction(transaction));
        model.addAttribute("specialActionsAvailable", actionTypes.stream().filter(t -> t.isStandardAction()).toArray(ActionType[] :: new).length > 0); //get the info if there are specialActions.
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
        //List<ActionType> actionTypes  = new ArrayList<>();
        //actionTypes = actionTypeRepository.findByInitiatorType(initiatorType);

        //add the list of special actions
        model.addAttribute("actiontypes", actionTypeRepository.findByInitiatorType(initiatorType));
        model.addAttribute("action", newAction);
        model.addAttribute("transactionID", transactionID);
        return "transaction-addSpecialAction";
    }

    //TODO: Security
    @GetMapping("/transaction/{transactionID}/addOffer")
    public String showOffer(@PathVariable Long transactionID, Model model) {
        Action newAction = new Action();

        model.addAttribute("action", newAction);
        model.addAttribute("transactionID", transactionID);
        return "transaction-addOffer";
    }

    //TODO: Security
    @PostMapping("/transaction/{transactionID}/addOffer")
    public String addOffer(Action offer, @PathVariable Long transactionID, @AuthenticationPrincipal User user, BindingResult bindingResult, Model model) {
        if(bindingResult.hasErrors()) {
            model.addAttribute("action", offer);
            model.addAttribute("transactionID", transactionID);
            return "transaction-addOffer";
        }
        offer.setActiontype(actionTypeService.getOfferAction());        //to be sure.
        offer.setTransaction(transactionRepository.findById(transactionID).get());
        offer.setInitiator(user);
        actionRepository.save(offer);       //save the new offer.
        return "redirect:/transaction/" + transactionID;
    }

    @PreAuthorize("hasRole('STUDENT')")
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

    @PostMapping("/transaction/{transactionID}/accept")
    public String createAcceptAction(String message, @PathVariable Long transactionID, @AuthenticationPrincipal User user) {
        Transaction transaction = transactionRepository.findById(transactionID).get();
        Action accept = new Action(message, actionTypeService.getAcceptActionType(), transaction);
        transaction.setConfirmed(true);
        accept.setInitiator(user);
        actionRepository.save(accept);
        transactionRepository.save(transaction);
        return "redirect:/transaction/" + transactionID;
    }

    @PostMapping("/transaction/{transactionID}/cancel")
    public String createCancelAction(String message, @PathVariable Long transactionID, @AuthenticationPrincipal User user) {
        Transaction transaction = transactionRepository.findById(transactionID).get();
        Action cancel = new Action(message, actionTypeService.getAbortActionType(), transaction);
        transaction.setActive(false);
        cancel.setInitiator(user);
        actionRepository.save(cancel);
        transactionRepository.save(transaction);
        return "redirect:/transaction/" + transactionID;
    }

    @PostMapping("/transaction/{transactionID}/delivery")
    public String createDeliveryAction(String message, @PathVariable Long transactionID, @AuthenticationPrincipal User user) {
        Transaction transaction = transactionRepository.findById(transactionID).get();
        Action delivered = new Action(message, actionTypeService.getDeliveryActionType(), transaction);
        transaction.setShipped(true);
        delivered.setInitiator(user);
        actionRepository.save(delivered);
        transactionRepository.save(transaction);
        return "redirect:/transaction/" + transactionID;
    }

    @PostMapping("/transaction/{transactionID}/invoicing")
    public String createInvoiceAction(String message, @PathVariable Long transactionID, @AuthenticationPrincipal User user) {
        Transaction transaction = transactionRepository.findById(transactionID).get();
        Action invoice = new Action(message, actionTypeService.getInvoiceActionType(), transaction);
        invoice.setInitiator(user);
        actionRepository.save(invoice);
        return "redirect:/transaction/" + transactionID;
    }

    @PostMapping("/transaction/{transactionID}/paid")
    public String createPaidAction(String message, @PathVariable Long transactionID, @AuthenticationPrincipal User user) {
        Transaction transaction = transactionRepository.findById(transactionID).get();
        Action paid = new Action(message, actionTypeService.getPaidActionType(), transaction);
        transaction.setPaid(true);
        paid.setInitiator(user);
        actionRepository.save(paid);
        transactionRepository.save(transaction);
        return "redirect:/transaction/" + transactionID;
    }


}
