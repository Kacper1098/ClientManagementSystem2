package stefanowicz.kacper.validators;

import stefanowicz.kacper.model.Preference;
import stefanowicz.kacper.model.enums.ProductCategory;
import stefanowicz.kacper.validators.generic.AbstractValidator;

import java.util.Map;

public class PreferenceValidator extends AbstractValidator<Preference> {
    @Override
    public Map<String, String> validate(Preference preference) {

        if(preference == null){
            errors.put("preferenceObject", "Preference object is not valid, it cannot be null");
            return errors;
        }


        if(!isPreferenceNumberValid(preference)){
            errors.put("preferenceNumber", "Preference number is not valid, it has to be greater than zero and less than or equal to quantity of product cetegories");
        }

        if(!isPreferenceProductCategoryValid(preference)){
            errors.put("preferenceProductCategory", "Preference product category is not valid, it cannot be null");
        }

        return errors;
    }

    private boolean isPreferenceNumberValid(Preference preference){
        return preference.getNumber() > 0 && preference.getNumber() <= ProductCategory.values().length + 1;
    }

    private boolean isPreferenceProductCategoryValid(Preference preference){
        return preference.getProductCategory() != null;
    }
}
