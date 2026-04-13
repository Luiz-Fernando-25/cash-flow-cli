package org.example.domain.models;

import org.example.domain.enums.TransactionStatus;
import org.example.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.util.Date;

public class TransactionCreditCard extends TransactionOutput{

    public TransactionCreditCard(BigDecimal transactionValue, String description, Date date, TransactionStatus status, TransactionType type, AbstractAccount account) {
        super(transactionValue, description, date, status, type, account);
    }
}
