package stefanowicz.kacper.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import stefanowicz.kacper.model.enums.ProductCategory;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class Preference {
    private int number;
    private ProductCategory productCategory;
}
