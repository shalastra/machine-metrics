package io.shalastra.machinemetrics;

import io.shalastra.machinemetrics.metrics.MetricsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

@Slf4j
@SpringBootApplication
public class MachineMetricsApplication implements CommandLineRunner {

	private final KafkaTemplate<String, String> kafkaTemplate;

	public MachineMetricsApplication(KafkaTemplate<String, String> kafkaTemplate) {
		this.kafkaTemplate = kafkaTemplate;
	}

	@Bean
	public MetricsService systemInfoService() {
		return new MetricsService();
	}

	public static void main(String[] args) {
		SpringApplication.run(MachineMetricsApplication.class, args);
	}

	@Override
	public void run(String... args) {
		log.info(systemInfoService().collect().toString());

		kafkaTemplate.send("single-topic", systemInfoService().collect().toString());
	}
}

