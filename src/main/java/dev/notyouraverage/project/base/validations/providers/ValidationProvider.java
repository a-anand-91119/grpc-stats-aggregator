package dev.notyouraverage.project.base.validations.providers;

import java.util.function.Function;

@FunctionalInterface
public interface ValidationProvider<T> extends Function<T, ValidationResult> {

    default ValidationProvider<T> and(ValidationProvider<? super T> other) {
        return (T t) -> {
            ValidationResult result = this.apply(t);
            return result.isValid() ? other.apply(t) : result;
        };
    }

    static <T> ValidationProvider<T> from(Function<? super T, ? extends ValidationResult> function) {
        return function::apply;
    }
}
