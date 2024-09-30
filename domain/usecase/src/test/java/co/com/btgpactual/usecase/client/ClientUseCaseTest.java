package co.com.btgpactual.usecase.client;

import co.com.btgpactual.model.client.Client;
import co.com.btgpactual.model.client.gateways.ClientRepository;
import co.com.btgpactual.model.exception.BusinessException;
import co.com.btgpactual.model.fund.gateways.FundRepository;
import co.com.btgpactual.model.notification.gateways.NotificationRepository;
import co.com.btgpactual.model.transaction.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static co.com.btgpactual.model.exception.BusinessErrorType.*;
import static co.com.btgpactual.usecase.util.UseCaseTestUtils.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class ClientUseCaseTest {
    @Mock
    private ClientRepository clientRepository;

    @Mock
    private FundRepository fundRepository;

    @Mock
    private NotificationRepository notificationRepository;

    @InjectMocks
    private ClientUseCase clientUseCase;

    @Test
    void removeSubscription_success() {
        when(clientRepository.read(CLIENT_ID)).thenReturn(Mono.just(getSubscribedClient()));
        when(clientRepository.update(any(Client.class))).thenReturn(Mono.just(getSubscribedClient()));

        StepVerifier.create(clientUseCase.removeSubscription(CLIENT_ID, FUND_ID))
                .expectNextMatches(transaction -> transaction.getType() == TransactionType.CANCELLATION)
                .verifyComplete();
    }

    @Test
    void removeSubscription_failed_clientNotFound() {
        when(clientRepository.read(CLIENT_ID)).thenReturn(Mono.empty());

        StepVerifier.create(clientUseCase.removeSubscription(CLIENT_ID, FUND_ID))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getBusinessErrorType() == CLIENT_NOT_FOUND)
                .verify();
    }

    @Test
    void removeSubscription_failed_noActiveSubscriptions() {
        when(clientRepository.read(CLIENT_ID)).thenReturn(Mono.just(getNewClient()));

        StepVerifier.create(clientUseCase.removeSubscription(CLIENT_ID, FUND_ID))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getBusinessErrorType() == NO_ACTIVE_SUBSCRIPTIONS_FOUND)
                .verify();
    }

    @Test
    void removeSubscription_failed_noSubscribedToFund() {
        when(clientRepository.read(CLIENT_ID)).thenReturn(Mono.just(getSubscribedClient()));

        StepVerifier.create(clientUseCase.removeSubscription(CLIENT_ID, FUND_ID_2))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getBusinessErrorType() == NOT_SUBSCRIBED_TO_FUND)
                .verify();
    }

    @Test
    void addSubscription_failed_clientNotFound() {
        when(clientRepository.read(CLIENT_ID)).thenReturn(Mono.empty());
        when(fundRepository.read(FUND_ID)).thenReturn(Mono.just(FPV_BTG_PACTUAL_RECAUDADORA));

        StepVerifier.create(clientUseCase.addSubscription(CLIENT_ID, FUND_ID, LOW_INVESTMENT_AMOUNT,
                        EMAIL_NOTIFICATION_TYPE))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getBusinessErrorType() == CLIENT_NOT_FOUND)
                .verify();
    }

    @Test
    void addSubscription_failed_fundNotFound() {
        when(clientRepository.read(CLIENT_ID)).thenReturn(Mono.just(getNewClient()));
        when(fundRepository.read(FUND_ID)).thenReturn(Mono.empty());

        StepVerifier.create(clientUseCase.addSubscription(CLIENT_ID, FUND_ID, LOW_INVESTMENT_AMOUNT,
                        EMAIL_NOTIFICATION_TYPE))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getBusinessErrorType() == FUND_NOT_FOUND)
                .verify();
    }

    @Test
    void addSubscription_failed_alreadySubscribedToFound() {
        when(clientRepository.read(CLIENT_ID)).thenReturn(Mono.just(getSubscribedClient()));
        when(fundRepository.read(FUND_ID)).thenReturn(Mono.just(FPV_BTG_PACTUAL_RECAUDADORA));

        StepVerifier.create(clientUseCase.addSubscription(CLIENT_ID, FUND_ID, HIGH_INVESTMENT_AMOUNT,
                        EMAIL_NOTIFICATION_TYPE))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getBusinessErrorType() == SUBSCRIPTION_ALREADY_EXISTS)
                .verify();
    }

    @Test
    void addSubscription_failed_insufficientBalance() {
        when(clientRepository.read(CLIENT_ID)).thenReturn(Mono.just(INSUFFICIENT_BALANCE_CLIENT));
        when(fundRepository.read(FUND_ID)).thenReturn(Mono.just(FPV_BTG_PACTUAL_RECAUDADORA));

        StepVerifier.create(clientUseCase.addSubscription(CLIENT_ID, FUND_ID, HIGH_INVESTMENT_AMOUNT,
                        EMAIL_NOTIFICATION_TYPE))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                        ((BusinessException) throwable).
                                getBusinessErrorType() == INSUFFICIENT_BALANCE_FOR_FUND_ENROLLMENT)
                .verify();
    }

    @Test
    void addSubscription_failed_minimumInvestmentAmountNotMet() {
        when(clientRepository.read(CLIENT_ID)).thenReturn(Mono.just(getNewClient()));
        when(fundRepository.read(FUND_ID)).thenReturn(Mono.just(FPV_BTG_PACTUAL_RECAUDADORA));

        StepVerifier.create(clientUseCase.addSubscription(CLIENT_ID, FUND_ID,
                        LOW_INVESTMENT_AMOUNT, EMAIL_NOTIFICATION_TYPE))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getBusinessErrorType() == MINIMUM_INVESTMENT_AMOUNT_NOT_MET)
                .verify();
    }

    @Test
    void addSubscription_failed_notificationTypeNotSupported() {
        when(clientRepository.read(CLIENT_ID)).thenReturn(Mono.just(getNewClient()));
        when(clientRepository.update(any(Client.class))).thenReturn(Mono.just(getSubscribedClient()));
        when(fundRepository.read(FUND_ID)).thenReturn(Mono.just(FPV_BTG_PACTUAL_RECAUDADORA));

        StepVerifier.create(clientUseCase.addSubscription(CLIENT_ID, FUND_ID,
                        HIGH_INVESTMENT_AMOUNT, "UNKNOWN"))
                .expectErrorMatches(throwable -> throwable instanceof BusinessException &&
                        ((BusinessException) throwable).getBusinessErrorType() == NOTIFICATION_TYPE_NOT_SUPPORTED)
                .verify();
    }

    @Test
    void addSubscription_success_emailNotification() {
        when(clientRepository.read(CLIENT_ID)).thenReturn(Mono.just(getNewClient()));
        when(fundRepository.read(FUND_ID)).thenReturn(Mono.just(FPV_BTG_PACTUAL_RECAUDADORA));
        when(clientRepository.update(any(Client.class))).thenReturn(Mono.just(getNewClient()));

        StepVerifier.create(clientUseCase.addSubscription(CLIENT_ID, FUND_ID, HIGH_INVESTMENT_AMOUNT,
                        EMAIL_NOTIFICATION_TYPE))
                .expectNextMatches(transaction -> transaction.getType() == TransactionType.SUBSCRIPTION)
                .verifyComplete();
    }

    @Test
    void addSubscription_success_smsNotification() {
        when(clientRepository.read(CLIENT_ID)).thenReturn(Mono.just(getNewClient()));
        when(fundRepository.read(FUND_ID)).thenReturn(Mono.just(FPV_BTG_PACTUAL_RECAUDADORA));
        when(clientRepository.update(any(Client.class))).thenReturn(Mono.just(getNewClient()));

        StepVerifier.create(clientUseCase.addSubscription(CLIENT_ID, FUND_ID, HIGH_INVESTMENT_AMOUNT,
                        SMS_NOTIFICATION_TYPE))
                .expectNextMatches(transaction -> transaction.getType() == TransactionType.SUBSCRIPTION)
                .verifyComplete();
    }

    @Test
    void getTransactions_success() {
        when(clientRepository.read(CLIENT_ID)).thenReturn(Mono.just(getSubscribedClient()));

        StepVerifier.create(clientUseCase.getTransactions(CLIENT_ID))
                .expectNextMatches(transactions -> transactions.size() == 1)
                .verifyComplete();
    }
}
