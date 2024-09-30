package co.com.btgpactual.usecase.fund;

import co.com.btgpactual.model.fund.Fund;
import co.com.btgpactual.model.fund.gateways.FundRepository;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@RequiredArgsConstructor
public class FundUseCase {
    private final FundRepository fundRepository;

    public Flux<Fund> readAll(){
        return fundRepository.readAll();
    }
}
