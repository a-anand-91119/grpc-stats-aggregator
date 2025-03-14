package dev.notyouraverage.project.one.grpc.aggregator.grpc_stats_aggregator;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@ComponentScan(basePackages = { "dev.notyouraverage.project.base", "dev.notyouraverage.project.one" })
public class GrpcStatsAggregatorApplication {

    public static void main(String[] args) {
        SpringApplication.run(GrpcStatsAggregatorApplication.class, args);
    }

}
