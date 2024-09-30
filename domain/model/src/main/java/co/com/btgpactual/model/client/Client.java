package co.com.btgpactual.model.client;

import co.com.btgpactual.model.subscription.Subscription;
import co.com.btgpactual.model.transaction.Transaction;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.util.List;

@Setter
@Getter
@Builder
public class Client {
    private String id;
    private String phoneNumber;
    private String email;
    private BigDecimal balance;
    private List<Subscription> subscriptions;
    private List<Transaction> transactions;
}
