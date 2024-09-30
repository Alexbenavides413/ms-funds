package co.com.btgpactual.api.helper.request;

import java.math.BigDecimal;

public record SubscribeRequest (String clientId,
                                String fundId,
                                BigDecimal investmentAmount,
                                String notificationType){}
