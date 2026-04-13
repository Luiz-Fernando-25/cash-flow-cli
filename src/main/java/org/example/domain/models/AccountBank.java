package org.example.domain.models;

import java.math.BigDecimal;

import org.example.domain.enums.AccountType;

public class AccountBank extends AbstractAccount{
    {
        type = AccountType.BANCO;
    }

    
    public AccountBank(Integer id, String accountName, BigDecimal balance) {
        super(id, accountName, balance);
    }


    public AccountBank(String accountName) {
        super( accountName);
    }



    
}
