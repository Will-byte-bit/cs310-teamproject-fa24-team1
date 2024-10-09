package edu.jsu.mcis.cs310.tas_fa24.dao;

import edu.jsu.mcis.cs310.tas_fa24.Department;
import java.sql.*;

/**
 *
 * @author joshwhaley
 * 
 * This class makes use of the daoFactory in order to create a find() method for
 * the department, taking the department id (int deptID) as a parameter, which 
 * will then return all the information of that department. The information is 
 * then formatted as a department object. Additionally, I added a method that 
 * finds the department based off the terminal id (int terminalID). 
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
        
        // connection and load query into statement
        try(PreparedStatement ps = daoFactory.getConnection().prepareStatement(QueryFindDept)){
            
            // get deptID
            ps.setInt(1, deptID);
            resultSet = ps.executeQuery();
            
            // retrieve fields from resultSet
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
        
        
    // find department method based off terminalID
    // find a way to make this method reachable
    public Department findByTermID(int terminalID){
        
        Department department = null;
        String QueryFindTermID = "SELECT * FROM department WHERE terminalid = ?";
        ResultSet resultSet = null;
        
        try(PreparedStatement ps = daoFactory.getConnection().prepareStatement(QueryFindTermID)){
            
            // get deptID
            ps.setInt(1, terminalID);
            resultSet = ps.executeQuery();
            
            // retrieve fields from resultSet
            if(resultSet.next()){
                
                String description = resultSet.getString("description");
                int deptID = resultSet.getInt("id");
                department = new Department(deptID, description, terminalID);
            
            }
            
            ps.close();
            
        } catch (SQLException e) {

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
