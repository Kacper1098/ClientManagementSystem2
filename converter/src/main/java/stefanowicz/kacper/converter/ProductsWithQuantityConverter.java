package stefanowicz.kacper.converter;

import stefanowicz.kacper.model.ProductWithQuantity;

import java.util.Set;

public class ProductsWithQuantityConverter extends JsonConverter<Set<ProductWithQuantity>> {
    public ProductsWithQuantityConverter(String fileName) {
        super(fileName);
    }
}
