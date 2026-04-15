package org.example.domain.models;

import org.example.domain.enums.TransactionStatus;
import org.example.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.util.Date;

public class TransactionCreditCard extends TransactionOutput{

    private CreditCard creditCard;
    private Date buyDate;
    
    

    public TransactionCreditCard(BigDecimal transactionValue, String description, Date date, TransactionStatus status,
            Category category, TransactionType type, AbstractAccount account, CreditCard creditCard, Date buyDate) {
        super(transactionValue, description, date, status, category, type, account);
        this.creditCard = creditCard;
        this.buyDate = buyDate;
    }


    public TransactionCreditCard(Integer id, BigDecimal transactionValue, String description, Date date, TransactionStatus status,
            Category category, TransactionType type, AbstractAccount account, CreditCard creditCard, Date buyDate) {
        super(id, transactionValue, description, date, status, category, type, account);
        this.creditCard = creditCard;
        this.buyDate = buyDate;
    }

    

    @Override
    public String toString() {
        return "TransactionCreditCard [creditCard=" + creditCard + ", id=" + id + ", buyDate=" + buyDate
                + ", transactionValue=" + transactionValue + ", description=" + description + ", date=" + date
                + ", status=" + status + ", category=" + category + ", type=" + type + ", account=" + account + "]";
    }



    public CreditCard getCreditCard() {
        return creditCard;
    }

    public void setCreditCard(CreditCard creditCard) {
        this.creditCard = creditCard;
    }

    public Date getBuyDate() {
        return buyDate;
    }

    public void setBuyDate(Date buyDate) {
        this.buyDate = buyDate;
    }

    


}
