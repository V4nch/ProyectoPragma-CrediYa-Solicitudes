package co.com.pragma.powerup.model.debtcapacity;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class PaymentPlan {
    private Integer month;
    private Double totalPayment;
    private Double principal;
    private Double interest;
    private Double remainingBalance;
}
