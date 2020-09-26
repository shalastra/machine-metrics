package io.shalastra.machinemetrics;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;

@Slf4j
@SpringBootApplication
public class MachineMetricsApplication implements CommandLineRunner {

	@Bean
	public SystemInfoService systemInfoService() {
		return new SystemInfoService();
	}

	public static void main(String[] args) {
		SpringApplication.run(MachineMetricsApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		log.info(systemInfoService().collect());
	}
}

class SystemInfoService {

	public String collect() {
		SystemInfo systemInfo = new SystemInfo();

		HardwareAbstractionLayer hal = systemInfo.getHardware();
		CentralProcessor cpu = hal.getProcessor();

		Sensors sensors = hal.getSensors();

		SensorsData sensorsData = new SensorsData(sensors.getCpuTemperature(), sensors.getCpuVoltage());
		return sensorsData.toString();
	}
}

record SensorsData(double cpuTemperature, double cpuVoltage) {}
