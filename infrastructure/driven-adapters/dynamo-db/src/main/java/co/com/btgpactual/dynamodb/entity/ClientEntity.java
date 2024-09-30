package co.com.btgpactual.dynamodb.entity;

import co.com.btgpactual.dynamodb.entity.converter.SubscriptionConverter;
import co.com.btgpactual.dynamodb.entity.converter.TransactionConverter;
import co.com.btgpactual.model.subscription.Subscription;
import co.com.btgpactual.model.transaction.Transaction;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbConvertedBy;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

import java.math.BigDecimal;
import java.util.List;

@DynamoDbBean
@NoArgsConstructor
@AllArgsConstructor
@Setter
public class ClientEntity {
    private String id;
    private String phoneNumber;
    private String email;
    private BigDecimal balance;
    private List<Subscription> subscriptions;
    private List<Transaction> transactions;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("id")
    public String getId() {
        return id;
    }

    @DynamoDbAttribute("phoneNumber")
    public String getPhoneNumber() {
        return phoneNumber;
    }

    @DynamoDbAttribute("email")
    public String getEmail() {
        return email;
    }

    @DynamoDbAttribute("balance")
    public BigDecimal getBalance() {
        return balance;
    }

    @DynamoDbAttribute("subscriptions")
    @DynamoDbConvertedBy(SubscriptionConverter.class)
    public List<Subscription> getSubscriptions() {
        return subscriptions;
    }

    @DynamoDbAttribute("transactions")
    @DynamoDbConvertedBy(TransactionConverter.class)
    public List<Transaction> getTransactions() { return transactions;
    }
}