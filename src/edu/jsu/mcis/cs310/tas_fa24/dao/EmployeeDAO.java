package edu.jsu.mcis.cs310.tas_fa24.dao;

import edu.jsu.mcis.cs310.tas_fa24.*;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;

/**
 * What This Code Does: This file is used to find an employee with their id 
 * number or badge number.
 * The Functions: 
 * find(int id) - This function takes the employees known id number and finds
 * the rest of the information regarding that certain employee and returns a new
 * employee object that is populated with the correct details.
 * find(Badge badge) - This function takes the employees known badge number and
 * finds the related id number and returns the find(id) function which uses the
 * found id in the first function.
 * 
 * @author Madison
 */
public class EmployeeDAO {
    private static final String QUERY_FIND_ID = "SELECT * FROM employee WHERE id = ?";
    private static final String QUERY_FIND_BADGE = "SELECT id FROM employee WHERE badgeid = ?";
    private static final String QUERY_BADGE_DESCRIPTION = "SELECT * FROM badge WHERE id = ?";
    private static final String QUERY_DEPARTMENT_DESCRIPTION = "SELECT * FROM department WHERE id = ?";
    private static final String QUERY_SHIFT_DATA = "SELECT * FROM shift WHERE id = ?";
    private static final int DEFAULT_ID = 0;
    
    private final DAOFactory daoFactory;
    
    public EmployeeDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }
    
    public Employee find(int id) {
        Employee employee = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
        
        try {
            Connection conn = daoFactory.getConnection();
            
            if (conn.isValid(0)) {
                ps = conn.prepareStatement(QUERY_FIND_ID);
                ps.setInt(1, id);
                
                rs = ps.executeQuery();
                        
                while(rs.next()) {
                    
                    // Getting employee type based on the id
                    int employeeTypeNum = rs.getInt("employeetypeid");
                    EmployeeType employeeType = null;
                    switch(employeeTypeNum) {
                        case 0 -> employeeType = EmployeeType.PART_TIME;
                        case 1 -> employeeType = EmployeeType.FULL_TIME;
                    }
                    
                    LocalDateTime active = rs.getTimestamp("active").toLocalDateTime();
                    
                    // Getting badge info and creating a badge object
                    String badgeID = rs.getString("badgeid");
                    PreparedStatement psBadge = conn.prepareStatement(QUERY_BADGE_DESCRIPTION);
                    psBadge.setString(1, badgeID);

                    ResultSet rsBadge = psBadge.executeQuery();
                    if(rsBadge.next()) {
                        String badgeDescription = rsBadge.getString("description");
                        
                        // Getting first, middle, and last name from badge
                        String [] nameParts = badgeDescription.split(", ");
                        String lastName = nameParts[0].trim();
                        String[] firstMiddleNameParts = nameParts[1].split(" ");
                        String firstName = firstMiddleNameParts[0].trim();
                        String middleName = firstMiddleNameParts[1].trim();
                        
                        Badge badge = new Badge(badgeID, badgeDescription);
                        
                        // Getting department info and creating a department object
                        int departmentID = rs.getInt("departmentid");
                        PreparedStatement psDepartment = conn.prepareStatement(QUERY_DEPARTMENT_DESCRIPTION);
                        psDepartment.setInt(1, departmentID);

                        ResultSet rsDepartment = psDepartment.executeQuery();
                                
                        if(rsDepartment.next()) {
                            Department department = new Department(departmentID,
                                rsDepartment.getString("description"), 
                                rsDepartment.getInt("terminalid"));
                            
                            // Getting shift info and creating a shift object
                            int shiftID = rs.getInt("shiftID");
                            PreparedStatement psShift = conn.prepareStatement(QUERY_SHIFT_DATA);
                            psShift.setInt(1, shiftID);

                            
                            
                            boolean resultsForShift = psShift.execute();
                            
                            if(resultsForShift) {
                                
                                ResultSet rsShift = psShift.getResultSet();
                                
                                Shift shift = new Shift(DAOUtility.resultSetToHashMap(rsShift));
                                
                                // Create and populate employee object
                                employee = new Employee(id, firstName, 
                                    middleName, lastName, active, badge, department,
                                    shift, employeeType);
                            }
                        }
                    }
                }  
            }
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new DAOException (e.getMessage());
                }
            }
        }
        return employee;
    }
    
    public Employee find(Badge badge) {
        PreparedStatement ps = null;
        ResultSet rs = null;
        int id = DEFAULT_ID;
        
        try {
            Connection conn = daoFactory.getConnection();
            
            if (conn.isValid(0)) {
                ps = conn.prepareStatement(QUERY_FIND_BADGE);
                ps.setString(1, badge.getId());
                
                boolean hasResults = ps.execute();
                
                if (hasResults) {
                    rs = ps.getResultSet();
                    rs.next();
                    id = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new DAOException(e.getMessage());
        } finally {
            if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
            if (ps != null) {
                try {
                    ps.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
        }
        return find(id);
    } 
}