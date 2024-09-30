package co.com.btgpactual.model.client.gateways;

import co.com.btgpactual.model.client.Client;
import reactor.core.publisher.Mono;

public interface ClientRepository {
    Mono<Client> read(String clientId);
    Mono<Client> update(Client client);
}
