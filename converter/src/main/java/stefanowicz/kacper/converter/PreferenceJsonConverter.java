package stefanowicz.kacper.converter;

import stefanowicz.kacper.model.Preference;

import java.util.Set;

public class PreferenceJsonConverter extends JsonConverter<Set<Preference>> {
    public PreferenceJsonConverter(String fileName) {
        super(fileName);
    }
}
