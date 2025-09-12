package co.com.pragma.powerup.r2dbc.entity;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoanApplicationListItemEntity {
    private Double amount;
    private Integer term;
    private String email;
    private String username;
    private String loantypename;
    private Double interestrate;
    private String statusname;
    private String basesalary;
    private String monthlyrequestedamount;
}
