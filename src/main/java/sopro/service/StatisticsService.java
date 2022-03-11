package sopro.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import sopro.model.Company;
import sopro.model.Transaction;
import sopro.repository.ActionRepository;
import sopro.repository.ActionTypeRepository;
import sopro.repository.CompanyRepository;
import sopro.repository.TransactionRepository;

@Service
public class StatisticsService {
    
    @Autowired
    CompanyRepository companyRepository;

    // @Autowired
    // UserRepository userRepository;

    @Autowired
    TransactionRepository transactionRepository;

    @Autowired
    ActionRepository actionRepository;

    @Autowired
    ActionTypeRepository actionTypeRepository;

    public Double getTotalTransactionBuyerVolume(Company buyer) {
        List<Transaction> transactions = transactionRepository.findByBuyer(buyer);
        Double volume = 0.0;
        for(Transaction transaction : transactions) {
            transaction.getActions().
        }
    }
    


    public Integer getAmountTransactionBuyer(Company buyer){
        List<Transaction> transactions = transactionRepository.findByBuyer(buyer);
        return transactions.size();
    }

    public Integer getAmountTransactionSeller(Company seller){
        List<Transaction> transactions = transactionRepository.findByBuyer(seller);
        return transactions.size();
    }
}
