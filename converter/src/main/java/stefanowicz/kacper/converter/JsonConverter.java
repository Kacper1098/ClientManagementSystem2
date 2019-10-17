package stefanowicz.kacper.converter;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import stefanowicz.kacper.exceptions.AppException;

import java.io.FileReader;
import java.io.FileWriter;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Optional;

public abstract class JsonConverter<T> {

    private final String fileName;
    private final Gson gson = new GsonBuilder().setPrettyPrinting().create();
    private final Type type = ((ParameterizedType)getClass().getGenericSuperclass()).getActualTypeArguments()[0];

    public JsonConverter(String fileName) { this.fileName = fileName; }

    public void toJson(T t){
        if( t == null ){
            throw new AppException("json converter exception - to json method - element is null");
        }
        try(FileWriter writer = new FileWriter(fileName)){
            gson.toJson(t, writer);
        }
        catch ( Exception e) {
            throw new AppException("json converter exception - to json method - " + e.getMessage());
        }
    }

    public Optional<T> fromJson(){
        try(FileReader reader = new FileReader(fileName)){
            return Optional.of(gson.fromJson(reader, type));
        }
        catch (Exception e ){
            throw new AppException("json converter exception - from json method - " + e.getMessage());
        }
    }
}
