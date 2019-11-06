package domain;

import java.io.Serializable;

public interface AccountTransaction extends Serializable {
    Long getAccountNo();
    Double getAmount();
}
