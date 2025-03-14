package dev.notyouraverage.project.base.enums;

import dev.notyouraverage.project.base.constants.Constants;
import dev.notyouraverage.project.base.dtos.response.wrapper.ErrorCodeTrait;
import lombok.Getter;
import lombok.ToString;

@Getter
@ToString
public enum ErrorCode implements ErrorCodeTrait {
    INTERNAL_SERVER_ERROR("001", "Internal Server Error"),
    INPUT_VALIDATION_ERROR("002", "Input Validation Error"),
    NO_HANDLER_FOUND("003", "No Handler Found"),
    FORBIDDEN("004", "Forbidden to access the resource"),
    UNAUTHORIZED("005", "Unauthorized access"),
    ;

    private final String code;

    private final String message;

    ErrorCode(String code, String message) {
        this.code = Constants.BASE_ERROR_CODE_PREFIX + code;
        this.message = message;
    }
}
