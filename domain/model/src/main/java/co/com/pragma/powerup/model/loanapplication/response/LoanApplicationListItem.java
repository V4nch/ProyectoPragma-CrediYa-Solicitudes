package co.com.pragma.powerup.model.loanapplication.response;

import lombok.*;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class LoanApplicationListItem {
    private Double amount;
    private Integer term;
    private String email;
    private String loanTypeName;
    private Double interestRate;
    private String statusName;
    private String baseSalary;
}
