package co.com.pragma.powerup.model.report.gateways;

import reactor.core.publisher.Mono;

public interface ReportSQSRepository {
    Mono<String> sendReport(String message);
}
