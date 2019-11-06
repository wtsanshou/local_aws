package domain;

import java.io.Serializable;

public class AccountStatement implements Serializable { //using protal serialization in prod
    private Long accountNo;
    private Double balance;

    public AccountStatement(long accountNo, Double balance) {
        this.accountNo = accountNo;
        this.balance = balance;
    }

    public Long getAccountNo() {
        return accountNo;
    }

    public Double getBalance() {
        return balance;
    }
}
