/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24.dao;
import edu.jsu.mcis.cs310.tas_fa24.Badge;
import edu.jsu.mcis.cs310.tas_fa24.Shift;
import java.sql.*;



/**
 *
 * @author Will
 * <p>
 * This class implements two find functions, which pull data from the database and return a shift object
 * with the data provided by the function.
 * 
 * </p>
 */
public class ShiftDAO {
    
    //prepared statements for the two find methods
    private static final String QUERY_FIND_ID = "SELECT * FROM shift WHERE id = ?";
    private static final String QUERY_FIND_BADGE = "SELECT shiftid FROM employee WHERE badgeid = ?";
    
    private final int DEFAULT_ID = 0;
 

    private final DAOFactory daoFactory;
    
    
    public ShiftDAO(DAOFactory daoFactory){
        this.daoFactory = daoFactory;
    }
    
   
    public Shift find(int id) {

        Shift shift = null;

        PreparedStatement ps = null;
        ResultSet rs = null;

        try {

            Connection conn = daoFactory.getConnection();

            if (conn.isValid(0)) {

                ps = conn.prepareStatement(QUERY_FIND_ID);
                ps.setInt(1, id);

                boolean hasresults = ps.execute();

                if (hasresults) {

                    rs = ps.getResultSet();
                    System.out.println(rs);
                    shift = new Shift(DAOUtility.resultSetToHashMap(rs));
                    
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

        return shift;

    }
    
    public Shift find(Badge badge){
        System.out.println("test");
        
        //create vars
        PreparedStatement ps = null;
        int id = DEFAULT_ID;
        ResultSet rs = null;

        try {

            Connection conn = daoFactory.getConnection();

            if (conn.isValid(0)) {

                ps = conn.prepareStatement(QUERY_FIND_BADGE);
                System.out.println(badge.getId());
                ps.setString(1, badge.getId());
                

                boolean hasresults = ps.execute();
                System.out.println(hasresults);

                if (hasresults) {
                    
                    rs = ps.getResultSet();
                    rs.next();
                    id = rs.getInt("shiftid");
                   
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
