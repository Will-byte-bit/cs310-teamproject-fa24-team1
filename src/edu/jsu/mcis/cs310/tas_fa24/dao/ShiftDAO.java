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
 *
 * @author Will
 */
public class ShiftDAO {
    
    //prepared statements for the two find methods
    private static final String QUERY_FIND_ID = "SELECT * FROM shift WHERE id = ?";
    private static final String QUERY_FIND_BADGE = "SELECT shiftid FROM employee WHERE badge = ?";
    
    private final int DEFUALT_ID = 0;
 

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
                    shift = new Shift(resultSetToHashMap(rs));
                    
                    
                   
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
        
        //create vars
        PreparedStatement ps = null;
        int id = DEFUALT_ID;
        ResultSet rs = null;

        try {

            Connection conn = daoFactory.getConnection();

            if (conn.isValid(0)) {

                ps = conn.prepareStatement(QUERY_FIND_BADGE);
                ps.setString(1, badge.getId());

                boolean hasresults = ps.execute();

                if (hasresults) {

                    rs = ps.getResultSet();
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
    public HashMap<Integer, String> resultSetToHashMap(ResultSet rs){
        
         //Hash map of the raw shift data, key is 0 through length of shift.
         HashMap<Integer, String> mapOfShift = new HashMap<Integer, String>();
         
         try{
            ResultSetMetaData rsMeta = rs.getMetaData();

            int numberOfCols = rsMeta.getColumnCount();
            while(rs.next()) {

              //iterate over cols
              for (int i=1; i<=numberOfCols; i++) {
                String colName = rsMeta.getColumnName(i);
                mapOfShift.put(i-1, rs.getString(colName));

              }

           }
         }
         catch(SQLException e){
             throw new DAOException(e.getMessage());
         }
         return mapOfShift;
     }

}
