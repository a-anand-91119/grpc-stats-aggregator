package dev.notyouraverage.project.base.filters;

import dev.notyouraverage.project.base.configurations.RequestContext;
import dev.notyouraverage.project.base.constants.FilterOrderingConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Instant;
import java.util.UUID;

@Component
@Order(value = FilterOrderingConstants.REQUEST_METADATA_INITIALIZER_FILTER_ORDER)
public class RequestMetadataInitializerFilter extends OncePerRequestFilter {

    private final RequestContext requestContext;

    @Autowired
    public RequestMetadataInitializerFilter(RequestContext requestContext) {
        this.requestContext = requestContext;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        requestContext.setRequestId(UUID.randomUUID().toString());
        requestContext.setRequestArrivalTime(Instant.now().toEpochMilli());
        filterChain.doFilter(request, response);
    }
}
