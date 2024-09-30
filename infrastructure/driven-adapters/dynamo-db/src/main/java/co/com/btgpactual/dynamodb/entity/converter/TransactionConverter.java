package co.com.btgpactual.dynamodb.entity.converter;

import co.com.btgpactual.model.transaction.Transaction;
import co.com.btgpactual.model.transaction.TransactionType;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class TransactionConverter implements AttributeConverter<List<Transaction>> {
    @Override
    public AttributeValue transformFrom(List<Transaction> transactions) {
        return AttributeValue.builder().l(transactions.stream()
                .map(transaction -> AttributeValue.builder().m(
                        Map.of(
                                "id", AttributeValue.builder().s(transaction.getId().toString()).build(),
                                "type", AttributeValue.builder().s(transaction.getType().toString()).build(),
                                "fundId", AttributeValue.builder().s(transaction.getFundId()).build(),
                                "amount", AttributeValue.builder().n(transaction.getAmount().toString()).build(),
                                "date", AttributeValue.builder().s(transaction.getDate().toString()).build()
                        )
                ).build()).toList()
        ).build();
    }

    @Override
    public List<Transaction> transformTo(AttributeValue attributeValue) {
        return Optional.ofNullable(attributeValue)
                .map(AttributeValue::l)
                .map(values -> mapAttributes(attributeValue))
                .orElseGet(ArrayList::new);
    }

    @Override
    public EnhancedType<List<Transaction>> type() {
        return EnhancedType.listOf(Transaction.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.L;
    }

    private List<Transaction> mapAttributes(AttributeValue attributeValue) {
        return attributeValue.l().stream()
                .map(data -> Transaction.builder()
                        .id(Optional.ofNullable(data.m().get("id"))
                                .map(AttributeValue::s).map(UUID::fromString)
                                .orElse(null))
                        .type(Optional.ofNullable(data.m().get("type"))
                                .map(AttributeValue::s).map(TransactionType::valueOf)
                                .orElse(null))
                        .fundId(Optional.ofNullable(data.m().get("fundId"))
                                .map(AttributeValue::s).orElse(null))
                        .amount(Optional.ofNullable(data.m().get("amount"))
                                .map(AttributeValue::n).map(BigDecimal::new)
                                .orElse(null))
                        .date(Optional.ofNullable(data.m().get("date"))
                                .map(AttributeValue::s).map(LocalDateTime::parse)
                                .orElse(null))
                        .build())
                .toList();
    }
}
