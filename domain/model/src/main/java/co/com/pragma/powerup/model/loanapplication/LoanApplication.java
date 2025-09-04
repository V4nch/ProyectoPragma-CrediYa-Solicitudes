package co.com.pragma.powerup.model.loanapplication;
import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class LoanApplication {

        private String email;
        private Double amount;
        private Integer term;
        private Long idLoanType;
        private Long idStatus;

    public LoanApplication(Double amount, Integer term, Long idLoanType) {
        this.amount = amount;
        this.term = term;
        this.idLoanType = idLoanType;
    }
}
