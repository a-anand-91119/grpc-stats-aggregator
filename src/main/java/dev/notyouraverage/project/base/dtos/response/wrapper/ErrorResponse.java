package dev.notyouraverage.project.base.dtos.response.wrapper;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AccessLevel;
import lombok.Builder;

import java.io.Serializable;

@Builder(access = AccessLevel.PRIVATE)
public record ErrorResponse(
        String errorCode,
        String message,
        @JsonInclude(JsonInclude.Include.NON_NULL) String detail
) implements Serializable {
    public static ErrorResponse from(ErrorCodeTrait errorCodeTrait) {
        return ErrorResponse.builder()
                .errorCode(errorCodeTrait.getCode())
                .message(errorCodeTrait.getMessage())
                .build();
    }

    public static ErrorResponse from(ErrorCodeTrait errorCodeTrait, String details) {
        return ErrorResponse.builder()
                .errorCode(errorCodeTrait.getCode())
                .message(errorCodeTrait.getMessage())
                .detail(details)
                .build();
    }

}
