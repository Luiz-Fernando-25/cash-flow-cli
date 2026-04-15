package org.example.domain.models;

import org.example.domain.enums.TransactionStatus;
import org.example.domain.enums.TransactionType;

import java.math.BigDecimal;
import java.util.Date;
import java.util.Objects;

public abstract class AbstractTransaction {
    protected Integer id;
    protected BigDecimal transactionValue;
    protected String description;
    protected Date date;
    protected TransactionStatus status;
    protected Category category;
    protected TransactionType type;
    protected AbstractAccount account;

    

    public AbstractTransaction(BigDecimal transactionValue, String description, Date date, TransactionStatus status,
            Category category, TransactionType type, AbstractAccount account) {
        this.transactionValue = transactionValue;
        this.description = description;
        this.date = date;
        this.status = status;
        this.category = category;
        this.type = type;
        this.account = account;
    }

    public AbstractTransaction(Integer id, BigDecimal transactionValue, String description, Date date, TransactionStatus status, Category category, TransactionType type, AbstractAccount account) {
        this.id = id;
        this.transactionValue = transactionValue;
        this.description = description;
        this.date = date;
        this.status = status;
        this.category = category;
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
        return "AbstractTransaction [id=" + id + ", transactionValue=" + transactionValue + ", description="
                + description + ", date=" + date + ", status=" + status + ", category=" + category + ", type=" + type
                + ", account=" + account + "]";
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public BigDecimal getTransactionValue() {
        return transactionValue;
    }

    public void setTransactionValue(BigDecimal transactionValue) {
        this.transactionValue = transactionValue;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public TransactionStatus getStatus() {
        return status;
    }

    public void setStatus(TransactionStatus status) {
        this.status = status;
    }

    public TransactionType getType() {
        return type;
    }

    public void setType(TransactionType type) {
        this.type = type;
    }

    public Category getCategory() {
        return category;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public AbstractAccount getAccount() {
        return account;
    }

    public void setAccount(AbstractAccount account) {
        this.account = account;
    }

    
}
