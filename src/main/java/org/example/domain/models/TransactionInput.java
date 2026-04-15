package org.example.domain.models;

import org.example.domain.enums.TransactionStatus;
import org.example.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.util.Date;

public class TransactionInput extends AbstractTransaction {

    

    public TransactionInput(BigDecimal transactionValue, String description, Date date, TransactionStatus status,
            Category category, TransactionType type, AbstractAccount account) {
        super(transactionValue, description, date, status, category, type, account);
    }

    public TransactionInput(Integer id, BigDecimal transactionValue, String description, Date date, TransactionStatus status,
            Category category, TransactionType type, AbstractAccount account) {
        super(id, transactionValue, description, date, status, category, type, account);
    }

}
