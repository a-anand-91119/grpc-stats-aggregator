package dev.notyouraverage.project.base.filters;

import dev.notyouraverage.project.base.configurations.RequestContext;
import dev.notyouraverage.project.base.constants.Constants;
import dev.notyouraverage.project.base.constants.FilterOrderingConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.MDC;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Set;

@Component
@Order(value = FilterOrderingConstants.HTTP_LOGGING_FILTER_ORDER)
@Slf4j
public class HttpLoggingFilter extends OncePerRequestFilter {
    private static final Set<String> ignoredPaths = Set.of("/health");

    private final RequestContext requestContext;

    public HttpLoggingFilter(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

    private static boolean shouldLogRequest(HttpServletRequest request) {
        return !ignoredPaths.contains(request.getRequestURI());
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        try (MDC.MDCCloseable ignored = MDC.putCloseable(Constants.REQUEST_ID, requestContext.getRequestId())) {
            filterChain.doFilter(request, response);
            if (shouldLogRequest(request)) {
                log.info("Request to {}: {} - {}", request.getMethod(), formURI(request), response.getStatus());
            }
        }
    }

    private String formURI(HttpServletRequest request) {
        String queryString = request.getQueryString() == null ? "" : "?" + request.getQueryString();
        return request.getRequestURI() + queryString;
    }
}
