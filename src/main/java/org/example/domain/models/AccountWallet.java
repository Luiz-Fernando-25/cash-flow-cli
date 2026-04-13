package org.example.domain.models;

import java.math.BigDecimal;

import org.example.domain.enums.AccountType;

public class AccountWallet extends AbstractAccount{
    {
        type = AccountType.CARTEIRA;
    }

        
    public AccountWallet(Integer id, String accountName, BigDecimal balance) {
        super(id, accountName, balance);
    }

    public AccountWallet(String accountName) {
        super(accountName);
    }




}
