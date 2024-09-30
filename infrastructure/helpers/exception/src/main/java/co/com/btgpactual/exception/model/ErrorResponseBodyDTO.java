package co.com.btgpactual.exception.model;

import lombok.Builder;

import java.util.List;

@Builder
public record ErrorResponseBodyDTO (List<ErrorResponseModelDTO> errors){}
