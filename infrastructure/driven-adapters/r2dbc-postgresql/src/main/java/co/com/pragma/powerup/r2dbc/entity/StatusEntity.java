package co.com.pragma.powerup.r2dbc.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Table("status")
public class StatusEntity {
    @Id
    private Long idStatus;
    private String name;
    private String description;
}
