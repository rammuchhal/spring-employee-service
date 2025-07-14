package com.testing.testingApp.controllers;

import com.testing.testingApp.TestContainerConfiguration;
import com.testing.testingApp.dto.EmployeeDto;
import com.testing.testingApp.entities.EmployeeEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

@AutoConfigureWebTestClient(timeout="100000")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Import(TestContainerConfiguration.class)
public class AbstractIntegrationTest {

    @Autowired
    WebTestClient webTestClient;

    EmployeeEntity testEmployee = EmployeeEntity.builder()
            .name("Ram")
                .email("ram@gmail.com")
                .salary(100000L)
                .build();

    EmployeeDto testEmployeeDto = EmployeeDto.builder()
            .name("Ram")
                .email("ram@gmail.com")
                .salary(100000L)
                .build();
}
