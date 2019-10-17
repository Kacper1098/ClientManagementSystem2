package stefanowicz.kacper.help;

import stefanowicz.kacper.exceptions.AppException;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.time.Duration;

public final class Request {
    private Request(){};

    public static HttpRequest requestGet(final String path){
        if(path == null){
            throw new AppException("request get exception - url path is null");
        }
        try{
            return HttpRequest.newBuilder()
                    .uri(new URI(path.replaceAll(" ", "%20")))
                    .version(HttpClient.Version.HTTP_2)
                    .timeout(Duration.ofSeconds(20))
                    .GET()
                    .build();
        }
        catch (Exception e){
            throw new AppException("request get exception - " + e.getMessage());
        }
    }
}
