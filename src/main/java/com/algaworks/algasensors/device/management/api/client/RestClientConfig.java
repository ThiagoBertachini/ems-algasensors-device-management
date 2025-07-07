package com.algaworks.algasensors.device.management.api.client;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.support.RestClientAdapter;
import org.springframework.web.service.invoker.HttpServiceProxyFactory;

@Configuration
public class RestClientConfig {

    @Bean
    public SensorMonitoringClient sensorMonitoringClient(RestClientFactory factory){
        RestClient restClient = factory.createSensorMonitoringClient();
        RestClientAdapter adpter = RestClientAdapter.create(restClient);

        HttpServiceProxyFactory factoryProxy = HttpServiceProxyFactory
                .builderFor(adpter).build();
        return factoryProxy.createClient(SensorMonitoringClient.class);
    }
}
