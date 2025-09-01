package co.com.pragma.powerup.model.loanapplication.response;

import co.com.pragma.powerup.model.loanapplication.LoanApplication;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class ResponseLoanApplication {
    private LoanApplication loanApplication;
    private String statusLoanApplication;
}
