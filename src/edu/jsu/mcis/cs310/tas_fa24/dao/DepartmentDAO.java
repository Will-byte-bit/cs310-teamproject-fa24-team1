/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24.dao;

import edu.jsu.mcis.cs310.tas_fa24.Department;
import java.sql.*;

/**
 *
 * @author joshwhaley
 */
public class DepartmentDAO {
    
    private final DAOFactory daoFactory;
    
    public DepartmentDAO(DAOFactory daoFactory){
        
        this.daoFactory = daoFactory;
        
    }
    
    // find method based off deptID + return
    public Department find(int deptID){
        
        Department department = null;
        String QueryFindDept = "SELECT * FROM department WHERE id = ?";
        ResultSet resultSet = null;
        
        try(PreparedStatement ps = daoFactory.getConnection().prepareStatement(QueryFindDept)){
            
            ps.setInt(1, deptID);
            resultSet = ps.executeQuery();
            
            if(resultSet.next()){
                
                String description = resultSet.getString("description");
                int terminalID = resultSet.getInt("terminalID");
                department = new Department(deptID, description, terminalID);
            
            }
            
            ps.close();
        
        }
        
        catch (SQLException e) {

            throw new DAOException(e.getMessage());

        } finally {

            if (resultSet != null) {
                try {
                    resultSet.close();
                } catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
        }

            
        return department;        
    }
     
    
    }
}
