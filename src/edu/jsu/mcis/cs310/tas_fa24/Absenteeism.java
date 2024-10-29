package edu.jsu.mcis.cs310.tas_fa24;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * Model Class for showing Employee Absenteeism
 * 
 * Constructor for Absenteeism
 * Getters for each field
 * To-String Override
 * @author samca
 */
public class Absenteeism {
	private Employee employee = null;
	private LocalDate startDate = null;
	private BigDecimal absenteeismPercentage = null;
	private Punch punch = null;
	private Shift shift = null;
	private Badge badge = null;
	
	
	// Constructor
	public Absenteeism(Employee employee, LocalDate startDate, BigDecimal absenteeismPercentage, 
		Punch punch, Shift shift, Badge badge) {
		
		this.employee = employee;
		this.startDate = startDate;
		this.absenteeismPercentage = absenteeismPercentage;
		this.punch = punch;
		this.shift = shift;
		this.badge = badge;
	}
	
	// Getters
	public Employee getEmployee() {
		return employee;
	}
	public LocalDate getStartDate() {
		return startDate;
	}
	public BigDecimal getabsenteeismPercentage() {
		return absenteeismPercentage;
	}
	public Punch getPunch() {
		return punch;
	}
	public Shift getShift() {
		return shift;
	}
	public Badge getBadge() {
		return badge;
	}
	
	// To-String
	public String ToString() {
		String result;
		StringBuilder s = new StringBuilder();
		s.append("#").append(badge).append("(Pay Period Starting: ").append(startDate).append("): ")
		.append(absenteeismPercentage).append("%");
		
		result = s.toString();
		return result;
	}
}
