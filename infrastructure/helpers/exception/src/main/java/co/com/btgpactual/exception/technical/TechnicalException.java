package co.com.btgpactual.exception.technical;

import lombok.Getter;

@Getter
public class TechnicalException extends RuntimeException {
    private final TechnicalErrorType errorMessage;
    public TechnicalException(Throwable cause, TechnicalErrorType technicalErrorMessage) {
        super(cause);
        this.errorMessage = technicalErrorMessage;
    }
}
