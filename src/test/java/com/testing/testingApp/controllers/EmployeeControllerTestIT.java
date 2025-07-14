package com.testing.testingApp.controllers;

import com.testing.testingApp.TestContainerConfiguration;
import com.testing.testingApp.dto.EmployeeDto;
import com.testing.testingApp.entities.EmployeeEntity;
import com.testing.testingApp.repositories.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.reactive.AutoConfigureWebTestClient;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.web.reactive.server.WebTestClient;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;


class EmployeeControllerTestIT extends AbstractIntegrationTest{

    @Autowired
    private EmployeeRepository employeeRepository;


    @BeforeEach
    void setUp() {
        employeeRepository.deleteAll();
    }

    @Test
    void testGetEmployeeById_success() {
        EmployeeEntity savedEmployee = employeeRepository.save(testEmployee);
        webTestClient.get()
                .uri("/employees/{id}", savedEmployee.getId())
                .exchange()
                .expectStatus().isOk()
                .expectBody(EmployeeDto.class)
                .value(employeeDto -> {
                    assertThat(employeeDto.getEmail()).isEqualTo(savedEmployee.getEmail());
                    assertThat(employeeDto.getName()).isEqualTo(savedEmployee.getName());
                });
        // .isEqualTo(testEmployeeDto)  useOnlywhenhashCodeandEqualsmethodaredefined it may just not work sometimes
    }

    @Test
    void testGetEmployeeById_failure() {
        webTestClient.get()
                .uri("/employees/1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testCreateNewEmployee_whenEmployeeAlreadyExists_thenThrowException() {
        EmployeeEntity savedEmployee = employeeRepository.save(testEmployee);

        webTestClient.post()
                .uri("/employees")
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testCreateNewEmployee_whenEmployeeDoesNotExists_thenCreateEmployee() {
        webTestClient.post()
                .uri("/employees")
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().isCreated()
                .expectBody()
                .jsonPath("$.email").isEqualTo(testEmployeeDto.getEmail())
                .jsonPath("$.name").isEqualTo(testEmployeeDto.getName())
                .jsonPath("$.salary").isEqualTo(testEmployeeDto.getSalary());
    }
    @Test
    void testUpdateEmployee_whenEmployeeDoesNotExists_thenThrowException () {
        webTestClient.put()
                .uri("/employees/222")
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testUpdateEmployee_whenEmployeeEmailBeingUpdated_thenThrowException(){
        EmployeeEntity savedEmployee=employeeRepository.save(testEmployee);
        testEmployeeDto.setName("Raja");
        testEmployeeDto.setEmail("raja@gmail.com");

        webTestClient.put()
                .uri("/employees/{id}",savedEmployee.getId())
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().is5xxServerError();
    }

    @Test
    void testUpdateEmployee_whenEmployeeIsValid_thenUpdateEmployee(){
        EmployeeEntity savedEmployee= employeeRepository.save(testEmployee);
        testEmployeeDto.setName("Rakunja");
        testEmployeeDto.setSalary(10000000L);

        webTestClient.put()
                .uri("/employees/{id}",savedEmployee.getId())
                .bodyValue(testEmployeeDto)
                .exchange()
                .expectStatus().isOk()
                .expectBody()
                .jsonPath("$.name").isEqualTo(testEmployeeDto.getName());
    }

    @Test
    void testDeleteEmployee_whenEmployeeDoesNotExists_thenThrowException(){
        webTestClient.delete()
                .uri("/employees/1")
                .exchange()
                .expectStatus().isNotFound();
    }

    @Test
    void testDeleteEmployee_whenEmployeeDoExists_thenDeleteEmployee(){
        EmployeeEntity savedEmployee=employeeRepository.save(testEmployee);

        webTestClient.delete()
                .uri("/employees/{id}",savedEmployee.getId() )
                .exchange()
                .expectStatus().isNoContent()
                .expectBody(void.class);

        webTestClient.delete()
                .uri("/employees/1")
                .exchange()
                .expectStatus().isNotFound();
    }

}