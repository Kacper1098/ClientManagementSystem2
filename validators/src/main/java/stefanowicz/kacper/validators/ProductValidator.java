package stefanowicz.kacper.validators;

import stefanowicz.kacper.model.Product;
import stefanowicz.kacper.validators.generic.AbstractValidator;

import java.math.BigDecimal;
import java.util.Map;

public class ProductValidator extends AbstractValidator<Product> {
    @Override
    public Map<String, String> validate(Product product) {
        errors.clear();

        if( product == null ){
            errors.put("productObject", "Product object is not valid, it cannot be null");
            return errors;
        }

        if(!isProductNameValid(product)){
            errors.put("productName", "Product name is not valid, it has to consists of letters and whitespaces only");
        }

        if(!isProductPriceValid(product)){
            errors.put("productPrice", "Product price is not valid, it has to be greater than zero");
        }

        if(!isProductCategoryValid(product)){
            errors.put("productCategory", "Product category is not valid, it cannot be null");
        }

        return errors;
    }

    private boolean isProductNameValid(Product product){
        return product.getName() != null && product.getName().matches("(([A-Za-z]+\\s)?[A-Za-z]+)+");
    }

    private boolean isProductPriceValid(Product product){
        return product.getPrice() != null && product.getPrice().compareTo(BigDecimal.ZERO) > 0;
    }

    private boolean isProductCategoryValid(Product product){
        return product.getCategory() != null;
    }
}
