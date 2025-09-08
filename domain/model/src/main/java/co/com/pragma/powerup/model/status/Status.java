package co.com.pragma.powerup.model.status;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder(toBuilder = true)
@ToString
public class Status {
    private Long idStatus;
    private String name;
    private String description;
}
