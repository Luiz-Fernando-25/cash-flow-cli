package org.example.domain.models;

import org.example.domain.interfaces.Account;

import java.math.BigDecimal;
import java.util.Objects;

public abstract class AbstractAccount implements Account {
    protected Integer id;
    protected String accountName;
    protected BigDecimal balance = BigDecimal.ZERO;

    public AbstractAccount(String accountName) {
        this.accountName = accountName;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AbstractAccount that = (AbstractAccount) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public BigDecimal getBalance(){
        return this.balance;
    }

    @Override
    public void deposit(BigDecimal value) {
        if (value == null || value.compareTo(BigDecimal.ZERO) <= 0) {
            throw new RuntimeException("O valor do depósito deve ser maior que zero.");
        }
        this.balance = this.balance.add(value);
    }

    @Override
    public void withdraw(BigDecimal value) {
        if (value == null || value.compareTo(this.balance) <= 0) {
            throw new RuntimeException("Saldo insuficiente para realizar o saque.");
        }
        this.balance = this.balance.subtract(value);
    }

    @Override
    public String toString() {
        return "AbstractAccount{" +
                "id=" + id +
                ", accountName='" + accountName + '\'' +
                ", balance=" + balance +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getAccountName() {
        return accountName;
    }

    public void setAccountName(String accountName) {
        this.accountName = accountName;
    }

    public void setBalance(BigDecimal balance) {
        this.balance = balance;
    }
}
