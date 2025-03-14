package dev.notyouraverage.project.base.constants;

import org.springframework.core.Ordered;

public class FilterOrderingConstants {

    public static final int CORS_FILTER_ORDER = Ordered.HIGHEST_PRECEDENCE;

    public static final int REQUEST_METADATA_INITIALIZER_FILTER_ORDER = Ordered.HIGHEST_PRECEDENCE + 1;

    public static final int HTTP_LOGGING_FILTER_ORDER = Ordered.HIGHEST_PRECEDENCE + 2;

    private FilterOrderingConstants() {
    }
}
