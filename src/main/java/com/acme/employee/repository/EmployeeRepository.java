package com.acme.employee.repository;

import com.acme.employee.domain.Employee;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface EmployeeRepository extends JpaRepository<Employee, Long> {

    List<Employee> findAllByOrderByIdAsc();

    @Query(value = "SELECT DESIGNATION_SEQ.NEXTVAL FROM DUAL", nativeQuery = true)
    Number nextId();
}
