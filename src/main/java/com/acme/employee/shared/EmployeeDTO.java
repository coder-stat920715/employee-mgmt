package com.acme.employee.shared;

import java.io.Serializable;

public class EmployeeDTO implements Serializable {

    private Long id;
    private String name;
    private double salaryPerHour;

    public EmployeeDTO() { } // required by GWT-RPC

    public EmployeeDTO(Long id, String name, double salaryPerHour) {
        this.id = id;
        this.name = name;
        this.salaryPerHour = salaryPerHour;
    }

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public double getSalaryPerHour() { return salaryPerHour; }
    public void setSalaryPerHour(double salaryPerHour) { this.salaryPerHour = salaryPerHour; }
}
