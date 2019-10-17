package stefanowicz.kacper.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Client {
    private String firstName;
    private String lastName;
    private int age;
    private BigDecimal cash;
    private String preferences;
    private String email;
}
