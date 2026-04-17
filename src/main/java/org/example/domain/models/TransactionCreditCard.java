package org.example.domain.models;

import org.example.domain.enums.TransactionStatus;
import org.example.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.util.Date;

public class TransactionCreditCard extends TransactionOutput{

    private CreditCard creditCard;
    private Date dueDate;
    
    

    public TransactionCreditCard(BigDecimal transactionValue, String description, Date date, TransactionStatus status,
            Category category, TransactionType type, AbstractAccount account, CreditCard creditCard, Date dueDate) {
        super(transactionValue, description, date, status, category, type, account);
        this.creditCard = creditCard;
        this.dueDate = dueDate;
    }


    public TransactionCreditCard(Integer id, BigDecimal transactionValue, String description, Date date, TransactionStatus status,
            Category category, TransactionType type, AbstractAccount account, CreditCard creditCard, Date dueDate) {
        super(id, transactionValue, description, date, status, category, type, account);
        this.creditCard = creditCard;
        this.dueDate = dueDate;
    }

    

    @Override
    public String toString() {
        return "TransactionCreditCard [creditCard=" + creditCard + ", id=" + id + ", dueDate=" + dueDate
                + ", transactionValue=" + transactionValue + ", description=" + description + ", date=" + date
                + ", status=" + status + ", category=" + category + ", type=" + type + ", account=" + account + "]";
    }



    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    public Date getDueDate() {
        return dueDate;
    }

    public void setDueDate(Date dueDate) {
        this.dueDate = dueDate;
    }

    


}
