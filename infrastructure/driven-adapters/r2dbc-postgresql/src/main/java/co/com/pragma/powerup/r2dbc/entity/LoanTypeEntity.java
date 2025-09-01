package co.com.pragma.powerup.r2dbc.entity;

import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("loanTypes")
public class LoanTypeEntity {

    @Id
    private Long idLoanType;
    private String name;
    private Double minimumAmount;
    private Double maximumAmount;
    private Double interestRate;
    private boolean automaticValidation;

}
