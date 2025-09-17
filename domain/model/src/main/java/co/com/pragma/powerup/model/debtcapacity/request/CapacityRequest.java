package co.com.pragma.powerup.model.debtcapacity.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class CapacityRequest {
    private String idCard;
    private Double amount;
    private Integer term;
    private Double interestRate;

}
