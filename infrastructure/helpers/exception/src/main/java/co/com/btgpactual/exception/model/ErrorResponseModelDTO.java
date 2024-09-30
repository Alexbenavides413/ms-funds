package co.com.btgpactual.exception.model;

import lombok.Builder;

@Builder
public record ErrorResponseModelDTO (String code, String message){}
