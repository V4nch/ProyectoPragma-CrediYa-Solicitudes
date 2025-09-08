package co.com.pragma.powerup.model.loanapplication.request;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplicationRequest {

    private String idCard;
    private Double amount;
    private Integer term;
    private Long idLoanType;
}
