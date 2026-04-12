package org.example.domain.models;

import org.example.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.util.Date;

public class TransactionInput extends AbstractTransaction {
    public TransactionInput(BigDecimal transacrionValue, String descripion, Date date, String status, TransactionType type, AbstractAccount account) {
        super(transacrionValue, descripion, date, status, type, account);
    }
}
