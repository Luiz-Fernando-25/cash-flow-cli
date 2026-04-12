package org.example.domain.models;

import org.example.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.util.Date;

public class TransactionOutput extends AbstractTransaction{
    public TransactionOutput(BigDecimal transacrionValue, String descripion, Date date, String status, TransactionType type, AbstractAccount account) {
        super(transacrionValue, descripion, date, status, type, account);
    }
}
