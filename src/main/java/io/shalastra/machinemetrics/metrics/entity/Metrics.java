package io.shalastra.machinemetrics.metrics.entity;

public record Metrics(MemoryData memoryData, SensorData sensorData, ProcessData processData, ThreadData threadData) {

}
