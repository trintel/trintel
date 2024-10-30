package sopro.service;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sopro.model.ActionType;
import sopro.model.Transaction;
import sopro.model.User;
import sopro.model.util.InitiatorType;
import sopro.repository.ActionTypeRepository;

@Service
public class ActionTypeService {

    @Autowired
    ActionTypeRepository actionTypeRepository;

    public ActionType getInitialActionType() {
        return actionTypeRepository.findByName("Request"); // TODO dynamisch, falls Anforderung nach abweichender
                                                           // anfänglichen Aktion.
    }

    public ActionType getAcceptActionType() {
        return actionTypeRepository.findByName("Accept");
    }

    public ActionType getAbortActionType() {
        return actionTypeRepository.findByName("Cancel");
    }

    public ActionType getOfferAction() {
        return actionTypeRepository.findByName("Offer");
    }

    public ActionType getDeliveryActionType() {
        return actionTypeRepository.findByName("Delivery");
    }

    public ActionType getInvoiceActionType() {
        return actionTypeRepository.findByName("Invoicing");
    }

    public ActionType getPaidActionType() {
        return actionTypeRepository.findByName("Paid");
    }

    public ActionType getActionTypeById(Long id) {
        return actionTypeRepository.findById(id).orElse(null); // TODO: check if present
    }

    /**
     * Approach: get all Actions available for initiator Type, then substract the
     * standard-Actions that are not availale.
     * Then: custom Action will be, by default, always available.
     *
     * @param transaction the transaction which is to be edited
     * @param currentUser the user trying to edit.
     * @return a list of all available actiontypes
     */
    public List<ActionType> getAvailableActions(Transaction transaction, User currentUser) {

        if (!transaction.getActive()) {
            return new ArrayList<>();
        }

        InitiatorType initiatorType = InitiatorType.SELLER;

        if (currentUser.getCompany().getId().equals(transaction.getBuyer().getId())) { // findout if current user is
                                                                                       // Buyer or seller.
            initiatorType = InitiatorType.BUYER;
        }

        List<ActionType> actionTypes = new ArrayList<>();
        actionTypes.addAll(actionTypeRepository.findByInitiatorType(initiatorType));
        actionTypes.addAll(actionTypeRepository.findByInitiatorType(InitiatorType.BOTH));

        // If the transaction is empty, all actions are available
        // Remove all actionTypes, that are not available
        // TODO: check if any logic has to be applied.

        return actionTypes;

    }

}
