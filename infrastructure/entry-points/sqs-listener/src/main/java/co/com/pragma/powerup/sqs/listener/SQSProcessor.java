package co.com.pragma.powerup.sqs.listener;

import co.com.pragma.powerup.model.debtcapacity.LoanStatusMessage;
import co.com.pragma.powerup.model.loanapplication.request.UpdateLoanStatusRequest;
import co.com.pragma.powerup.usecase.loanapplication.LoanApplicationUseCase;
import lombok.RequiredArgsConstructor;

import org.reactivecommons.utils.ObjectMapper;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.function.Function;

@Service
@RequiredArgsConstructor
public class SQSProcessor implements Function<Message, Mono<Void>> {

    private final ObjectMapper objectMapper;
    private final LoanApplicationUseCase loanApplicationUseCase;

    @Override
    public Mono<Void> apply(Message message) {
        return Mono.just(objectMapper.map(message.body(), LoanStatusMessage.class))
                .flatMap(msg ->
                     loanApplicationUseCase.putLoanApp(new UpdateLoanStatusRequest(msg.getLoanId(), msg.getStatus()))
                     .then()
                )
                ;
    }
}