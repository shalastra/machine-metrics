package io.shalastra.machinemetrics;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.HardwareAbstractionLayer;

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

		return cpu.toString();
	}
}
