package co.com.btgpactual.model.fund.gateways;

import co.com.btgpactual.model.fund.Fund;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface FundRepository {
    Flux<Fund> readAll();
    Mono<Fund> read(String fundId);
}
