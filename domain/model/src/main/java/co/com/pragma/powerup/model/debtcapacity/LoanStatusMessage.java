package co.com.pragma.powerup.model.debtcapacity;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString
public class LoanStatusMessage {
    private Long loanId;
    private String status;
}
