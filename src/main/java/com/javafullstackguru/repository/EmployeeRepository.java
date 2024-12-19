package com.javafullstackguru.repository;

import com.javafullstackguru.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface EmployeeRepository extends JpaRepository<Employee, String> {

    @Query("FROM Employee e")
    List<Employee> getAllEmployeeData();

}