package co.com.pragma.powerup.r2dbc.entity;


import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("loanapplications")
public class LoanApplicationEntity {

    @Id
    private Long loanId;
    private String email;
    private Double amount;
    private Integer term;
    @Column("id_loan_type")
    private Long idLoanType;
    @Column("id_status")
    private Long idStatus;

}
