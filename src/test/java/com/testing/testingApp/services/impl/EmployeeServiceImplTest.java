package com.testing.testingApp.services.impl;

import com.testing.testingApp.TestContainerConfiguration;
import com.testing.testingApp.dto.EmployeeDto;
import com.testing.testingApp.entities.EmployeeEntity;
import com.testing.testingApp.exceptions.ResourceNotFoundException;
import com.testing.testingApp.repositories.EmployeeRepository;
import com.testing.testingApp.services.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

import javax.swing.text.html.Option;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.AssertionsForClassTypes.*;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ExtendWith(MockitoExtension.class)
@Import(TestContainerConfiguration.class)
@Slf4j
class EmployeeServiceImplTest {

    EmployeeRepository employeeRepository=Mockito.mock(EmployeeRepository.class);

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    @Spy
    private ModelMapper mapper;

    private EmployeeEntity employee;
    private EmployeeDto employeeDto;

    @BeforeEach
    void setUp(){
        employee=EmployeeEntity.builder().id(1L).name("Ram").email("ram@gmail.com").salary(1000000L).build();
        employeeDto = mapper.map(employee, EmployeeDto.class);
    }

    @Test
    public void testGetEmployeeById_whenEmployeeIsPresent_thenReturnEmployee(){
        //arrange
        Long id=employee.getId();
        Mockito.when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        log.info("stubbing findById method");

        //act
        EmployeeDto result=employeeService.getEmployeeById(id);
        log.info("got result from stubbed method call");

        //assert
        assertThat(result.getName()).isEqualTo("Ram");
        assertThat(result.getEmail()).isEqualTo(employeeDto.getEmail());
        verify(employeeRepository,times(1)).findById(id);
        verify(employeeRepository,only()).findById(id);
    }

    @Test
    void testGetEmployeeById_whenNotPresent_thenThrowException(){
        //arrange
        when(employeeRepository.findById(anyLong())).thenReturn(Optional.empty());

        //act and assert
        assertThatThrownBy(()->employeeService.getEmployeeById(1L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: 1");

        verify(employeeRepository).findById(1L);
    }

    @Test
    void testCreateNewEmployee_whenValidEmployee_thenCreateNewEmployee(){
        //arrange
        when(employeeRepository.findByEmail(anyString())).thenReturn(List.of());
        when(employeeRepository.save(any())).thenReturn(employee);

        //act
        EmployeeDto employeeDto1=employeeService.createNewEmployee(employeeDto);

        //assert
        assertThat(employeeDto1).isNotNull();
        assertThat(employeeDto1.getEmail()).isEqualTo(employeeDto.getEmail());

        ArgumentCaptor<EmployeeEntity> employeeCaptor=ArgumentCaptor.forClass(EmployeeEntity.class);
        verify(employeeRepository).save(employeeCaptor.capture());

        EmployeeEntity capturedEmployee=employeeCaptor.getValue();
        assertThat(capturedEmployee.getEmail()).isEqualTo(employee.getEmail());

    }

    @Test
    void testCreateNewEmployee_whenEmployeeAlreadyExist_thenThrowException(){
        //arrange
        when(employeeRepository.findByEmail(employee.getEmail())).thenReturn(List.of(employee));

        //act and assert
        assertThatThrownBy(()->employeeService.createNewEmployee(employeeDto)).isInstanceOf(RuntimeException.class)
                .hasMessage("Employee already exists with email: "+employee.getEmail());

        verify(employeeRepository).findByEmail(employee.getEmail());
        verify(employeeRepository,never()).save(any());
    }

    @Test
    void testUpdateEmployee_whenEmployeeDoesNotExist_thenThrowException(){
        when(employeeRepository.findById(1L)).thenReturn(Optional.empty());


        //act and assert
        assertThatThrownBy(()->employeeService.updateEmployee(1L,employeeDto)).isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: " + employeeDto.getId());

        verify(employeeRepository).findById(1L);
        verify(employeeRepository,never()).save(any());
    }

    @Test
    void testUpdateEmployee_whenAttemptingToUpdateEmail_thenThrowException(){
        when(employeeRepository.findById(1L)).thenReturn(Optional.of(employee));
        employeeDto.setName("Aam");
        employeeDto.setEmail("aam@gmail.com");

        assertThatThrownBy(()->employeeService.updateEmployee(employeeDto.getId(),employeeDto))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("The email of the employee cannot be updated");

        verify(employeeRepository,never()).save(any());
    }

    @Test
    void testUpdateExistingEmployee_whenEmployeeDoExistAndValid_thenUpdateEmployee(){
        //arrange
        when(employeeRepository.findById(employeeDto.getId())).thenReturn(Optional.of(employee));
        employeeDto.setName("Vikas");
        employeeDto.setSalary(100L);

        EmployeeEntity newEmployee=mapper.map(employeeDto, EmployeeEntity.class);
        when(employeeRepository.save(any(EmployeeEntity.class))).thenReturn(newEmployee);

        //act
        EmployeeDto updatedEmployeeDto=employeeService.updateEmployee(employeeDto.getId(),employeeDto);

        assertThat(updatedEmployeeDto).isEqualTo(employeeDto);

        verify(employeeRepository).findById(1L);
        verify(employeeRepository).save(any());
    }

    @Test
    void testDeleteEmployee_whenEmployeeDoExist_thenDeleteEmployee(){
        when(employeeRepository.existsById(1L)).thenReturn(true);

        employeeService.deleteEmployee(1L);

        verify(employeeRepository).deleteById(1L);
    }

    @Test
    void testDeleteEmployee_whenEmployeeDoNotExist_thenThrowException(){
        when(employeeRepository.existsById(1L)).thenReturn(false);

        assertThatThrownBy(()->employeeService.deleteEmployee(1L)).isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("Employee not found with id: " + employeeDto.getId());

        verify(employeeRepository,never()).deleteById(anyLong());
    }
}
