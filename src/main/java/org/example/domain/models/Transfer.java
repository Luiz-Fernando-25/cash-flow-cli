package org.example.domain.models;

import java.util.Objects;

public class Transfer {
    private Integer id;
    private AbstractTransaction outputTransaction;
    private AbstractTransaction inputTransaction;

    
    public Transfer(Integer id, AbstractTransaction outputTransaction, AbstractTransaction inputTransaction) {
        this.id = id;
        this.outputTransaction = outputTransaction;
        this.inputTransaction = inputTransaction;
    }

    public Transfer(AbstractTransaction outputTransaction, AbstractTransaction inputTransaction) {
        this.outputTransaction = outputTransaction;
        this.inputTransaction = inputTransaction;
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        Transfer transfer = (Transfer) o;
        return Objects.equals(getId(), transfer.getId());
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(getId());
    }

    @Override
    public String toString() {
        return "Transfer{" +
                "id=" + id +
                ", outputTransaction=" + outputTransaction +
                ", inputTransaction=" + inputTransaction +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public AbstractTransaction getOutputTransaction() {
        return outputTransaction;
    }

    public void setOutputTransaction(AbstractTransaction outputTransaction) {
        this.outputTransaction = outputTransaction;
    }

    public AbstractTransaction getInputTransaction() {
        return inputTransaction;
    }

    public void setInputTransaction(AbstractTransaction inputTransaction) {
        this.inputTransaction = inputTransaction;
    }
}
