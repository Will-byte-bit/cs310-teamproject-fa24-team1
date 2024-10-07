/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24.dao;

import edu.jsu.mcis.cs310.tas_fa24.Badge;
import edu.jsu.mcis.cs310.tas_fa24.Department;
import edu.jsu.mcis.cs310.tas_fa24.Employee;
import edu.jsu.mcis.cs310.tas_fa24.EmployeeType;
import edu.jsu.mcis.cs310.tas_fa24.Shift;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.HashMap;

/**
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
                            Department department = new Department(departmentID, rsDepartment.getString("description"), rsDepartment.getInt("terminalid"));
                            
                            // Getting shift info and creating a shift object
                            int shiftID = rs.getInt("shiftID");
                            PreparedStatement psShift = conn.prepareStatement(QUERY_SHIFT_DATA);
                            psShift.setInt(1, shiftID);

                            ResultSet rsShift = psShift.executeQuery();
                            
                            if(rsShift.next()) {
                                HashMap<Integer, String> shiftData = new HashMap<>();
                                shiftData.put(0, rsShift.getString("id"));
                                shiftData.put(1, rsShift.getString("description"));
                                shiftData.put(2, rsShift.getString("shiftstart"));
                                shiftData.put(3, rsShift.getString("shiftstop"));
                                shiftData.put(4, rsShift.getString("roundinterval"));
                                shiftData.put(5, rsShift.getString("graceperiod"));
                                shiftData.put(6, rsShift.getString("dockpenalty"));
                                shiftData.put(7, rsShift.getString("lunchstart"));
                                shiftData.put(8, rsShift.getString("lunchstop"));
                                shiftData.put(9, rsShift.getString("lunchthreshold"));
                                
                                Shift shift = new Shift(shiftData);
                                
                                // Create and populate employee object
                                employee = new Employee(id, firstName, middleName, lastName, active, badge, department, shift, employeeType);
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
                    System.out.println(id);
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