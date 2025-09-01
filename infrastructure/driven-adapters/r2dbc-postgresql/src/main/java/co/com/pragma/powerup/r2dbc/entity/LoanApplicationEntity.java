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
@Table("loanApplications")
public class LoanApplicationEntity {

    @Id
    private Long loanId;
    private String email;
    private Double amount;
    private Integer term;
    private Long idLoanType;
    private Long idStatus;

}
