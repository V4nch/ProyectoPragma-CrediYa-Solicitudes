package co.com.pragma.powerup.model.debtcapacity.response;

import co.com.pragma.powerup.model.debtcapacity.PaymentPlan;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class CapacityResponse {
    private Long loanId;
    private Double maxCapacity;
    private Double amount;
    private Double currentMonthlyDebt;
    private Double availableCapacity;
    private String baseSalary;
    private Double newLoanPayment;
    private List<PaymentPlan> paymentPlan;
}
