package stefanowicz.kacper.converter;

import stefanowicz.kacper.model.Client;

import java.util.Set;

public class ClientJsonConverter extends JsonConverter<Set<Client>> {
    public ClientJsonConverter(String fileName) {
        super(fileName);
    }
}
