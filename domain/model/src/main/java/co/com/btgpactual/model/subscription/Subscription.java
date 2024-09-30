package co.com.btgpactual.model.subscription;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
public class Subscription {
    private String fundId;
    private BigDecimal investmentAmount;
}
