package co.com.btgpactual.model.fund;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Setter
@Getter
@Builder
public class Fund {
    private String id;
    private String name;
    private BigDecimal minimumInitialInvestment;
    private String category;
}
