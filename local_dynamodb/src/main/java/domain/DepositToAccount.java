package domain;


import java.io.Serializable;

public class DepositToAccount implements Serializable { //using protal serialization in prod
    private Long accountNo;
    private Double amount;

    public DepositToAccount(long accountNo, Double amount) {
        this.accountNo = accountNo;
        this.amount = amount;
    }

    public Long getAccountNo() {
        return accountNo;
    }

    public Double getAmount() {
        return amount;
    }
}


