package com.algaworks.algasensors.device.management.api.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SensorDetailOutput {
    private SensorOutput sensorOutput;
    private SensorMonitoringOutput sensorMonitoringOutPut;
}
