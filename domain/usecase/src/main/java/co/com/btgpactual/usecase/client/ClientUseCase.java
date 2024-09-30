package co.com.btgpactual.usecase.client;

import co.com.btgpactual.model.client.Client;
import co.com.btgpactual.model.client.gateways.ClientRepository;
import co.com.btgpactual.model.exception.BusinessException;
import co.com.btgpactual.model.fund.Fund;
import co.com.btgpactual.model.fund.gateways.FundRepository;
import co.com.btgpactual.model.notification.Notification;
import co.com.btgpactual.model.notification.gateways.NotificationRepository;
import co.com.btgpactual.model.subscription.Subscription;
import co.com.btgpactual.model.transaction.Transaction;
import co.com.btgpactual.model.transaction.TransactionType;
import co.com.btgpactual.usecase.client.utils.NotificationType;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static co.com.btgpactual.model.exception.BusinessErrorType.*;
import static co.com.btgpactual.usecase.client.utils.UseCaseUtil.SUBSCRIPTION_SUCCESS_MESSAGE;
import static co.com.btgpactual.usecase.client.utils.UseCaseUtil.SUBSCRIPTION_SUCCESS_SUBJECT;
import static co.com.btgpactual.usecase.client.validations.ClientValidation.isSubscriptionRequestValid;


@RequiredArgsConstructor
public class ClientUseCase {
    private final ClientRepository clientRepository;
    private final FundRepository fundRepository;
    private final NotificationRepository notificationRepository;

    public Mono<Client> read(String clientId) {
        return clientRepository.read(clientId)
                .switchIfEmpty(Mono.error(new BusinessException(CLIENT_NOT_FOUND)));
    }

    public Mono<List<Transaction>> getTransactions(String clientId) {
        return this.read(clientId)
                .map(Client::getTransactions);
    }

    public Mono<Transaction> removeSubscription(String clientId, String fundId) {
        return this.read(clientId)
                .map(client -> updateClientRemoveSubscription(client, fundId))
                .flatMap(updatedClient -> clientRepository.update(updatedClient)
                        .thenReturn(updatedClient.getTransactions().get(updatedClient.getTransactions().size() - 1)));
    }

    public Mono<Transaction> addSubscription(String clientId, String fundId, BigDecimal investmentAmount,
                                             String notificationType) {
        return this.read(clientId)
                .zipWith(fundRepository.read(fundId)
                        .switchIfEmpty(Mono.error(new BusinessException(FUND_NOT_FOUND))))
                .filter(tuple -> isSubscriptionRequestValid(tuple.getT1(), tuple.getT2(), investmentAmount))
                .map(tuple -> updateClientAddSubscription(tuple.getT1(), tuple.getT2(), investmentAmount))
                .flatMap(tuple -> clientRepository.update(tuple.getT1())
                        .doOnSuccess(client -> sendSubscribedNotification(client, tuple.getT2().getName(),
                                notificationType))
                        .thenReturn(tuple.getT1().getTransactions().get(tuple.getT1().getTransactions().size() - 1)));
    }

    private Tuple2<Client, Fund> updateClientAddSubscription(Client client, Fund fund, BigDecimal amount) {
        client.setBalance(client.getBalance().subtract(amount));
        List<Subscription> clientSubscriptions = Optional.ofNullable(client.getSubscriptions())
                .orElse(new ArrayList<>());
        clientSubscriptions.add(Subscription.builder()
                .fundId(fund.getId())
                .investmentAmount(amount)
                .build());
        client.setSubscriptions(clientSubscriptions);

        List<Transaction> clientTransactions = Optional.ofNullable(client.getTransactions())
                .orElse(new ArrayList<>());
        clientTransactions.add(Transaction.builder().id(UUID.randomUUID())
                .type(TransactionType.SUBSCRIPTION)
                .fundId(fund.getId())
                .amount(amount)
                .date(LocalDateTime.now())
                .build());
        client.setTransactions(clientTransactions);
        return Tuples.of(client, fund);
    }

    private Client updateClientRemoveSubscription(Client client, String fundId) {
        List<Subscription> clientSubscriptions = Optional.ofNullable(client.getSubscriptions())
                .filter(subscriptions -> !subscriptions.isEmpty())
                .orElseThrow(() -> new BusinessException(NO_ACTIVE_SUBSCRIPTIONS_FOUND));

        Subscription subscriptionToRemove = clientSubscriptions.stream()
                .filter(subscription -> subscription.getFundId().equals(fundId))
                .findFirst()
                .orElseThrow(() -> new BusinessException(NOT_SUBSCRIBED_TO_FUND));

        clientSubscriptions.remove(subscriptionToRemove);

        List<Transaction> clientTransactions = Optional.ofNullable(client.getTransactions())
                .orElse(new ArrayList<>());
        clientTransactions.add(Transaction.builder().id(UUID.randomUUID())
                .type(TransactionType.CANCELLATION)
                .fundId(fundId)
                .amount(subscriptionToRemove.getInvestmentAmount())
                .date(LocalDateTime.now())
                .build());
        client.setTransactions(clientTransactions);

        client.setBalance(client.getBalance().add(subscriptionToRemove.getInvestmentAmount()));
        return client;
    }

    private void sendSubscribedNotification(Client client, String fundName, String notificationType) {
        Notification notification = Notification.builder()
                .message(String.format(SUBSCRIPTION_SUCCESS_MESSAGE, fundName))
                .subject(SUBSCRIPTION_SUCCESS_SUBJECT)
                .build();
        try {
            NotificationType type = NotificationType.valueOf(notificationType.toUpperCase());
            if (type == NotificationType.EMAIL) {
                notificationRepository.sendEmailNotification(client.getEmail(), notification);
            } else if (type == NotificationType.SMS) {
                notificationRepository.sendSmsNotification(client.getPhoneNumber(), notification);
            }
        } catch (IllegalArgumentException e) {
            throw new BusinessException(NOTIFICATION_TYPE_NOT_SUPPORTED);
        }
    }
}