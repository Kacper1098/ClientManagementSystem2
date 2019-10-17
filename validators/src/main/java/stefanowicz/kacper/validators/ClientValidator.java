package stefanowicz.kacper.validators;

import stefanowicz.kacper.model.Client;
import stefanowicz.kacper.model.enums.ProductCategory;
import stefanowicz.kacper.validators.generic.AbstractValidator;

import java.math.BigDecimal;
import java.util.Map;

public class ClientValidator extends AbstractValidator<Client> {
    @Override
    public Map<String, String> validate(Client client) {

        errors.clear();

        if(client == null){
            errors.put("clientObject", "Client object is not valid, it cannot be null");
            return errors;
        }


        if(!isClientsFirstNameValid(client)){
            errors.put("clientFirstName", "Clients first name is not valid, it has to start with capital letter and consists of letters and whitespaces only");
        }

        if(!isClientsLastNameValid(client)){
            errors.put("clientSecondName", "Clients last name is not valid,  it has to start with capital letter and consists of letters only");
        }

        if(!isClientsAgeValid(client)){
            errors.put("clientAge", "Clients age is not valid, it has to be greater than zero");
        }

        if(!isClientsCashValid(client)){
            errors.put("clientCash", "Clients cash is not valid, it cannot be null and has to be greater than or equal to zero");
        }

        if(!areClientsPreferencesValid(client)){
            errors.put("clientsPreferences", "Clients preferences are not valid, they have to be greater than or equal to 18");
        }

        return errors;
    }

    private boolean isClientsFirstNameValid(Client client){
        return client.getFirstName() != null && client.getFirstName().matches("([A-Z]+\\s)?[A-Za-z]+");
    }

    private boolean isClientsLastNameValid(Client client){
        return client.getLastName() != null && client.getLastName().matches("([A-Z]+\\s)?[A-Za-z]+");
    }

    private boolean isClientsAgeValid(Client client){
        return client.getAge() >= 18;
    }

    private boolean isClientsCashValid(Client client){
        return client.getCash() != null && client.getCash().compareTo(BigDecimal.ZERO) >= 0;
    }

    private boolean areClientsPreferencesValid(Client client){
        return client.getPreferences() != null && client.getPreferences().matches("\\d+") &&
                client.getPreferences().chars()
                        .mapToObj(c -> (char) c)
                        .allMatch(preference -> Character.getNumericValue(preference) > 0
                                && Character.getNumericValue(preference) <= ProductCategory.values().length + 1);
    }
}
