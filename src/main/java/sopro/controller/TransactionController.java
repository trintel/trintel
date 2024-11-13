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
import org.springframework.transaction.annotation.Transactional;
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
import sopro.model.NonArchivedTransaction;
import sopro.model.Rating;
import sopro.model.Transaction;
import sopro.model.User;
import sopro.repository.ActionRepository;
import sopro.repository.ActionTypeRepository;
import sopro.repository.CompanyRepository;
import sopro.repository.NonArchivedTransactionRepository;
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

    @Autowired
    NonArchivedTransactionRepository nonArchivedTransactionRepository;

    // TODO: write a service for the transactions, because this controller should
    // not handle that much logic.
    // TODO: write a service for the transactions, because this controller should
    // not handle that much logic.

    @GetMapping("/transactions")
    public String listTransactions(Model model, @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "true") boolean sortByNewest, @RequestParam(required = false) Long status,
            @RequestParam(defaultValue = "") String buyer, @RequestParam(defaultValue = "") String seller) {
        List<Transaction> transactions = new ArrayList<>();
        if (user.getRole().equals("ADMIN")) { // Admins can see all transactions
            transactions = transactionRepository.findAllByBuyerAndSellerName(buyer, seller);
        }
        if (user.getRole().equals("STUDENT")) { // Students can only see transactions, where they are involved
            transactions = transactionRepository.findOwnByBuyerAndSellerName(buyer, seller, user.getCompany());
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
        return "transactions/transactions-list";

    }

    @GetMapping("/transactions/archived")
    public String listTransactionsArchived(Model model, @AuthenticationPrincipal User user,
            @RequestParam(defaultValue = "true") boolean sortByNewest, @RequestParam(required = false) Long status,
            @RequestParam(defaultValue = "") String buyer, @RequestParam(defaultValue = "") String seller) {
        List<Transaction> transactions = new ArrayList<>();
        if (user.getRole().equals("ADMIN")) { // Admins can see all transactions
            transactions = transactionRepository.findAllByBuyerAndSellerName(buyer, seller);
        }
        if (user.getRole().equals("STUDENT")) { // Students can only see transactions, where they are involved
            transactions = transactionRepository.findOwnByBuyerAndSellerNameArchived(buyer, seller,
                    user.getCompany());
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
        return "transactions/transactions-list-archived";

    }

    @PreAuthorize("hasCompany()")
    @GetMapping("/transaction/{companyID}/create")
    public String createTransaction(@PathVariable Long companyID, @AuthenticationPrincipal User user, Model model) {

        Transaction newTransaction = new Transaction();
        newTransaction.setSeller(companyRepository.findById(companyID).get());

        Action newAction = new Action();
        // TODO: handle the availability of actiontypes differently.
        List<ActionType> actionTypes = new ArrayList<>();
        actionTypeRepository.findAll().forEach(at -> {
            if (!at.getName().equals("Cancel")) {
                actionTypes.add(at);
            }
        });

        model.addAttribute("actionTypes", actionTypeService);
        model.addAttribute("altActionTypes", actionTypes);
        model.addAttribute("companyID", companyID);
        model.addAttribute("action", newAction);
        model.addAttribute("transaction", newTransaction);
        return "transactions/transaction-add-choose";
    }

    @PreAuthorize("hasCompany()")
    @GetMapping("/transaction/{companyID}/create/{actionTypeID}")
    public String createTransactionSkipSave(Action action, Transaction transaction, @PathVariable Long companyID,
            @PathVariable Long actionTypeID,
            @AuthenticationPrincipal User user, Model model) {
        ActionType actionType = actionTypeService.getActionTypeById(actionTypeID);
        model.addAttribute("companyID", companyID);
        model.addAttribute("skip", true);
        model.addAttribute("actionTypeID", actionTypeID);
        model.addAttribute("actionType", actionType);
        model.addAttribute("actionTypes", actionTypeService);
        model.addAttribute("initiatorType", actionType.getInitiatorType().toString());
        return "transactions/transaction-addAction";
    }

    @PreAuthorize("hasCompany()")
    @PostMapping("/transaction/{companyID}/create/{actionTypeID}")
    @Transactional
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
        transaction = transactionRepository.save(transaction);
        handleAction(action, attachments, transaction.getId(), actionTypeID, user);

        return "redirect:/transaction/" + transaction.getId();
    }

    @Transactional
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

        transactionRepository.create(transaction);
        actionRepository.save(action);

        if (attachments.length > 0) {
            List<AttachedFile> attachedFiles = attachedFileService.storeFiles(Arrays.asList(attachments), action);
            action.setAttachedFiles(attachedFiles);
        } else {
            return "redirect:/transaction/" + companyID + "/create";
        }

        return "redirect:/transactions";

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
        model.addAttribute("transactionID", id);
        model.addAttribute("isAlreadyReviewed",
                ratingRepository.existsByTransactionAndRatingCompany(transaction, user.getCompany()));
        return "transactions/transaction-info";

    }

    @Transactional
    @PreAuthorize("hasPermission(#id, 'transaction')")
    @GetMapping("/transaction/archive/{id}")
    public String archiveTransaction(@PathVariable Long id, @AuthenticationPrincipal User user) {

        Transaction transaction = transactionRepository.findById(id).get();
        NonArchivedTransaction nonArchivedTransaction = nonArchivedTransactionRepository.findByTransactionAndCompany(
                transaction,
                user.getCompany());

        nonArchivedTransactionRepository.delete(nonArchivedTransaction);
        return "redirect:/transactions";
    }

    @Transactional
    @PreAuthorize("hasPermission(#id, 'transaction')")
    @GetMapping("/transaction/activate/{id}")
    public String activateTransaction(@PathVariable Long id, @AuthenticationPrincipal User user) {

        Transaction transaction = transactionRepository.findById(id).get();
        NonArchivedTransaction nonArchivedTransaction = new NonArchivedTransaction();
        nonArchivedTransaction.setTransaction(transaction);
        nonArchivedTransaction.setCompany(user.getCompany());

        nonArchivedTransactionRepository.save(nonArchivedTransaction);
        return "redirect:/transactions/archived";
    }

    @Transactional
    @PreAuthorize("hasPermission(#id, 'transaction')")
    @GetMapping("/transaction/delete/{id}")
    public String deleteTransaction(@PathVariable Long id, @AuthenticationPrincipal User user) {

        Transaction transaction = transactionRepository.findById(id).get();

        List<NonArchivedTransaction> nonArchivedTransactions = nonArchivedTransactionRepository
                .findByTransaction(transaction);

        nonArchivedTransactions.forEach(nonArchivedTransaction -> {
            nonArchivedTransactionRepository.delete(nonArchivedTransaction);
        });

        transactionRepository.delete(transaction);

        // TODO: delete all actions and attached files -> cascade

        return "redirect:/transactions";
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
        return "transactions/transaction-addOffer";
    }

    @Transactional
    @PreAuthorize("hasPermission(#transactionID, 'transaction') and hasRole('STUDENT')")
    @PostMapping("/transaction/{transactionID}/addOffer")
    public String addOffer(Action offer, @RequestParam("attachment") MultipartFile[] attachments,
            @PathVariable Long transactionID, @AuthenticationPrincipal User user,
            BindingResult bindingResult, Model model) {
        if (bindingResult.hasErrors()) {
            model.addAttribute("action", offer);
            model.addAttribute("transactionID", transactionID);
            return "transactions/transaction-addOffer";
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

    @Transactional
    @PreAuthorize("hasPermission(#transactionID, 'transaction') and hasRole('STUDENT')")
    @PostMapping("/transaction/{transactionID}/action/{actionTypeID}")
    public String handleAction(Action action, @RequestParam("attachment") MultipartFile[] attachments,
            @PathVariable Long transactionID, @PathVariable Long actionTypeID,
            @AuthenticationPrincipal User user) {
        Transaction transaction = transactionRepository.findById(transactionID).get();
        action.setInitiator(user);
        action.setTransaction(transaction);

        // TODO: refactor !!!
        // TODO: Accept and Cancel should be handled in a different way.
        ActionType actionType = actionTypeService.getActionTypeById(actionTypeID);
        action.setActiontype(actionType);
        if (actionType.getName().equals("Accept")) {
            transaction.setConfirmed(true);
        } else if (actionType.getName().equals("Cancel")) {
            transaction.setActive(false);
        }

        actionRepository.save(action);
        transactionRepository.create(transaction);
        if (attachments.length > 0) {
            List<AttachedFile> attachedFiles = attachedFileService.storeFiles(Arrays.asList(attachments), action);
            action.setAttachedFiles(attachedFiles);
        }

        return "redirect:/transaction/" + transactionID;
    }

    @Transactional
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
    public ResponseEntity<byte[]> downloadAttachement(@PathVariable long fileId, HttpServletResponse response,
            Model model) {

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
