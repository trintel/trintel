package sopro.controller;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import sopro.model.Action;
import sopro.model.ActionType;
import sopro.model.AttachedFile;
import sopro.model.Company;
import sopro.model.Rating;
import sopro.model.Transaction;
import sopro.model.User;
import sopro.repository.ActionRepository;
import sopro.repository.ActionTypeRepository;
import sopro.repository.CompanyRepository;
import sopro.repository.RatingRepository;
import sopro.repository.TransactionRepository;
import sopro.service.ActionTypeService;
import sopro.service.AttachedFileInterface;

@Controller
public class TransactionController {

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    ActionRepository actionRepository;

    @Autowired
    ActionTypeRepository actionTypeRepository;
    // TODO maybe own Controller
    @Autowired
    CompanyRepository companyRepository;

    @Autowired
    RatingRepository ratingRepository;

    @Autowired
    ActionTypeService actionTypeService;

    @Autowired
    AttachedFileInterface attachedFileService;

    //TODO: write a service for the transactions, because this controller should not handle that much logic.

    @GetMapping("/transactions")
    public String listTransactions(Model model, @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "true") boolean sortByNewest, @RequestParam(required = false) Long status,
            @RequestParam(defaultValue = "") String buyer, @RequestParam(defaultValue = "") String seller) {
        List<Transaction> transactions = new ArrayList<>();
        if (user.getRole().equals("ADMIN")) { // Admins can see all transactions
            transactions = transactionRepository.findAllByBuyerAndSellerName(buyer, seller);
        }
        if (user.getRole().equals("STUDENT")) { // Students can only see transactions, where they are involved
            transactions = transactionRepository.findOwnByBuyerAndSellerName(buyer, seller, user.getCompany().getId());
        }
        if (status != null) {
            transactions = transactions.stream()
                    .filter(transaction -> transaction.getLastAction().getActiontype().getId().equals(status))
                    .collect(Collectors.toList());
        }
        // Sort transactions.
        if (sortByNewest) {
            transactions = transactions.stream().sorted(Comparator.comparing(Transaction::getLatestActionDate)
                    .thenComparing(Transaction::getLatestActionTime).reversed())
                    .collect(Collectors.toList());
        } else {
            transactions = transactions.stream().sorted(Comparator.comparing(Transaction::getLatestActionDate)
                    .thenComparing(Transaction::getLatestActionTime))
                    .collect(Collectors.toList());
        }
        model.addAttribute("transactions", transactions);
        model.addAttribute("actiontypes", actionTypeRepository.findAll());
        return "transactions-list";

    }

    @PreAuthorize("hasCompany()")
    @GetMapping("/transaction/{companyID}/create")
    public String createTransaction(@PathVariable Long companyID, @AuthenticationPrincipal User user, Model model) {

        // if(user.getCompany().getId() != companyID && user.getRole() != "ADMIN") {
        // return "redirect:/transactions";
        // }

        Transaction newTransaction = new Transaction();
        // added by @philo to pre set the seller known by the id to print the name in
        // the formular
        newTransaction.setSeller(companyRepository.findById(companyID).get());

        Action newAction = new Action();
        model.addAttribute("companyID", companyID);
        model.addAttribute("action", newAction);
        model.addAttribute("transaction", newTransaction);
        return "transaction-add";

    }

    @PreAuthorize("hasCompany()")
    @PostMapping("/transaction/{companyID}/save")
    public String createTransaction(Action action, Transaction transaction, @PathVariable Long companyID,
            @AuthenticationPrincipal User user, @RequestParam("attachment") MultipartFile[] attachments, Model model) {

        transaction.setBuyer(user.getCompany());
        transaction.setSeller(companyRepository.findById(companyID).get());

        action.setInitiator(user);
        action.setTransaction(transaction);
        // action.setActiontype(actionTypeRepository.findByName("Request"));
        action.setActiontype(actionTypeService.getInitialActionType());

        transactionRepository.save(transaction);
        actionRepository.save(action);

        if (attachments.length > 0) {
            List<AttachedFile> attachedFiles = attachedFileService.storeFiles(Arrays.asList(attachments), action);
            action.setAttachedFiles(attachedFiles);
        } else {
            return "redirect:/transaction/" + companyID + "/create";
        }

        return "redirect:/transactions";

    }

    @PreAuthorize("hasCompany()")
    @GetMapping("/transaction/{companyID}/create/skip")
    public String createTransactionSkip(@PathVariable Long companyID, @AuthenticationPrincipal User user, Model model) {

        Transaction newTransaction = new Transaction();
        newTransaction.setSeller(companyRepository.findById(companyID).get());

        Action newAction = new Action();
        Iterable<ActionType> altActionTypesIter = actionTypeRepository.findAll();

        List<ActionType> altActionTypes = new ArrayList<ActionType>();
        for (ActionType actionType : altActionTypesIter) {
            if (!actionType.equals(actionTypeService.getInitialActionType())
                    && !actionType.equals(actionTypeService.getAbortActionType()) && actionType.isStandardAction()) {
                altActionTypes.add(actionType);
            }
        }
        model.addAttribute("actionTypes", actionTypeService);
        model.addAttribute("altActionTypes", altActionTypes);
        model.addAttribute("companyID", companyID);
        model.addAttribute("action", newAction);
        model.addAttribute("transaction", newTransaction);
        return "transaction-add-skip";

    }

    @PreAuthorize("hasCompany()")
    @GetMapping("/transaction/{companyID}/create/skip/{actionTypeID}")
    public String createTransactionSkipSave(Action action, Transaction transaction, @PathVariable Long companyID,
            @PathVariable Long actionTypeID,
            @AuthenticationPrincipal User user, Model model) {
        model.addAttribute("companyID", companyID);
        model.addAttribute("skip", true);
        model.addAttribute("actionTypeID", actionTypeID);
        model.addAttribute("actionTypes", actionTypeService);
        if (actionTypeService.getDeliveryActionType().getId().equals(actionTypeID)
                || actionTypeService.getInvoiceActionType().getId().equals(actionTypeID)
                || actionTypeService.getPaidActionType().getId().equals(actionTypeID)) {
            model.addAttribute("roleRestricted", true);
        }
        return "transaction-addAction";
    }

    @PreAuthorize("hasCompany()")
    @PostMapping("/transaction/{companyID}/create/skip/{actionTypeID}")
    public String createTransactionSkipAddOffer(Action action, @RequestParam("attachment") MultipartFile[] attachments,
            @RequestParam String product, boolean isBuyer, @PathVariable Long companyID,
            @PathVariable Long actionTypeID, @AuthenticationPrincipal User user,
            BindingResult bindingResult, Model model) {
        Transaction transaction = new Transaction();
        transaction.setProduct(product);
        if (isBuyer) {
            transaction.setBuyer(user.getCompany());
            transaction.setSeller(companyRepository.getById(companyID));
        } else {
            transaction.setBuyer(companyRepository.getById(companyID));
            transaction.setSeller(user.getCompany());
        }

        transactionRepository.save(transaction);
        if (actionTypeService.getOfferAction().getId().equals(actionTypeID)) {
            addOffer(action, attachments, transaction.getId(), user, bindingResult, model);
        } else if (actionTypeService.getAcceptActionType().getId().equals(actionTypeID)) {
            transaction.setConfirmed(true);
            createAcceptAction(action, attachments, transaction.getId(), user);
        } else if (actionTypeService.getDeliveryActionType().getId().equals(actionTypeID)) {
            transaction.setConfirmed(true);
            createDeliveryAction(action, transaction.getId(), attachments, user);
        } else if (actionTypeService.getInvoiceActionType().getId().equals(actionTypeID)) {
            transaction.setConfirmed(true);
            transaction.setShipped(true);
            createInvoiceAction(action, transaction.getId(), attachments, user);
        } else if (actionTypeService.getPaidActionType().getId().equals(actionTypeID)) {
            transaction.setConfirmed(true);
            transaction.setShipped(true);
            createPaidAction(action, transaction.getId(), attachments, user);
        }
        return "redirect:/transaction/" + transaction.getId();
    }

    @PreAuthorize("hasPermission(#id, 'transaction')")
    @GetMapping("/transaction/{id}")
    public String transactionDetail(Model model, @PathVariable Long id, @AuthenticationPrincipal User user) {
        Action newAction = new Action();
        Transaction transaction = transactionRepository.findById(id).get();

        List<ActionType> actionTypes = new ArrayList<>();
        if (user.getRole().equals("STUDENT")) {
            actionTypes = actionTypeService.getAvailableActions(transaction, user);
        }
        model.addAttribute("actiontypes", actionTypes); // only find the available actiontypes for that user.
        model.addAttribute("action", newAction);
        model.addAttribute("actions", actionRepository.findByTransaction(transaction));
        model.addAttribute("specialActionsAvailable",
                actionTypes.stream().filter(t -> !t.isStandardAction()).toArray(ActionType[]::new).length > 0); // get
                                                                                                                // the
                                                                                                                // info
                                                                                                                // if
                                                                                                                // there
                                                                                                                // are
                                                                                                                // specialActions.
        model.addAttribute("transactionID", id);
        model.addAttribute("isAlreadyReviewed",
                ratingRepository.existsByTransactionAndRatingCompany(transaction, user.getCompany()));
        return "transaction-view";

    }

    // TODO only othorize if user is seller/buyer in resprct to
    // actiontype.initiatorType.
    @PreAuthorize("hasPermission(#transactionID, 'transaction') and hasRole('STUDENT')")
    @GetMapping("/transaction/{transactionID}/addAction")
    public String showAction(Action action, @PathVariable Long transactionID, @AuthenticationPrincipal User user,
            Model model) {
        Action newAction = new Action();
        Transaction transaction = transactionRepository.findById(transactionID).get();

        // add the list of special actions
        model.addAttribute("actiontypes", actionTypeService.getAvailableActions(transaction, user));
        model.addAttribute("action", newAction);
        model.addAttribute("transactionID", transactionID);
        model.addAttribute("lastAction", transaction.getLastAction());
        return "transaction-addSpecialAction";
    }

    @PreAuthorize("hasPermission(#transactionID, 'transaction') and hasRole('STUDENT')")
    @GetMapping("/transaction/{transactionID}/addOffer")
    public String showOffer(@PathVariable Long transactionID, Model model) {
        Action newAction = new Action();
        Transaction transaction = transactionRepository.findById(transactionID).get();
        newAction.setAmount(transaction.getAmount());
        newAction.setPricePerPiece(transaction.getPricePerPiece());

        model.addAttribute("action", newAction);
        model.addAttribute("transactionID", transactionID);
        model.addAttribute("transaction", transaction);
        model.addAttribute("lastAction", transaction.getLastAction());
        return "transaction-addOffer";
    }

    @PreAuthorize("hasPermission(#transactionID, 'transaction') and hasRole('STUDENT')")
    @PostMapping("/transaction/{transactionID}/addOffer")
    public String addOffer(Action offer, @RequestParam("attachment") MultipartFile[] attachments,
            @PathVariable Long transactionID, @AuthenticationPrincipal User user,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("action", offer);
            model.addAttribute("transactionID", transactionID);
            return "transaction-addOffer";
        }

        offer.setActiontype(actionTypeService.getOfferAction()); // to be sure.
        offer.setTransaction(transactionRepository.findById(transactionID).get());
        offer.setInitiator(user);
        actionRepository.save(offer); // save the new offer.

        if (attachments.length > 0) {
            List<AttachedFile> attachedFiles = attachedFileService.storeFiles(Arrays.asList(attachments), offer);
            offer.setAttachedFiles(attachedFiles);
        }

        return "redirect:/transaction/" + transactionID;
    }

    @PreAuthorize("hasPermission(#transactionID, 'transaction') and hasRole('STUDENT')")
    @PostMapping("/transaction/{transactionID}/addAction")
    public String createAction(Action action, MultipartFile[] attachments, @PathVariable Long transactionID,
            @AuthenticationPrincipal User user,
            Model model) {

        Transaction transaction = transactionRepository.findById(transactionID).get();

        if (action.getActiontype().getName().equals("ACCEPT")) {
            transaction.setConfirmed(true);
        } else if (action.getActiontype().getName().equals("PAID")) {
            transaction.setPaid(true);
        }

        action.setTransaction(transaction);
        action.setInitiator(user);
        actionRepository.save(action);

        if (attachments.length > 0) {
            List<AttachedFile> attachedFiles = attachedFileService.storeFiles(Arrays.asList(attachments), action);
            action.setAttachedFiles(attachedFiles);
        }

        return "redirect:/transaction/" + transactionID;
    }

    @PreAuthorize("hasPermission(#transactionID, 'transaction') and hasRole('STUDENT')")
    @PostMapping("/transaction/{transactionID}/accept")
    public String createAcceptAction(Action accept, @RequestParam("attachment") MultipartFile[] attachments,
            @PathVariable Long transactionID,
            @AuthenticationPrincipal User user) {

        Transaction transaction = transactionRepository.findById(transactionID).get();
        transaction.setConfirmed(true);
        accept.setInitiator(user);
        accept.setActiontype(actionTypeService.getAcceptActionType());
        accept.setTransaction(transaction);
        actionRepository.save(accept);
        transactionRepository.save(transaction);
        if (attachments.length > 0) {
            List<AttachedFile> attachedFiles = attachedFileService.storeFiles(Arrays.asList(attachments), accept);
            accept.setAttachedFiles(attachedFiles);
        }
        return "redirect:/transaction/" + transactionID;
    }

    @PreAuthorize("hasPermission(#transactionID, 'transaction') and hasRole('STUDENT')")
    @PostMapping("/transaction/{transactionID}/cancel")
    public String createCancelAction(String message, @PathVariable Long transactionID,
            @AuthenticationPrincipal User user) {
        Transaction transaction = transactionRepository.findById(transactionID).get();
        Action cancel = new Action(message, actionTypeService.getAbortActionType(), transaction, null); // TODO !!
        transaction.setActive(false);
        cancel.setInitiator(user);
        actionRepository.save(cancel);
        transactionRepository.save(transaction);
        return "redirect:/transaction/" + transactionID;
    }

    @PreAuthorize("hasPermission(#transactionID, 'transaction') and hasRole('STUDENT')")
    @PostMapping("/transaction/{transactionID}/delivery")
    public String createDeliveryAction(Action delivery, @PathVariable Long transactionID,
            @RequestParam("attachment") MultipartFile[] attachments,
            @AuthenticationPrincipal User user) {
        Transaction transaction = transactionRepository.findById(transactionID).get();
        transaction.setShipped(true);
        delivery.setInitiator(user);
        delivery.setTransaction(transaction);
        delivery.setActiontype(actionTypeService.getDeliveryActionType());
        actionRepository.save(delivery);
        transactionRepository.save(transaction);
        if (attachments.length > 0) {
            List<AttachedFile> attachedFiles = attachedFileService.storeFiles(Arrays.asList(attachments), delivery);
            delivery.setAttachedFiles(attachedFiles);
        }
        return "redirect:/transaction/" + transactionID;
    }

    @PreAuthorize("hasPermission(#transactionID, 'transaction') and hasRole('STUDENT')")
    @PostMapping("/transaction/{transactionID}/invoicing")
    public String createInvoiceAction(Action invoice, @PathVariable Long transactionID,
            @RequestParam("attachment") MultipartFile[] attachments,
            @AuthenticationPrincipal User user) {
        Transaction transaction = transactionRepository.findById(transactionID).get();
        invoice.setInitiator(user);
        invoice.setTransaction(transaction);
        invoice.setActiontype(actionTypeService.getInvoiceActionType());
        actionRepository.save(invoice);
        transactionRepository.save(transaction);
        if (attachments.length > 0) {
            List<AttachedFile> attachedFiles = attachedFileService.storeFiles(Arrays.asList(attachments), invoice);
            invoice.setAttachedFiles(attachedFiles);
        }
        return "redirect:/transaction/" + transactionID;
    }

    @PreAuthorize("hasPermission(#transactionID, 'transaction') and hasRole('STUDENT')")
    @PostMapping("/transaction/{transactionID}/paid")
    public String createPaidAction(Action payment, @PathVariable Long transactionID,
            @RequestParam("attachment") MultipartFile[] attachments,
            @AuthenticationPrincipal User user) {
        Transaction transaction = transactionRepository.findById(transactionID).get();
        transaction.setPaid(true);
        transaction.setActive(false);
        payment.setInitiator(user);
        payment.setTransaction(transaction);
        payment.setActiontype(actionTypeService.getPaidActionType());
        actionRepository.save(payment);
        transactionRepository.save(transaction);
        if (attachments.length > 0) {
            List<AttachedFile> attachedFiles = attachedFileService.storeFiles(Arrays.asList(attachments), payment);
            payment.setAttachedFiles(attachedFiles);
        }
        return "redirect:/transaction/" + transactionID;
    }

    @PreAuthorize("hasPermission(#transactionID, 'transaction') and hasRole('STUDENT')")
    @PostMapping("/transaction/{transactionID}/rate")
    public String rateTransaction(@PathVariable Long transactionID, @AuthenticationPrincipal User user,
            @RequestParam int rating) {

        Transaction t = transactionRepository.findById(transactionID).get();
        Company ratingCompany = user.getCompany();
        if (!ratingRepository.existsByTransactionAndRatingCompany(t, ratingCompany)) {
            Rating r;
            if (ratingCompany.getId().equals(t.getBuyer().getId())) {
                r = new Rating(t, t.getSeller(), ratingCompany, rating);
            } else {
                r = new Rating(t, t.getBuyer(), ratingCompany, rating);
            }
            ratingRepository.save(r);
        }
        return "redirect:/transaction/" + transactionID + "?rated";
    }

    @GetMapping("/pdfexport/action/{actionId}")
    public ResponseEntity<byte[]> exportAction(@PathVariable long actionId, HttpServletResponse response, Model model) {
        Action action = actionRepository.findById(actionId).get(); // TODO exception handling

        if (!action.getAttachedFiles().isEmpty()) { // TODO machen wir, wenn wir bezahlt werden.
            byte[] contents = action.getAttachedFiles().get(0).getData();
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            String filename = action.getAttachedFiles().get(0).getFileName(); // Last part is the filename
            headers.setContentDispositionFormData(filename, filename);
            headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
            ResponseEntity<byte[]> res = new ResponseEntity<>(contents, headers, HttpStatus.OK);
            return res;
        }

        ByteArrayOutputStream out = attachedFileService.generatePdfFromAction(actionId);
        byte[] contents = out.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "pdf-export-" + actionId + ".pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> res = new ResponseEntity<>(contents, headers, HttpStatus.OK);
        return res;
    }

    @GetMapping("/pdfexport/transaction/{transactionId}")
    public ResponseEntity<byte[]> exportTransaction(@PathVariable long transactionId, HttpServletResponse response,
            Model model) {
        ByteArrayOutputStream out = attachedFileService.generatePdfFromTransaction(transactionId);

        byte[] contents;
        contents = out.toByteArray();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = "pdf-export-" + transactionId + ".pdf";
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> res = new ResponseEntity<>(contents, headers, HttpStatus.OK);
        return res;
    }

    @GetMapping("download/attachment/{fileId}")
    public ResponseEntity<byte[]> downloadAttachement(@PathVariable long fileId, HttpServletResponse response, Model model) {

        AttachedFile attachement = attachedFileService.getFile(fileId);

        byte[] contents = attachement.getData();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        String filename = attachement.getFileName();
        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> res = new ResponseEntity<>(contents, headers, HttpStatus.OK);
        return res;
    }
}
