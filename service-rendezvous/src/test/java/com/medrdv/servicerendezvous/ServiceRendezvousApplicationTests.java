package com.medrdv.servicerendezvous;

import com.medrdv.rendezvous.RendezvousApplication;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(
        classes = RendezvousApplication.class,
        properties = {
                "spring.config.location=classpath:/application-test.properties"
        }
)
class ServiceRendezvousApplicationTests {

    @Test
    void contextLoads() {
    }
}