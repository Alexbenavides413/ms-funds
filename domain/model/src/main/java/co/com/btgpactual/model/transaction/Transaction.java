package co.com.btgpactual.model.transaction;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Getter
@Setter
@Builder
public class Transaction {
    private UUID id;
    private TransactionType type;
    private String fundId;
    private BigDecimal amount;
    private LocalDateTime date;
}
