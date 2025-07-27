package com.cmpdi.project.repository;

import java.time.LocalDate;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.cmpdi.project.model.MasterEmployee;

public interface MasterEmployeeRepository extends JpaRepository<MasterEmployee, String> {
    Optional<MasterEmployee> findByEmployeeId(String employeeId);
    boolean existsByEmployeeId(String employeeId);
    Optional<MasterEmployee> findByEmployeeIdAndDob(String employeeId, LocalDate dob);
}
