package dev.notyouraverage.project.one.grpc.aggregator.grpc_stats_aggregator.configurations;

import org.apache.coyote.UpgradeProtocol;
import org.apache.coyote.http2.Http2Protocol;
import org.springframework.boot.web.embedded.tomcat.TomcatConnectorCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ApplicationConfiguration {

    @Bean
    public TomcatConnectorCustomizer customizer() {
        return (connector) -> {
            for (UpgradeProtocol protocol : connector.findUpgradeProtocols()) {
                if (protocol instanceof Http2Protocol http2Protocol) {
                    http2Protocol.setOverheadWindowUpdateThreshold(0);
                }
            }
        };
    }

}
