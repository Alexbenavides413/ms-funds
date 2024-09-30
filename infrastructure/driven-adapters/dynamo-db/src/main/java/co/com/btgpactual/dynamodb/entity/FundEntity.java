package co.com.btgpactual.dynamodb.entity;

import lombok.Setter;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbAttribute;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbBean;
import software.amazon.awssdk.enhanced.dynamodb.mapper.annotations.DynamoDbPartitionKey;

@Setter
@DynamoDbBean
public class FundEntity {
    private String id;
    private String name;
    private String minimumInitialInvestment;
    private String category;

    @DynamoDbPartitionKey
    @DynamoDbAttribute("id")
    public String getId() {
        return id;
    }
    @DynamoDbAttribute("name")
    public String getName() {
        return name;
    }
    @DynamoDbAttribute("minimumInitialInvestment")
    public String getMinimumInitialInvestment() {
        return minimumInitialInvestment;
    }
    @DynamoDbAttribute("category")
    public String getCategory() {
        return category;
    }
}
