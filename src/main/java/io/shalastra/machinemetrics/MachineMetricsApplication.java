package io.shalastra.machinemetrics;

import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.admin.AdminClientConfig;
import org.apache.kafka.clients.admin.NewTopic;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import oshi.SystemInfo;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.hardware.Sensors;
import oshi.software.os.OperatingSystem;

import java.util.HashMap;
import java.util.Map;

@Slf4j
@SpringBootApplication
public class MachineMetricsApplication implements CommandLineRunner {

	@Autowired
	private KafkaTemplate<String, String> kafkaTemplate;

	@Bean
	public SystemInfoService systemInfoService() {
		return new SystemInfoService();
	}

	public static void main(String[] args) {
		SpringApplication.run(MachineMetricsApplication.class, args);
	}

	@Override
	public void run(String... args) {
		log.info(systemInfoService().collect());

		kafkaTemplate.send("single-topic", systemInfoService().collect());
	}
}

@Configuration
class KafkaConfig {

	@Value(value = "${kafka.bootstrapAddress}")
	private String bootstrapAddress;

	@Bean
	public KafkaAdmin kafkaAdmin() {
		Map<String, Object> configs = new HashMap<>();
		configs.put(AdminClientConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);

		return new KafkaAdmin(configs);
	}

	@Bean
	public NewTopic singleTopic() {
		return new NewTopic("single-topic", 1, (short) 1);
	}
}

@Configuration
class KafkaProducerConfig {

	@Value(value = "${kafka.bootstrapAddress}")
	private String bootstrapAddress;

	@Bean
	public ProducerFactory<String, String> producerFactory() {
		Map<String, Object> properties = new HashMap<>();

		properties.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapAddress);
		properties.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
		properties.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

		return new DefaultKafkaProducerFactory<>(properties);
	}

	@Bean
	public KafkaTemplate<String, String> kafkaTemplate() {
		return new KafkaTemplate<>(producerFactory());
	}
}

class SystemInfoService {

	public String collect() {
		SystemInfo systemInfo = new SystemInfo();

		HardwareAbstractionLayer hal = systemInfo.getHardware();
		OperatingSystem operatingSystem = systemInfo.getOperatingSystem();

		Sensors sensors = hal.getSensors();

		long availableMemory = hal.getMemory().getAvailable();
		long virtualMemoryInUse = hal.getMemory().getVirtualMemory().getVirtualInUse();

		int processCount = operatingSystem.getProcessCount();
		int threadCount = operatingSystem.getThreadCount();

		SensorData sensorData = new SensorData(sensors.getCpuTemperature(), sensors.getCpuVoltage());
		MemoryData memoryData = new MemoryData(availableMemory, virtualMemoryInUse);
		ProcessData processData = new ProcessData(processCount);
		ThreadData threadData = new ThreadData(threadCount);

		Metrics metrics = new Metrics(memoryData, sensorData, processData, threadData);

		return metrics.toString();
	}
}

record Metrics(MemoryData memoryData, SensorData sensorData, ProcessData processData, ThreadData threadData) {
}

record SensorData(double cpuTemperature, double cpuVoltage) {
}

record ProcessData(int processCount) {
}

record ThreadData(int threadCount) {
}

record MemoryData(long available, long virtualInUse) {
}
