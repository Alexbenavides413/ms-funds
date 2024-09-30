package co.com.btgpactual.exception;

import co.com.btgpactual.exception.model.ErrorResponseBodyDTO;
import co.com.btgpactual.exception.model.ErrorResponseModelDTO;
import co.com.btgpactual.exception.technical.TechnicalException;
import co.com.btgpactual.model.exception.BusinessException;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.boot.autoconfigure.web.WebProperties;
import org.springframework.boot.autoconfigure.web.reactive.error.AbstractErrorWebExceptionHandler;
import org.springframework.boot.web.reactive.error.ErrorAttributes;
import org.springframework.context.ApplicationContext;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.codec.ServerCodecConfigurer;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import java.util.List;

import static co.com.btgpactual.exception.technical.TechnicalErrorType.UNEXPECTED_EXCEPTION;

@Order(-2)
@Component
@Log4j2
public class GlobalExceptionHandler extends AbstractErrorWebExceptionHandler {

    Logger logger = LogManager.getLogger(getClass());

    public GlobalExceptionHandler(ErrorAttributes errorAttributes,
                            ApplicationContext applicationContext,
                            ServerCodecConfigurer serverCodecConfigurer) {
        super(errorAttributes, new WebProperties.Resources(), applicationContext);
        this.setMessageWriters(serverCodecConfigurer.getWriters());
    }

    @Override
    protected RouterFunction<ServerResponse> getRoutingFunction(ErrorAttributes errorAttributes) {
        return RouterFunctions.route(RequestPredicates.all(), this::buildErrorResponse);
    }

    private Mono<ServerResponse> buildErrorResponse(ServerRequest serverRequest) {
        return Mono.just(serverRequest)
                .map(this::getError)
                .flatMap(Mono::error)
                .onErrorResume(TechnicalException.class, this::buildResponseBody)
                .onErrorResume(BusinessException.class, this::buildResponseBody)
                .onErrorResume(this::buildResponseBody)
                .cast(Tuple2.class)
                .flatMap(tuple -> this.buildResponse((ErrorResponseBodyDTO) tuple.getT1(), (HttpStatus) tuple.getT2()))
                .doAfterTerminate(() -> logger.error(getError(serverRequest)));
    }

    private Mono<Tuple2<ErrorResponseBodyDTO, HttpStatus>> buildResponseBody(BusinessException businessException) {
        return Mono.just(ErrorResponseBodyDTO.builder().errors(List.of(ErrorResponseModelDTO.builder()
                                .code(businessException.getBusinessErrorType().getCode())
                                .message(businessException.getMessage())
                                .build()))
                        .build())
                .zipWith(Mono.just(HttpStatus
                        .resolve(businessException.getBusinessErrorType().getStatusCode())));
    }

    private Mono<Tuple2<ErrorResponseBodyDTO, HttpStatus>> buildResponseBody(Throwable throwable) {
        return Mono.just(ErrorResponseBodyDTO.builder().errors(List.of(ErrorResponseModelDTO.builder()
                                .code(UNEXPECTED_EXCEPTION.getCode())
                                .message(UNEXPECTED_EXCEPTION.getMessage())
                                .build()))
                        .build())
                .zipWith(Mono.just(HttpStatus.INTERNAL_SERVER_ERROR));
    }

    public Mono<ServerResponse> buildResponse(ErrorResponseBodyDTO body, HttpStatus httpStatus) {
        return ServerResponse.status(httpStatus)
                .contentType(MediaType.APPLICATION_JSON)
                .body(Mono.just(body), ErrorResponseBodyDTO.class);
    }
}
