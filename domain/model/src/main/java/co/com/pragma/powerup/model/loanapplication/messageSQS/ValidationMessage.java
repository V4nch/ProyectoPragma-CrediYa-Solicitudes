package co.com.pragma.powerup.model.loanapplication.messageSQS;

import co.com.pragma.powerup.model.debtcapacity.PaymentPlan;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class ValidationMessage {
    private Long loanId;
    private Double amount;
    private Double newLoanPayment;
    private Double availableCapacity;
    private String baseSalary;
    private List<PaymentPlan> paymentPlan;
}
