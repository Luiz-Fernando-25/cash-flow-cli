package org.example.domain.models;

import java.math.BigDecimal;
import java.util.Objects;

public class CreditCard {
    private Integer id;
    private String name;
    private BigDecimal limit;
    private int closigDay;
    private int dueDate;
    private Bank bank;


    public CreditCard(String name, Bank bank) {
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
                ", closigDay=" + closigDay +
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

    public int getClosigDay() {
        return closigDay;
    }

    public void setClosigDay(int closigDay) {
        this.closigDay = closigDay;
    }

    public int getDueDate() {
        return dueDate;
    }

    public void setDueDate(int dueDate) {
        this.dueDate = dueDate;
    }

    public Bank getBank() {
        return bank;
    }

    public void setBank(Bank bank) {
        this.bank = bank;
    }
}
