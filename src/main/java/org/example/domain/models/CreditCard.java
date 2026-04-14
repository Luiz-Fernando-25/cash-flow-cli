package org.example.domain.models;

import java.math.BigDecimal;
import java.util.Objects;

public class CreditCard {
    private Integer id;
    private String name;
    private BigDecimal limit;
    private int closingDay;
    private int dueDate;
    private AccountBank bank;

    
    public CreditCard(Integer id, String name, BigDecimal limit, int closingDay, int dueDate, AccountBank bank) {
        this.id = id;
        this.name = name;
        this.limit = limit;
        this.closingDay = closingDay;
        this.dueDate = dueDate;
        this.bank = bank;
    }

    public CreditCard(String name, AccountBank bank) {
        this.name = name;
        this.bank = bank;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        CreditCard that = (CreditCard) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "CreditCard{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", limit=" + limit +
                ", closingDay=" + closingDay +
                ", dueDate=" + dueDate +
                ", bank=" + bank +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getLimit() {
        return limit;
    }

    public void setLimit(BigDecimal limit) {
        this.limit = limit;
    }

    public int getClosingDay() {
        return closingDay;
    }

    public void setClosingDay(int closingDay) {
        this.closingDay = closingDay;
    }

    public int getDueDate() {
        return dueDate;
    }

    public void setDueDate(int dueDate) {
        this.dueDate = dueDate;
    }

    public AccountBank getBank() {
        return bank;
    }

    public void setBank(AccountBank bank) {
        this.bank = bank;
    }
}
