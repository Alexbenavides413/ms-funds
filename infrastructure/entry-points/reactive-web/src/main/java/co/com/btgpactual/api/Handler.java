package co.com.btgpactual.api;

import co.com.btgpactual.api.helper.request.SubscribeRequest;
import co.com.btgpactual.model.exception.BusinessException;
import co.com.btgpactual.model.fund.Fund;
import co.com.btgpactual.usecase.client.ClientUseCase;
import co.com.btgpactual.usecase.fund.FundUseCase;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import static co.com.btgpactual.api.helper.EntryPointsUtil.CLIENT_ID_HEADER;
import static co.com.btgpactual.api.helper.EntryPointsUtil.FUND_ID_HEADER;
import static co.com.btgpactual.model.exception.BusinessErrorType.CLIENT_ID_REQUIRED;
import static co.com.btgpactual.model.exception.BusinessErrorType.FUND_ID_REQUIRED;


@Component
@RequiredArgsConstructor
public class Handler {
    private final ClientUseCase clientUseCase;
    private final FundUseCase fundUseCase;

    public Mono<ServerResponse> getClientTransactions(ServerRequest serverRequest) {
        String clientId = serverRequest.queryParam(CLIENT_ID_HEADER)
                .orElseThrow(() -> new BusinessException(CLIENT_ID_REQUIRED));
        return clientUseCase.getTransactions(clientId)
                .flatMap(transactions -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(transactions));
    }

    public Mono<ServerResponse> getFunds() {
        return ServerResponse.ok()
                .contentType(MediaType.APPLICATION_JSON)
                .body(fundUseCase.readAll(), Fund.class);
    }

    public Mono<ServerResponse> subscribeToFund(ServerRequest serverRequest) {
        return serverRequest.bodyToMono(SubscribeRequest.class)
                .flatMap(request -> clientUseCase.addSubscription(request.clientId(), request.fundId(),
                        request.investmentAmount(), request.notificationType()))
                .flatMap(operationResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(operationResponse));
    }

    public Mono<ServerResponse> unsubscribeFromFund(ServerRequest serverRequest) {
        String clientId = serverRequest.queryParam(CLIENT_ID_HEADER)
                .orElseThrow(() -> new BusinessException(CLIENT_ID_REQUIRED));
        String fundId = serverRequest.queryParam(FUND_ID_HEADER)
                .orElseThrow(() -> new BusinessException(FUND_ID_REQUIRED));
        return clientUseCase.removeSubscription(clientId, fundId)
                .flatMap(operationResponse -> ServerResponse.ok()
                        .contentType(MediaType.APPLICATION_JSON)
                        .bodyValue(operationResponse));
    }
}
