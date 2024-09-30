package co.com.btgpactual.exception.technical;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum TechnicalErrorType {
    UNEXPECTED_EXCEPTION("FT0001","Ocurrió un error de excepción inesperado"),
    DATABASE_CONNECTION_ERROR("FT0002","Ocurrió un error al intentar conectar a la base de datos"),
    TIMEOUT_EXCEPTION("FT0003","Ocurrió un error de tiempo de espera al intentar conectar a la base de datos"),
    SNS_CONNECTION_ERROR("FT0004","Ocurrió un error al intentar conectar al sevicio de SNS");

    private final String code;
    private final String message;
}
