package co.com.pragma.powerup.sqs.listener;

import co.com.pragma.powerup.model.debtcapacity.LoanStatusMessage;
import co.com.pragma.powerup.model.loanapplication.request.UpdateLoanStatusRequest;
import co.com.pragma.powerup.usecase.loanapplication.LoanApplicationUseCase;
import lombok.RequiredArgsConstructor;

import lombok.extern.log4j.Log4j2;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

@Log4j2
@Service
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {

    private final ObjectMapper objectMapper;
    private final LoanApplicationUseCase loanApplicationUseCase;

    @Override
    public Mono<Void> apply(Message message) {
        try {
            log.info("Raw body: {}", message.body());

            LoanStatusMessage msge = objectMapper.readValue(message.body(), LoanStatusMessage.class);
            log.info("Recieved messagge from SQS: {}", msge);

            return loanApplicationUseCase.putLoanApp(
                    new UpdateLoanStatusRequest(msge.getLoanId(), msge.getStatus())
            ).then();
        } catch (Exception e) {
            log.error("Error parsing message: {}", message.body(), e);
            return Mono.error(e);
        }
    }
}