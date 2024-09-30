package co.com.btgpactual.model.exception;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BusinessErrorType {
    CLIENT_NOT_FOUND("FB0001","Cliente no encontrado", 404),
    FUND_NOT_FOUND("FB0002","Fondo no encontrado", 404),
    INSUFFICIENT_BALANCE_FOR_FUND_ENROLLMENT("FB0003","No tiene saldo disponible para vincularse al fondo %s", 400),
    MINIMUM_INVESTMENT_AMOUNT_NOT_MET("FB0004", "El monto de vinculación es inferior al monto mínimo de vinculación al fondo", 400),
    CLIENT_ID_REQUIRED("FB0005", "Es necesario incluir el 'client-id' en la solicitud para poder procesarla correctamente. Por favor, asegúrese de enviar este valor para continuar.", 400),
    FUND_ID_REQUIRED("FB0006", "Es necesario incluir el 'fund-id' en la solicitud para poder procesarla correctamente. Por favor, asegúrese de enviar este valor para continuar.", 400),
    NO_ACTIVE_SUBSCRIPTIONS_FOUND("FB0007", "No se puede eliminar la suscripción ya que el cliente no tiene suscripciones activas", 400),
    SUBSCRIPTION_ALREADY_EXISTS("FB0008", "El cliente ya se encuentra suscrito al fondo especificado", 400),
    NOTIFICATION_TYPE_NOT_SUPPORTED("FB0009", "El medio de notificación especificado no es soportado", 400),
    NOT_SUBSCRIBED_TO_FUND("FB0010", "El cliente no está suscrito al fondo especificado", 400);

    private final String code;
    private final String message;
    private final int statusCode;
}
