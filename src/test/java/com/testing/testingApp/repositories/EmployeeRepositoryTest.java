package com.testing.testingApp.repositories;

import com.testing.testingApp.TestContainerConfiguration;
import com.testing.testingApp.entities.EmployeeEntity;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import java.util.List;

import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(TestContainerConfiguration.class)
class EmployeeRepositoryTest {

    @Autowired
    private EmployeeRepository employeeRepository;

    private EmployeeEntity employee;

    @BeforeEach
    void setUp() {
        employee=EmployeeEntity.builder().
                name("Ram").
                email("ram@gmail.com").
                salary(100000L).
                build();
    }

    @AfterEach
    void tearDown() {
        employeeRepository.deleteAll();
    }

    @Test
    void testFindByEmail_whenEmailIsPresent_thenReturnEmployee() {
        //Arrange,Given
        employeeRepository.save(employee);

        //Act,when
        List<EmployeeEntity> employeeList=employeeRepository.findByEmail(employee.getEmail());

        //Assert,then
        assertThat(employeeList).isNotNull();
//        assertThat(employeeList).isNotEmpty();
        assertThat(employeeList.getFirst().getEmail()).isEqualTo(employee.getEmail());
    }

    @Test
    void testFindByEmail_whenEmailIsNotFound_thenReturnEmptyEmployeeList() {
        //Arrange,Given
        String email="rama@gmail.com";

        //Act,when
        List<EmployeeEntity> employeeList = employeeRepository.findByEmail(email);

        //Assert,then
        assertThat(employeeList).isNotNull();
//        assertThat(employeeList).isEmpty();
    }
}