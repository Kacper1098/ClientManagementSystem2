package stefanowicz.kacper.help;

import stefanowicz.kacper.converter.JsonConverter;
import stefanowicz.kacper.help.CategoryWithProducts;

import java.util.List;

public class CategoriesWithProductsJsonConverter extends JsonConverter<List<CategoryWithProducts>> {
    public CategoriesWithProductsJsonConverter(String fileName) {
        super(fileName);
    }
}
