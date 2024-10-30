package edu.jsu.mcis.cs310.tas_fa24.dao;

import edu.jsu.mcis.cs310.tas_fa24.Absenteeism;
import edu.jsu.mcis.cs310.tas_fa24.Employee;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.sql.*;


/**
 *
 * @author samca
 */ 
public class AbsenteeismDAO {
    private final DAOFactory daoFactory;
    private static final String QUERY_FIND = "SELECT * FROM absenteeism WHERE employeeid = ? AND payperiod = ?";
    private static final String QUERY_INSERT = "INSERT INTO absenteeism (employeeid, payperiod, percentage) VALUES (?, ?, ?)";
    private static final String QUERY_UPDATE = "UPDATE absenteeism SET percentage = ? WHERE employeeid = ? AND payperiod = ?";
    private static final String QUERY_CHECK_EXISTENCE = "SELECT * FROM absenteeism WHERE employeeid = ? AND payperiod = ?";
    
    public AbsenteeismDAO(DAOFactory dAOFactory) {
	this.daoFactory = dAOFactory;
    }
	
	
    // Find an employee's absenteeism record
    public Absenteeism find(Employee employee, LocalDate payPeriodStart) {
	Absenteeism absenteeism = null;
	PreparedStatement ps = null;
	ResultSet rs = null;
		
	try {
	    Connection conn = daoFactory.getConnection();
            
	    if (conn.isValid(0)){
		// Statements
		ps = conn.prepareStatement(QUERY_FIND);
		ps.setInt(1, employee.getId());
		ps.setDate(2, java.sql.Date.valueOf(payPeriodStart));
		rs = ps.executeQuery();
		if (rs.next()) {
		    double percentageValue = rs.getDouble("percentage");
		    BigDecimal percentage = BigDecimal.valueOf(percentageValue).setScale(2, RoundingMode.HALF_UP);
		    System.out.println("Retrieved Absenteeism Percentage from DB: " + percentage);
		    absenteeism = new Absenteeism(employee, payPeriodStart, percentage);
		}
	    }
	} catch (SQLException e) {
	    throw new DAOException("Error finding absenteeism record: " + e.getMessage());
	} finally {
	    if (rs != null) try { rs.close(); } catch (SQLException e) { throw new DAOException(e.getMessage()); }
	    if (ps != null) try { ps.close(); } catch (SQLException e) { throw new DAOException(e.getMessage()); }
	}
      
	return absenteeism;
    }
	
    // Create 
    public void create(Absenteeism absenteeism) {

	PreparedStatement ps = null;
	ResultSet rs = null;
	
	try {
	    Connection conn = daoFactory.getConnection();
	    if (conn.isValid(0)) {
		// Check if record exists
		ps = conn.prepareStatement(QUERY_CHECK_EXISTENCE);
		ps.setInt(1, absenteeism.getEmployee().getId());
		ps.setDate(2, java.sql.Date.valueOf(absenteeism.getPayPeriodStart()));
		rs = ps.executeQuery();
		// Scale percentage
		BigDecimal scaledPercentage = absenteeism.getAbsenteeismPercentage().setScale(2, RoundingMode.HALF_UP);

		
		// Choose query based on record existence
		if (rs.next()) {
		    // Record exists, update it
		    ps = conn.prepareStatement(QUERY_UPDATE);
		    ps.setBigDecimal(1, scaledPercentage);
		    ps.setInt(2, absenteeism.getEmployee().getId());
		    ps.setDate(3, java.sql.Date.valueOf(absenteeism.getPayPeriodStart()));
		} else {
		    // Record doesn't exist, insert it
		    ps = conn.prepareStatement(QUERY_INSERT);
		    ps.setInt(1, absenteeism.getEmployee().getId());
		    ps.setDate(2, java.sql.Date.valueOf(absenteeism.getPayPeriodStart()));
		    ps.setBigDecimal(3, scaledPercentage);
		}
		System.out.println("Absenteeism Percentage to be stored: " + scaledPercentage);
		// Execute update
		ps.executeUpdate();
		}
	} catch (SQLException e) {
	    throw new DAOException("Error creating/updating absenteeism record: " + e.getMessage());				
	} finally {
	    try {
		if (rs != null) rs.close();
		if (ps != null) ps.close();
	    } catch (SQLException e) {
		throw new DAOException("Error closing resources: " + e.getMessage());
	    }
	}		
    }
	
}
