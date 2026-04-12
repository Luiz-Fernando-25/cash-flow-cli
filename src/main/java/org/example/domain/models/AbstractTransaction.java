package org.example.domain.models;

import org.example.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

public abstract class AbstractTransaction {
    protected Integer id;
    protected BigDecimal transacrionValue;
    protected String descripion;
    protected Date date;
    protected String status;
    protected TransactionType type;
    protected AbstractAccount account;

    public AbstractTransaction(BigDecimal transacrionValue, String descripion, Date date, String status, TransactionType type, AbstractAccount account) {
        this.transacrionValue = transacrionValue;
        this.descripion = descripion;
        this.date = date;
        this.status = status;
        this.type = type;
        this.account = account;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        AbstractTransaction that = (AbstractTransaction) o;
        return Objects.equals(getId(), that.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "AbstractTransaction{" +
                "id=" + id +
                ", transacrionValue=" + transacrionValue +
                ", descripion='" + descripion + '\'' +
                ", date=" + date +
                ", status='" + status + '\'' +
                ", type=" + type +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getTransacrionValue() {
        return transacrionValue;
    }

    public void setTransacrionValue(BigDecimal transacrionValue) {
        this.transacrionValue = transacrionValue;
    }

    public String getDescripion() {
        return descripion;
    }

    public void setDescripion(String descripion) {
        this.descripion = descripion;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }
}
