package dev.notyouraverage.project.base.configurations;

import lombok.Getter;
import lombok.Setter;
import org.springframework.stereotype.Component;
import org.springframework.web.context.annotation.RequestScope;

@RequestScope
@Component
@Getter
@Setter
public class RequestContext {

    private String requestId;

    private Long requestArrivalTime;
}
