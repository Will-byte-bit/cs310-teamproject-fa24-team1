/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24.dao;

import edu.jsu.mcis.cs310.tas_fa24.Badge;
import edu.jsu.mcis.cs310.tas_fa24.Employee;
import java.sql.*;

/**
 *
 * @author Madison
 */
public class EmployeeDAO {
    private static final String QUERY_FIND_ID = "SELECT * FROM employee WHERE id = ?";
    private static final String QUERY_FIND_BADGE = "SELECT id FROM employee WHERE badgeid = ?";
    
    private final DAOFactory daoFactory;
    
    public EmployeeDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }
    
    public Employee find(int id) {
        Employee employee = null;
        
        PreparedStatement ps = null;
        ResultSet rs = null;
        boolean hasResults;
        
        try {
            Connection conn = daoFactory.getConnection();
            
            if (conn.isValid(0)) {
                ps = conn.prepareCall(QUERY_FIND_ID);
                ps.setInt(1, id);
                
                hasResults = ps.execute();
                
                if (hasResults) {
                    rs = ps.getResultSet();
                    
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
        boolean hasResults;
        int id = 0;
        
        try {
            Connection conn = daoFactory.getConnection();
            
            if (conn.isValid(0)) {
                ps = conn.prepareCall(QUERY_FIND_BADGE);
                ps.setString(1, badge.getId());
                
                hasResults = ps.execute();
                
                if (hasResults) {
                    rs = ps.getResultSet();
                    rs.next();
                    id = rs.getInt("id");
                }
            }
        } catch (SQLException e) {
            throw new DAOException(e.getLocalizedMessage());
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
