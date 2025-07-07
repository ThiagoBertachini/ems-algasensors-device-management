package com.algaworks.algasensors.device.management.api.controller;

import com.algaworks.algasensors.device.management.api.client.SensorMonitoringClient;
import com.algaworks.algasensors.device.management.api.model.SensorDetailOutput;
import com.algaworks.algasensors.device.management.api.model.SensorInput;
import com.algaworks.algasensors.device.management.api.model.SensorMonitoringOutput;
import com.algaworks.algasensors.device.management.api.model.SensorOutput;
import com.algaworks.algasensors.device.management.common.IdGenerator;
import com.algaworks.algasensors.device.management.domain.model.Sensor;
import com.algaworks.algasensors.device.management.domain.model.SensorId;
import com.algaworks.algasensors.device.management.domain.repository.SensorRepository;
import io.hypersistence.tsid.TSID;
import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.server.ResponseStatusException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/sensors")
public class SensorController {

    private final SensorRepository repository;
    private final SensorMonitoringClient sensorMonitoringClient;

    @GetMapping
    public Page<SensorOutput> search(Pageable pageable) {
        return repository.findAll(pageable)
                .map(this::convertToModel);
    }

    @GetMapping("/{sensorId}")
    public SensorOutput getOne(@PathVariable TSID sensorId) {
        Sensor sensor = repository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        return convertToModel(sensor);
    }

    @GetMapping("/{sensorId}/detail")
    public SensorDetailOutput getOneWithDetails(@PathVariable TSID sensorId) {
        Sensor sensor = repository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        SensorMonitoringOutput sensorMonitoringOutPut = sensorMonitoringClient
                .getDetails(sensorId);

        return SensorDetailOutput.builder()
                .sensorOutput(convertToModel(sensor))
                .sensorMonitoringOutPut(sensorMonitoringOutPut)
                .build();
    }

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public SensorOutput create(@RequestBody SensorInput input) {
        Sensor sensor = Sensor.builder()
                .id(new SensorId(IdGenerator.generateTSID()))
                .ip(input.getIp())
                .name(input.getName())
                .location(input.getLocation())
                .model(input.getModel())
                .protocol(input.getProtocol())
                .enabled(false)
                .build();

        Sensor sensorSaved = repository.saveAndFlush(sensor);

        return convertToModel(sensorSaved);
    }

    @PutMapping("/{sensorId}")
    @ResponseStatus(HttpStatus.OK)
    public SensorOutput update(@RequestBody SensorInput input, @PathVariable @Nonnull TSID sensorId) {
        Sensor sensorUpdated = repository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));

        sensorUpdated.setIp(input.getIp());
        sensorUpdated.setName(input.getName());
        sensorUpdated.setLocation(input.getLocation());
        sensorUpdated.setModel(input.getModel());
        sensorUpdated.setProtocol(input.getProtocol());

        Sensor sensorSaved = repository.save(sensorUpdated);

        return convertToModel(sensorSaved);
    }

    @DeleteMapping("/{sensorId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable @Nonnull TSID sensorId) {
        repository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        repository.deleteById(new SensorId(sensorId));
        sensorMonitoringClient.disableMonitoring(sensorId);
    }

    @PutMapping("/{sensorId}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void enable(@PathVariable @Nonnull TSID sensorId) {
        Sensor sensorUpdated = repository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        sensorUpdated.setEnabled(true);
        repository.save(sensorUpdated);
        sensorMonitoringClient.enableMonitoring(sensorId);
    }

    @DeleteMapping("/{sensorId}/enable")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void disabled(@PathVariable @Nonnull TSID sensorId) {
        Sensor sensorUpdated = repository.findById(new SensorId(sensorId))
                .orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
        sensorUpdated.setEnabled(false);
        repository.save(sensorUpdated);
        sensorMonitoringClient.disableMonitoring(sensorId);
    }

    private SensorOutput convertToModel(Sensor sensor) {
        return SensorOutput.builder()
                .id(sensor.getId().getValue())
                .ip(sensor.getIp())
                .name(sensor.getName())
                .location(sensor.getLocation())
                .model(sensor.getModel())
                .protocol(sensor.getProtocol())
                .enabled(sensor.getEnabled())
                .build();
    }
}
