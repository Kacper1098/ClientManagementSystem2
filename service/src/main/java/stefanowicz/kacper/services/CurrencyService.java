package stefanowicz.kacper.services;

import com.fasterxml.jackson.core.type.TypeReference;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import stefanowicz.kacper.exceptions.AppException;
import stefanowicz.kacper.help.RateList;
import stefanowicz.kacper.help.Request;
import stefanowicz.kacper.model.Rate;

import java.lang.reflect.Type;
import java.net.ProxySelector;
import java.net.http.HttpClient;
import java.net.http.HttpResponse;
import java.util.List;
import java.util.stream.Collectors;

public class CurrencyService {

    private final String RATES_URL = "http://api.nbp.pl/api/exchangerates/tables/a/?format=json";

    public List<Rate> getRates(){
        try{
            HttpResponse<String> response = HttpClient
                    .newBuilder()
                    .proxy(ProxySelector.getDefault())
                    .build()
                    .send(Request.requestGet(RATES_URL), HttpResponse.BodyHandlers.ofString());
            return filterRates(fromJson(response.body(), new TypeReference<List<RateList>>(){}.getType()));
        }
        catch (Exception e){
            throw new AppException("Currency service exception - get rates method - " + e.getMessage());
        }
    }

    private List<Rate> filterRates(List<RateList> rates){
        if(rates == null || rates.isEmpty()){
            throw new AppException("Currency service exception - filterRates method - list given as argument is null or empty");
        }

        return rates
                .get(0)
                .getRates()
                .stream()
                .filter(rate -> rate.getCode().toLowerCase().equals("usd") ||
                        rate.getCode().toLowerCase().equals("eur") ||
                        rate.getCode().toLowerCase().equals("aud") ||
                        rate.getCode().toLowerCase().equals("chf"))
                .collect(Collectors.toList());
    }

    private <T> T fromJson(String body, Type typeT){
        try{
            final Gson gson = new GsonBuilder().setPrettyPrinting().create();

            return gson.fromJson(body, typeT);
        }
        catch (Exception e){
            throw new AppException("CurrencyService exception - from json method - " + e.getMessage());
        }
    }
}
