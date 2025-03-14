package dev.notyouraverage.project.base.dtos.response.wrapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import dev.notyouraverage.project.base.enums.ResponseStatus;

import java.time.LocalDateTime;
import java.util.List;

public record ResponseWrapper<T>(
        @JsonInclude(JsonInclude.Include.NON_NULL) T data,
        @JsonInclude(JsonInclude.Include.NON_EMPTY) List<ErrorResponse> errors,
        MetaResponse meta
) {

    public static <T> ResponseWrapper<T> success(T data) {
        MetaResponse metaResponse = MetaResponse.builder()
                .status(ResponseStatus.SUCCESS)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseWrapper<>(data, null, metaResponse);
    }

    public static <T> ResponseWrapper<T> failure(List<ErrorResponse> errorResponses) {
        MetaResponse metaResponse = MetaResponse.builder()
                .status(ResponseStatus.FAILURE)
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseWrapper<>(null, errorResponses, metaResponse);
    }

}
