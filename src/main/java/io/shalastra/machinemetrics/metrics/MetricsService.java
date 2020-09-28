package io.shalastra.machinemetrics.metrics;

import io.shalastra.machinemetrics.metrics.entity.*;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;
import oshi.software.os.OperatingSystem;

public class MetricsService {

    public Metrics collect() {
        SystemInfo systemInfo = new SystemInfo();

        HardwareAbstractionLayer hal = systemInfo.getHardware();
        OperatingSystem operatingSystem = systemInfo.getOperatingSystem();

        return new Metrics(collectMemoryMetrics(hal), collectSensorMetrics(hal.getSensors()),
                collectProcessMetrics(operatingSystem), collectThreadMetrics(operatingSystem));
    }

    private MemoryData collectMemoryMetrics(HardwareAbstractionLayer hal) {
        long availableMemory = hal.getMemory().getAvailable();
        long virtualMemoryInUse = hal.getMemory().getVirtualMemory().getVirtualInUse();

        return new MemoryData(availableMemory, virtualMemoryInUse);
    }

    private SensorData collectSensorMetrics(Sensors sensors) {
        return new SensorData(sensors.getCpuTemperature(), sensors.getCpuVoltage());
    }

    private ProcessData collectProcessMetrics(OperatingSystem operatingSystem) {
        int processCount = operatingSystem.getProcessCount();

        return new ProcessData(processCount);
    }

    private ThreadData collectThreadMetrics(OperatingSystem operatingSystem) {
        int threadCount = operatingSystem.getThreadCount();

        return new ThreadData(threadCount);
    }
}
