package com.medrdv.servicepatients;

import com.medrdv.patients.PatientsApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
		classes = PatientsApplication.class,
		properties = {
				"spring.config.location=classpath:/application-test.properties"
		}
)
class ServicePatientsApplicationTests {

	@Test
	void contextLoads() {
	}
}