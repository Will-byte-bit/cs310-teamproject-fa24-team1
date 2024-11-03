/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24.dao;
import edu.jsu.mcis.cs310.tas_fa24.Badge;
import edu.jsu.mcis.cs310.tas_fa24.Shift;
import java.sql.*;
import java.util.HashMap;



/**
 * This class pulls shift data from the database.
 * It implements two find functions, which pull data from the database and return a shift object.
 * with the data provided by the database
 * One function takes an ID, another a badge.
 * Both functions use a DAO utility class call resultSetToHashMap()
 * @see #DAOUtility.resultSetToHashmap()
 * 
 * @author William Saint
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
    
    /**
    * 
    * returns a shift object based on the ID provided.
    * @param id, id of shift
    * @return shift, created shift object.
    */

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
    /**
    * 
    * returns a shift object based on the badge provided.
    * @param badge, badge of shift
    * @return shift, created shift object.
    */
    public Shift find(Badge badge){
        
        //create vars
        PreparedStatement ps = null;
        int id = DEFAULT_ID;
        ResultSet rs = null;

        try {

            Connection conn = daoFactory.getConnection();

            if (conn.isValid(0)) {

                ps = conn.prepareStatement(QUERY_FIND_BADGE);
               // System.out.println(badge.getId());
                ps.setString(1, badge.getId());
                

                boolean hasresults = ps.execute();
                //System.out.println(hasresults);

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
