package co.com.pragma.powerup.model.loanapplication.request;


import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
public class UpdateLoanStatusRequest {
    private Long loanId;
    private String newStatus;
}
