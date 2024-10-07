/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Matthew
 */
public class Employee {
    
    private int id;
    private String firstName;
    private String middleName;
    private String lastName;
    private LocalDateTime active;
    private Badge badge;
    private Department department;
    private Shift shift;
    private EmployeeType employeeType;
    

    // Constructor
    public Employee(int id, String firstName, String middleName, String lastName, 
                    LocalDateTime active, Badge badge, Department department, 
                    Shift shift, EmployeeType employeeType) {
        this.id = id;
        this.firstName = firstName;
        this.middleName = middleName;
        this.lastName = lastName;
        this.active = active;
        this.badge = badge;
        this.department = department;
        this.shift = shift;
        this.employeeType = employeeType;
    }

    // Getters
    public int getId() { return id; }
    public String getFirstName() { return firstName; }
    public String getMiddleName() { return middleName; }
    public String getLastName() { return lastName; }
    public LocalDateTime getActive() { return active; }
    public Badge getBadge() { return badge; }
    public Department getDepartment() { return department; }
    public Shift getShift() { return shift; }
    public EmployeeType getEmployeeType() { return employeeType; }

    // Override toString method
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        String result;
        
        // Formatter for date to match
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy");
        String formatActive = active.format(formatter);
        
        sb.append("ID #").append(id).append(": ").append(lastName).append(", ")
                .append(firstName).append(" ").append(middleName).append(" (#")
                .append(badge.getId()).append("), Type: ")
                .append(employeeType).append(", Department: ")
                .append(department.getDescription()).append(", Active: ")
                .append(formatActive);
        
        result = sb.toString();
        
        return result;
    }
}
