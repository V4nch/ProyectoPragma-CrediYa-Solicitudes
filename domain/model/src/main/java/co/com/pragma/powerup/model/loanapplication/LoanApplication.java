package co.com.pragma.powerup.model.loanapplication;
import co.com.pragma.powerup.model.loantype.LoanType;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class LoanApplication {

        private Long loanId;
        private String email;
        private Double amount;
        private Integer term;
        private Long idLoanType;
        private Long idStatus;
        private LoanType loanType;

    public LoanApplication(Double amount, Integer term, Long idLoanType) {
        this.amount = amount;
        this.term = term;
        this.idLoanType = idLoanType;
    }

    public LoanApplication(String email, Double amount, Integer term, Long idLoanType, Long idStatus) {
        this.email=email;
        this.amount = amount;
        this.term = term;
        this.idLoanType = idLoanType;
        this.idStatus=idStatus;
    }
}
