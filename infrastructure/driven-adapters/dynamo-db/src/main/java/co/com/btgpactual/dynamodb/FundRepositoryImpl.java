package co.com.btgpactual.dynamodb;

import co.com.btgpactual.dynamodb.entity.FundEntity;
import co.com.btgpactual.dynamodb.helper.EntityMapper;
import co.com.btgpactual.model.fund.Fund;
import co.com.btgpactual.model.fund.gateways.FundRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbAsyncTable;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedAsyncClient;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;

@Repository
public class FundRepositoryImpl implements FundRepository {
    private DynamoDbAsyncTable<FundEntity> fundTable;
    private EntityMapper entityMapper;

    @Autowired
    public FundRepositoryImpl(DynamoDbEnhancedAsyncClient enhancedAsyncClient,
                                @Value("${aws.dynamodb.funds-table}") String dynamoDbTableName,
                                EntityMapper entityMapper) {
        this.entityMapper = entityMapper;
        this.fundTable = enhancedAsyncClient.table(dynamoDbTableName,
                TableSchema.fromBean(FundEntity.class));
    }

    @Override
    public Flux<Fund> readAll() {
        return Flux.from(fundTable.scan().items())
                .map(entityMapper::toFund);
    }

    @Override
    public Mono<Fund> read(String fundId) {
        return Mono.fromFuture(fundTable.getItem(request ->
                        request.key(key -> key.partitionValue(fundId))))
                .map(entityMapper::toFund);
    }
}
