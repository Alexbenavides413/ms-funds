package co.com.btgpactual.usecase.client.validations;

import co.com.btgpactual.model.client.Client;
import co.com.btgpactual.model.exception.BusinessException;
import co.com.btgpactual.model.fund.Fund;
import lombok.experimental.UtilityClass;

import java.math.BigDecimal;

import static co.com.btgpactual.model.exception.BusinessErrorType.*;
import static co.com.btgpactual.model.exception.BusinessErrorType.INSUFFICIENT_BALANCE_FOR_FUND_ENROLLMENT;

@UtilityClass
public class ClientValidation {

    public static Boolean isSubscriptionRequestValid(Client client, Fund fund, BigDecimal amount){
        if(client.getSubscriptions().stream().anyMatch(subscription -> subscription.getFundId().equals(fund.getId()))){
            throw new BusinessException(SUBSCRIPTION_ALREADY_EXISTS);
        }
        BigDecimal balanceAfterOperation = client.getBalance().subtract(amount);
        if(balanceAfterOperation.compareTo(BigDecimal.ZERO) < 0) {
            throw new BusinessException(INSUFFICIENT_BALANCE_FOR_FUND_ENROLLMENT, fund.getName());
        }
        if(amount.compareTo(fund.getMinimumInitialInvestment()) < 0){
            throw new BusinessException(MINIMUM_INVESTMENT_AMOUNT_NOT_MET);
        }
        return Boolean.TRUE;
    }
}
