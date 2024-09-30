package co.com.btgpactual.model.exception;

import lombok.Getter;

@Getter
public class BusinessException extends RuntimeException {
    private final BusinessErrorType businessErrorType;
    public BusinessException(BusinessErrorType businessErrorType) {
        super(businessErrorType.getMessage());
        this.businessErrorType = businessErrorType;
    }
    public BusinessException(BusinessErrorType businessErrorType, String messageDetail) {
        super(String.format( businessErrorType.getMessage(), messageDetail));
        this.businessErrorType = businessErrorType;
    }
}
