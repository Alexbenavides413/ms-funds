package co.com.btgpactual.dynamodb.helper;

import co.com.btgpactual.dynamodb.entity.ClientEntity;
import co.com.btgpactual.dynamodb.entity.FundEntity;
import co.com.btgpactual.model.client.Client;
import co.com.btgpactual.model.fund.Fund;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface EntityMapper {
    Client toClient(ClientEntity clientEntity);
    ClientEntity toClientEntity(Client client);

    Fund toFund(FundEntity fundEntity);
}
