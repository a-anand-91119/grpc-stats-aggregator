package dev.notyouraverage.project.base.filters;

import dev.notyouraverage.project.base.constants.Constants;
import dev.notyouraverage.project.base.constants.FilterOrderingConstants;
import dev.notyouraverage.project.base.constants.HeaderConstants;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Locale;

@Component(value = Constants.TEST)
@Order(value = FilterOrderingConstants.CORS_FILTER_ORDER)
public class CorsFilter extends OncePerRequestFilter {

    private final String swaggerPrefix;

    private final String uiAppHosts;

    public CorsFilter(
            @Value("${server.servlet.context-path:}") String contextPath,
            @Value("${project.ui-app.hosts}") String uiAppHosts
    ) {
        this.swaggerPrefix = String.format("%s/%s", contextPath, "swagger-ui");
        this.uiAppHosts = uiAppHosts;
    }

    @Override
    protected void doFilterInternal(
            @NonNull HttpServletRequest request,
            @NonNull HttpServletResponse response,
            @NonNull FilterChain filterChain
    ) throws ServletException, IOException {
        response.setHeader(HeaderConstants.ACCESS_CONTROL_ALLOW_CREDENTIALS, "true");
        response.setHeader(
                HeaderConstants.ACCESS_CONTROL_ALLOW_HEADERS,
                "X-Requested-With, Authorization, Content-Type"
        );
        response.setHeader(HeaderConstants.ACCESS_CONTROL_ALLOW_METHODS, "POST, GET, PUT, OPTIONS, DELETE");
        response.setHeader(HeaderConstants.ACCESS_CONTROL_EXPOSE_HEADERS, "Client-App-Version");
        if (uiAppHosts.equals("*")) {
            response.setHeader(HeaderConstants.ACCESS_CONTROL_ALLOW_ORIGIN, request.getHeader(HeaderConstants.ORIGIN));
        } else {
            response.setHeader(HeaderConstants.ACCESS_CONTROL_ALLOW_ORIGIN, uiAppHosts);
        }
        response.setHeader(HeaderConstants.ACCESS_CONTROL_MAX_AGE, "3600");
        response.setHeader(HeaderConstants.CACHE_CONTROL, "no-store, max-age=0");
        if (!request.getRequestURI().startsWith(swaggerPrefix)) {
            response.setHeader(HeaderConstants.CONTENT_SECURITY_POLICY, "default-src 'none'");
        }
        response.setHeader(HeaderConstants.CROSS_ORIGIN_EMBEDDER_POLICY, "require-corp");
        response.setHeader(HeaderConstants.CROSS_ORIGIN_OPENER_POLICY, "same-origin");
        response.setHeader(HeaderConstants.CROSS_ORIGIN_RESOURCE_POLICY, "same-origin");
        response.setHeader(HeaderConstants.PERMISSIONS_POLICY, HeaderConstants.PERMISSIONS_POLICY_VALUE);
        response.setHeader(HeaderConstants.PRAGMA, "no-cache");
        response.setHeader(HeaderConstants.REFERRER_POLICY, "no-referrer");
        response.setHeader(HeaderConstants.STRICT_TRANSPORT_SECURITY, "max-age=31536000 ; includeSubDomains");
        response.setHeader(HeaderConstants.X_CONTENT_TYPE_OPTIONS, "nosniff");
        response.setHeader(HeaderConstants.X_FRAME_OPTIONS, "DENY");
        response.setHeader(HeaderConstants.X_PERMITTED_CROSS_DOMAIN_POLICIES, "none");

        if (HttpMethod.OPTIONS.matches(request.getMethod().toUpperCase(Locale.getDefault()))) {
            response.setStatus(HttpServletResponse.SC_OK);
            return;
        }

        filterChain.doFilter(request, response);
    }
}
