package co.com.pragma.powerup.r2dbc.entity;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanApplicationListItemEntity {
    private Double amount;
    private Integer term;
    private String email;
    private String loanTypeName;
    private Double interestRate;
    private String statusName;
    private String baseSalary;
}
