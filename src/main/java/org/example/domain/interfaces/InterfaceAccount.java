package org.example.domain.interfaces;

import java.math.BigDecimal;

public interface InterfaceAccount {
    BigDecimal getBalance();
    void deposit(BigDecimal value);
    void withdraw(BigDecimal value);
}
