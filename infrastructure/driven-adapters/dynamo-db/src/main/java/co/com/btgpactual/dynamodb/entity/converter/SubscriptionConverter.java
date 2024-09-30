package co.com.btgpactual.dynamodb.entity.converter;

import co.com.btgpactual.model.subscription.Subscription;
import software.amazon.awssdk.enhanced.dynamodb.AttributeConverter;
import software.amazon.awssdk.enhanced.dynamodb.AttributeValueType;
import software.amazon.awssdk.enhanced.dynamodb.EnhancedType;
import software.amazon.awssdk.services.dynamodb.model.AttributeValue;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class SubscriptionConverter implements AttributeConverter<List<Subscription>> {
    @Override
    public AttributeValue transformFrom(List<Subscription> subscriptions) {
        return AttributeValue.builder().l(subscriptions.stream()
                .map(subscription -> AttributeValue.builder().m(
                                Map.of(
                                        "fundId", AttributeValue.builder().s(subscription.getFundId()).build(),
                                        "investmentAmount", AttributeValue.builder().n(subscription.getInvestmentAmount().toString()).build()
                                )
                        ).build()
                )
                .toList()
        ).build();
    }

    @Override
    public List<Subscription> transformTo(AttributeValue attributeValue) {
        return Optional.ofNullable(attributeValue)
                .map(AttributeValue::l)
                .map(values -> mapAttributes(attributeValue))
                .orElseGet(ArrayList::new);

    }

    @Override
    public EnhancedType<List<Subscription>> type() {
        return EnhancedType.listOf(Subscription.class);
    }

    @Override
    public AttributeValueType attributeValueType() {
        return AttributeValueType.L;
    }

    private List<Subscription> mapAttributes(AttributeValue attributeValue) {
        return attributeValue.l().stream()
                .map(data -> Subscription.builder()
                        .fundId(Optional.ofNullable(data.m().get("fundId"))
                                .map(AttributeValue::s)
                                .orElse(null))
                        .investmentAmount(Optional.ofNullable(data.m().get("investmentAmount"))
                                .map(AttributeValue::n)
                                .map(BigDecimal::new)
                                .orElse(null))
                        .build())
                .toList();
    }
}
