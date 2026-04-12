package org.example.domain.models;

import org.example.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.util.Date;

public class TransactionCreditCard extends TransactionOutput{

    public TransactionCreditCard(BigDecimal transacrionValue, String descripion, Date date, String status, TransactionType type, AbstractAccount account) {
        super(transacrionValue, descripion, date, status, type, account);
    }
}
