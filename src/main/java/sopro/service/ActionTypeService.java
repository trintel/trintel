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
        return actionTypeRepository.findByName("Request");      //TODO dynamisch, falls Anforderung nach abweichender anfänglichen Aktion.
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

    /**
     * Approach: get all Actions available for initiator Type, then substract the standard-Actions that are not availale.
     * Then: custom Action will be, by default, always available.
     * @param transaction the transaction which is to be edited
     * @param currentUser the user trying to edit.
     * @return a list of all available actiontypes
     */
    public List<ActionType> getAvailableActions(Transaction transaction, User currentUser) {

        if(!transaction.getActive()) {
            return new ArrayList<>();
        }

        InitiatorType initiatorType = InitiatorType.SELLER;

        if(currentUser.getCompany().equals(transaction.getBuyer())) {      //findout if current user is Buyer or seller.
            initiatorType = InitiatorType.BUYER;
        }

        List<ActionType> actionTypes = new ArrayList<>();
        actionTypes.addAll(actionTypeRepository.findByInitiatorType(initiatorType));
        actionTypes.addAll(actionTypeRepository.findByInitiatorType(InitiatorType.BOTH));
        //Remove all actionTypes, that are not available
        for(ActionType actionType : actionTypes.stream().filter(t -> t.isStandardAction()).toArray(ActionType[] :: new)) {

            //Add the Offer-Option if: the last action is a Request from the other company
            //                     OR: the last action is an Offer
            if(actionType.getName().equals("Offer")) {
                if(transaction.getLatestAction().getInitiator().getCompany().getId().equals(currentUser.getCompany().getId()) && transaction.getLatestActionName().equals("Request")) {
                    if(!transaction.getLatestStandardAction().getActiontype().getName().equals("Offer")) {
                        actionTypes.remove(actionType);
                    }
                    // actionTypes.remove(actionType);
                }
            }

            //Add the Accept-Option if: the last action is an Offer from the other company
            if(actionType.getName().equals("Accept")) {
                if(transaction.getLatestStandardAction().getInitiator().getCompany().getId().equals(currentUser.getCompany().getId()) || !transaction.getLatestStandardAction().getActiontype().getName().equals("Offer")) {
                    actionTypes.remove(actionType);
                }
            }

            //Add the Cancel-Option if: The transaction is not confirmed yet.
            if(actionType.getName().equals("Cancel")) {
                if(transaction.getConfirmed()) {
                    actionTypes.remove(actionType);
                }
            }

            //Add the delivery-Option if: the transaction is confirmed
            if(actionType.getName().equals("Delivery")) {
                if(!transaction.getConfirmed()) {
                    actionTypes.remove(actionType);
                }
            }

            //Add the invoicing-Option if: the transaction is confirmed
            if(actionType.getName().equals("Invoicing")) {
                if(!transaction.getConfirmed()) {
                    actionTypes.remove(actionType);
                }
            }

            //Add the paid-Option if: the transaction is confirmed
            if(actionType.getName().equals("Paid")) {
                if(!transaction.getShipped()) {
                    actionTypes.remove(actionType);
                }
            }
        }

        return actionTypes;

    }

}
