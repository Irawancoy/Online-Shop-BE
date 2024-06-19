package com.readinessbtpnbe.orderBE;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@EnableJpaAuditing
@SpringBootApplication(scanBasePackages = { "com.readinessbtpnbe.orderBE", "lib.minio", "lib.i18n" })
public class OrderBeApplication {

	public static void main(String[] args) {
		SpringApplication.run(OrderBeApplication.class, args);
	}

}
