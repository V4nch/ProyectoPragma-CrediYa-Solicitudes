package co.com.pragma.powerup.model.loantype;
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
public class LoanType {
    private Long idLoanType;
    private String name;
    private Double minimumAmount;
    private Double maximumAmount;
    private Double interestRate;
    private boolean automaticValidation;

}
