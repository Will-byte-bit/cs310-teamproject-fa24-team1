package edu.jsu.mcis.cs310.tas_fa24;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.DayOfWeek;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;

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
	private LocalDate payPeriodStart = null;
	private BigDecimal absenteeismPercentage = null;
	
	
	// Constructor
	public Absenteeism(Employee employee, LocalDate payPeriodStart, BigDecimal absenteeismPercentage) {
	    this.employee = employee;
	    this.payPeriodStart = payPeriodStart.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY));
	    this.absenteeismPercentage = absenteeismPercentage.setScale(2, RoundingMode.HALF_UP);
	}
	
	// Getters
	public Employee getEmployee() {
	    return employee;
	}
	public LocalDate getPayPeriodStart() {
	    return payPeriodStart;
	}
	public BigDecimal getAbsenteeismPercentage() {
	    return absenteeismPercentage;
	}

	
	// To-String
	@Override
	public String toString() {
	    
	    System.out.println("Debug in toString - Absenteeism Percentage: " + absenteeismPercentage);
	    String result;
	    StringBuilder s = new StringBuilder();
	    s.append("#");
	    s.append(employee.getBadge().getId());
	    s.append(" (Pay Period Starting ");
	    s.append(payPeriodStart.format(DateTimeFormatter.ofPattern("MM-dd-yyyy")));
	    s.append("): ");
	    s.append(absenteeismPercentage);
	    s.append("%");
	    
	    result = s.toString();
	    return result;
	}
}
