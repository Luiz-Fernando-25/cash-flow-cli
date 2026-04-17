package org.example.service.impl;

import java.math.BigDecimal;
import java.util.Calendar;
import java.util.Date;
import java.util.List;


import org.example.domain.enums.CategoryType;
import org.example.domain.enums.TransactionStatus;
import org.example.domain.enums.TransactionType;
import org.example.domain.models.AbstractAccount;
import org.example.domain.models.AbstractTransaction;
import org.example.domain.models.Category;
import org.example.domain.models.CreditCard;
import org.example.domain.models.TransactionInput;
import org.example.domain.models.TransactionOutput;
import org.example.repositories.AccountRepository;
import org.example.repositories.CategoryRepository;
import org.example.repositories.CreditCardRepository;
import org.example.repositories.TransactionRepository;
import org.example.service.AccountService;
import org.example.service.TransactionService;

public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository repoTransaction;
    private final AccountRepository repoAccount;
    private final CreditCardRepository repoCreditCard;
    private final CategoryRepository repoCategory;
    private final AccountService servAccount;



    public TransactionServiceImpl(TransactionRepository repoTransaction, AccountRepository repoAccount,
            CreditCardRepository repoCreditCard, CategoryRepository repoCategory, AccountService servAccount) {
        this.repoTransaction = repoTransaction;
        this.repoAccount = repoAccount;
        this.repoCreditCard = repoCreditCard;
        this.repoCategory = repoCategory;
        this.servAccount = servAccount;
    }

    private void validateBasicData(BigDecimal value, String description, Date date){
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) throw new RuntimeException("O Valor da transação tem que ser um valor positovo e não nulo!");
        if (description == null || description.trim().isEmpty()) throw new RuntimeException("A descrição tem que ter um valor valido!");
        if (date == null) date = new Date();
    }

    private Category validateCategory(Integer categoryId){
        Category category = repoCategory.findById(categoryId).orElseThrow(
            () -> new RuntimeException("O id da categoria não foi encontrado.")
        );
        return category;
    }

    private AbstractAccount validateAccount(Integer accountId){
        AbstractAccount account = repoAccount.findById(accountId).orElseThrow(
            () -> new RuntimeException("O id da conta não foi encontrado.")
        );
        return account;
    }

    @Override
    public void create(BigDecimal value, String description, Date date, TransactionStatus status, Integer categoryId,
            TransactionType transactionType, Integer accountId) {
                validateBasicData(value, description, date);
                Category category = validateCategory(categoryId);
                AbstractAccount account = validateAccount(accountId);

                AbstractTransaction transaction;
                if (TransactionType.ENTRADA == transactionType) {
                    transaction = new TransactionInput(value, description, date, TransactionStatus.PENDENTE, category, transactionType, account);
                } else if (TransactionType.SAIDA == transactionType) {
                    transaction = new TransactionOutput(value, description, date, TransactionStatus.PENDENTE, category, transactionType, account);
                } else {
                    throw new RuntimeException("O tipo de transação não é um tipo valido!");
                }
                if (TransactionStatus.EFETIVADA == status) {
                    transaction.setStatus(status);
                    if (TransactionType.ENTRADA == transactionType) {
                        servAccount.deposit(accountId, value);
                    } else {
                        servAccount.withdraw(accountId, value);
                    }
                } else if (!(TransactionType.SAIDA == transactionType)) {
                    throw new RuntimeException("O status é invalido!");
                } 
                repoTransaction.save(transaction);
            }

    @Override
    public void createCreditCardTransaction(BigDecimal value, String description, Date date, Integer categoryId, Integer accountId, Integer cardId) {
/* ----ainda em criação---- 
        validateBasicData(value, description, date);
        Category category = validateCategory(categoryId);
        AbstractAccount account = validateAccount(accountId);
        CreditCard creditCard = repoCreditCard.findById(cardId).orElseThrow(
            () -> new RuntimeException("O id da conta não foi encontrado.")
        );
        Date todayDate = new Date();
        Calendar cal = Calendar.getInstance();
        cal.setTime(todayDate);
        int todayDay = cal.get(Calendar.DAY_OF_MONTH);
        if (todayDay > creditCard.getClosingDay() && todayDay < ge){

        }
        int mes = dueDate.getMonth();
        creditCard.getDueDate()        
 
 */    }

    @Override
    public List<AbstractTransaction> searchTransactions(TransactionStatus status, Integer categoryId,
            CategoryType categoryType, Integer accountId, Integer cardId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'searchTransactions'");
    }

    @Override
    public void changeValue(Integer transactionId, BigDecimal value) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changeValue'");
    }

    @Override
    public void changeDescription(Integer transactionId, String description) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changeDescription'");
    }

    @Override
    public void changeDate(Integer transactionId, Date date) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changeDate'");
    }

    @Override
    public void changeStatus(Integer transactionId, TransactionStatus status) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changeStatus'");
    }

    @Override
    public void changeCategory(Integer transactionId, Integer categoryId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changeCategory'");
    }

    @Override
    public void changeAccount(Integer transactionId, Integer accountId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changeAccount'");
    }

    @Override
    public void changeCreditCard(Integer transactionId, Integer creditCardId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changeCreditCard'");
    }

    @Override
    public void changeDateBuy(Integer transactionId, Date dateBuy) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'changeDateBuy'");
    }

    @Override
    public void remove(Integer transactionId) {
        // TODO Auto-generated method stub
        throw new UnsupportedOperationException("Unimplemented method 'remove'");
    }
    
}
