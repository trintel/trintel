package sopro.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

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

    /**
     *
     * @param buyer the buying company
     * @return the total volume of all transactions, where the company was buyer
     */
    public Double getTotalTransactionBuyerVolume(Company buyer) {
        List<Transaction> transactions = transactionRepository.findByBuyer(buyer);
        Double volume = 0.0;
        for(Transaction transaction : transactions) {
            if(transaction.getConfirmed()) volume += transaction.getAmount() * transaction.getPricePerPiece();     //0 if transcation doesnt have an offer yet.
        }
        return volume;
    }

    /**
     *
     * @param seller the seller company
     * @return the total volume of all transactions, where the company was seller
     */
    public Double getTotalTransactionSellerVolume(Company seller) {
        List<Transaction> transactions = transactionRepository.findBySeller(seller);
        Double volume = 0.0;
        for(Transaction transaction : transactions) {
            if(transaction.getConfirmed()) volume += transaction.getAmount() * transaction.getPricePerPiece();     //0 if transcation doesnt have an offer yet.
        }
        return volume;
    }


    /**
     *
     * @param buyer the buying company
     * @return the number of transactions, where the company has bought products
     */
    public Integer getNumberNonConfirmedTransactionBuyer(Company buyer){
        List<Transaction> transactions = transactionRepository.findByBuyer(buyer);
        return transactions.size();
    }

    /**
     *
     * @param seller the selling company
     * @return the number of transactions, where the company has sold products
     */
    public Integer getNumberNonConfirmedTransactionSeller(Company seller){
        List<Transaction> transactions = transactionRepository.findBySeller(seller);
        return transactions.size();
    }

    /**
     *
     * @param seller the selling company
     * @return the number of different companies, that have bought from the selling company
     */
    public Long getNumberDistinctBuyers(Company seller) {
        return transactionRepository.countDistinctBuyers(seller);
    }

    /**
     *
     * @param buyer the buying company
     * @return the number of different companies, that have sold to the buying company
     */
    public Long getNumberDistinctSellers(Company buyer) {
        return transactionRepository.countDistinctSellers(buyer);
    }

    public Long getNumberConfirmedTransactions(Company company) {
        return transactionRepository.countConfirmedTransactions(company);
    }

    // public Map<Company, Double[]> getRelativeStatistics(Company company) {

    //     Map<Company, Double[]> relativeStatistics = new HashMap<>();

    //     List<Transaction> transactions = company.getBuyingTransactions();
        
    //     for(Transaction transaction : transactions.stream().filter(t -> t.getConfirmed()).toArray(Transaction[] :: new)) {
    //         Double nb_transactions = relativeStatistics.get(transaction.getSeller())[0];
    //         relativeStatistics.put(transaction.getSeller(), nb_transactions == null ? 1 : nb_transactions + 1);
    //     }


    //     transactions = company.getSellingTransactions();

    //     for(Transaction transaction : transactions.stream().filter(t -> t.getConfirmed()).toArray(Transaction[] :: new)) {
    //         Double nb_transactions = relativeStatistics.get(transaction.getSeller())[0];
    //         relativeStatistics.put(transaction.getBuyer(), nb_transactions == null ? 1 : nb_transactions + 1);
    //     }

    //     return relativeStatistics;
    // }

    
}
