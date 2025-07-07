package com.algaworks.algasensors.device.management.api.client;

import com.algaworks.algasensors.device.management.api.client.exceptions.SensorMonitoringClientBadGatewayException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class RestClientFactory {

    private final RestClient.Builder restClient;

    public RestClient createSensorMonitoringClient() {
        return restClient.baseUrl("http://localhost:8082")
                .requestFactory(generateRequestFactory())
                .defaultStatusHandler(HttpStatusCode::isError, (request, response) ->
                        {
                            throw new SensorMonitoringClientBadGatewayException();
                        }
                ).build();
    }

    private ClientHttpRequestFactory generateRequestFactory() {
        SimpleClientHttpRequestFactory requestFactory = new SimpleClientHttpRequestFactory();
        requestFactory.setConnectTimeout(Duration.ofSeconds(5)); // timeout to connect
        requestFactory.setReadTimeout(Duration.ofSeconds(5)); // timeout to read, after connect
        return requestFactory;
    }
}
