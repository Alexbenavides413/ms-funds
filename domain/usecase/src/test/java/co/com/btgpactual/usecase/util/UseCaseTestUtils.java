package co.com.btgpactual.usecase.util;

import co.com.btgpactual.model.client.Client;
import co.com.btgpactual.model.fund.Fund;
import co.com.btgpactual.model.subscription.Subscription;
import co.com.btgpactual.model.transaction.Transaction;
import co.com.btgpactual.model.transaction.TransactionType;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@UtilityClass
public class UseCaseTestUtils {
    public static final String CLIENT_ID = "client-id";
    public static final String FUND_ID = "fund-id";
    public static final String FUND_ID_2 = "fund-id-2";
    public static final BigDecimal LOW_INVESTMENT_AMOUNT = BigDecimal.valueOf(100);
    public static final BigDecimal HIGH_INVESTMENT_AMOUNT = BigDecimal.valueOf(300000);
    public static final String EMAIL_NOTIFICATION_TYPE = "email";
    public static final String SMS_NOTIFICATION_TYPE = "sms";

    public static Client getNewClient() {
        return Client.builder()
                .id(CLIENT_ID)
                .balance(BigDecimal.valueOf(500000))
                .subscriptions(new ArrayList<>())
                .transactions(new ArrayList<>())
                .build();
    }

    public static Client getSubscribedClient(){
        return Client.builder()
                .id(CLIENT_ID)
                .balance(BigDecimal.valueOf(500000))
                .subscriptions(new ArrayList<>(List.of(Subscription.builder()
                        .fundId(FUND_ID)
                        .investmentAmount(LOW_INVESTMENT_AMOUNT)
                        .build())))
                .transactions(new ArrayList<>(List.of(Transaction.builder()
                        .id(UUID.randomUUID())
                        .type(TransactionType.SUBSCRIPTION)
                        .fundId(FUND_ID)
                        .amount(new BigDecimal("75000"))
                        .date(LocalDateTime.now())
                        .build())))
                .build();
    }

    public static final Client INSUFFICIENT_BALANCE_CLIENT = Client.builder()
            .id(CLIENT_ID)
            .balance(BigDecimal.valueOf(100))
            .subscriptions(new ArrayList<>())
            .transactions(new ArrayList<>())
            .build();

    public static final Fund FPV_BTG_PACTUAL_RECAUDADORA = Fund.builder()
            .id(FUND_ID)
            .name("FPV_BTG_PACTUAL_RECAUDADORA")
            .minimumInitialInvestment(BigDecimal.valueOf(75000))
            .category("FPV")
            .build();
}
