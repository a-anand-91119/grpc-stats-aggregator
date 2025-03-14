package dev.notyouraverage.project.base.validations.providers;

import dev.notyouraverage.project.base.dtos.response.wrapper.ErrorCodeTrait;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.function.Consumer;

@Data
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class ValidationResult {
    private final boolean isValid;

    private final ErrorCodeTrait errorCode;

    public static ValidationResult valid() {
        return new ValidationResult(true, null);
    }

    public static ValidationResult invalid(ErrorCodeTrait errorCode) {
        return new ValidationResult(false, errorCode);
    }

    public void onError(Consumer<ValidationResult> failureConsumer) {
        if (!isValid)
            failureConsumer.accept(this);
    }
}
