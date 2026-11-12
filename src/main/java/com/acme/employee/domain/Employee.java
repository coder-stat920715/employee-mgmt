package com.acme.employee.domain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;
import java.math.BigDecimal;

@Entity
@Table(name = "DESIGNATION")
public class Employee {

    @Id
    @Column(name = "DES_ID")
    private Long id;

    @Column(name = "DES_NAME", nullable = false, length = 100)
    private String name;

    @Column(name = "SALARY_PER_HOUR", nullable = false, precision = 10, scale = 2)
    private BigDecimal salaryPerHour;

    public Employee() { }

    public Employee(Long id, String name, BigDecimal salaryPerHour) {
        this.id = id;
        this.name = name;
        this.salaryPerHour = salaryPerHour;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public BigDecimal getSalaryPerHour() { return salaryPerHour; }
    public void setSalaryPerHour(BigDecimal salaryPerHour) { this.salaryPerHour = salaryPerHour; }
}
