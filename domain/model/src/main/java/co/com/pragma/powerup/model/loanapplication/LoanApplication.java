package co.com.pragma.powerup.model.loanapplication;
import lombok.Builder;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;



@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class LoanApplication {

        private Long loanId;
        private String email;
        private Double amount;
        private Integer term;
        private Long idLoanType;
        private Long idStatus;

}
