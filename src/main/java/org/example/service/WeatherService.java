package org.example.service;

import com.google.gson.Gson;
import jakarta.enterprise.context.ApplicationScoped;
import lombok.NoArgsConstructor;
import org.example.exception.APIException;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.util.Map;

@ApplicationScoped
@NoArgsConstructor
public class WeatherService {
    private final HttpClient httpClient = HttpClient.newBuilder()
            .version(HttpClient.Version.HTTP_2)
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    private final String weatherApiTemplateURI = "http://api.weatherapi.com/v1/current.json?key=%s&q=%s&aqi=no";

    /**
     * Получение температуры в городе с помощью api.weatherapi.com
     * @param ip ip
     * @return температура в этом городе
     * @throws APIException Исключение которое возникает в случае ошибки запроса в апи
     */
    public Float getTemperatureByIp(String ip) throws APIException {
//        System.out.println("DEBUG: IP = " + ip);
        String apiKey = System.getenv("WEATHER_API_KEY");
        if (apiKey == null || apiKey.trim().isEmpty()) {
            throw new APIException("WEATHER_API_KEY environment variable is not set.");
        }

        HttpRequest httpRequest = HttpRequest.newBuilder()
                .GET()
                .uri(URI.create(String.format(weatherApiTemplateURI, apiKey, ip)))
                .header("Accept", "application/json")
                .build();

        try {
            HttpResponse<String> response = httpClient.send(httpRequest, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new APIException(String.format("Статус ответа: %d. Ошибка запроса к API.", response.statusCode()));
            }
            if (response.body().contains("error")) {
                throw new APIException(response.body());
            }

            Gson gson = new Gson();
            Map<String, Object> mappedJson = gson.fromJson(response.body(), Map.class);
            Map<String, Object> current = (Map<String, Object>) mappedJson.get("current");
            if (current == null) throw new APIException("'current' object is null in the API response");

            Object tempObj = current.get("temp_c");
            if (tempObj == null) throw new APIException("'temp_c' is null in the API response");

            return ((Number) tempObj).floatValue();

        } catch (IOException | InterruptedException exception) {
            throw new APIException(exception.getMessage());
        }
    }

}
