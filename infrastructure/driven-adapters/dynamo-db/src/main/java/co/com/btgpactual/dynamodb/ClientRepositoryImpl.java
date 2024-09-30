package co.com.btgpactual.dynamodb;

import co.com.btgpactual.dynamodb.entity.ClientEntity;
import co.com.btgpactual.dynamodb.helper.EntityMapper;
import co.com.btgpactual.exception.technical.TechnicalException;
import co.com.btgpactual.model.client.Client;
import co.com.btgpactual.model.client.gateways.ClientRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.enhanced.dynamodb.model.GetItemEnhancedRequest;
import software.amazon.awssdk.services.dynamodb.model.DynamoDbException;

import java.util.concurrent.TimeoutException;

import static co.com.btgpactual.exception.technical.TechnicalErrorType.DATABASE_CONNECTION_ERROR;
import static co.com.btgpactual.exception.technical.TechnicalErrorType.TIMEOUT_EXCEPTION;

@Repository
public class ClientRepositoryImpl implements ClientRepository {
    private DynamoDbAsyncTable<ClientEntity> clientTable;
    private EntityMapper entityMapper;

    @Autowired
    public ClientRepositoryImpl(DynamoDbEnhancedAsyncClient enhancedAsyncClient,
                                @Value("${aws.dynamodb.clients-table}") String dynamoDbTableName,
                                EntityMapper entityMapper) {
        this.entityMapper = entityMapper;
        this.clientTable = enhancedAsyncClient.table(dynamoDbTableName,
                TableSchema.fromBean(ClientEntity.class));
    }

    @Override
    public Mono<Client> create(Client client) {
        return update(client);
    }

    @Override
    public Mono<Client> read(String clientId) {
        return Mono.fromFuture(clientTable.getItem(GetItemEnhancedRequest.builder()
                .key(k -> k.partitionValue(clientId))
                .build()))
                .map(entityMapper::toClient);
    }

    @Override
    public Mono<Client> update(Client client) {
        return Mono.fromFuture(clientTable.updateItem(entityMapper.toClientEntity(client)))
                .map(entityMapper::toClient)
                .onErrorMap(TimeoutException.class, ex ->
                        new TechnicalException(ex, TIMEOUT_EXCEPTION))
                .onErrorMap(DynamoDbException.class, ex ->
                        new TechnicalException(ex, DATABASE_CONNECTION_ERROR));
    }
}
