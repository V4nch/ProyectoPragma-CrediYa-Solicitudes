package co.com.pragma.powerup.model.loanapplication.messageSQS;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class ValidationMessage {
    private String idCard;
    private Double amount;
    private Integer term;
    private Double interestRate;
}
