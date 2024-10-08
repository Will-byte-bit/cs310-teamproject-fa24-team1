/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24.dao;

import edu.jsu.mcis.cs310.tas_fa24.Badge;
import edu.jsu.mcis.cs310.tas_fa24.Punch;
import edu.jsu.mcis.cs310.tas_fa24.EventType;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

/**
 *
 * @author Will
 * <p>
 * PunchDao, like shift dao, creates a dao factory object to be used. The find method retrieves information from the ID.
 * The list function is a bolier plate retrieving function. Will be implemented later.
 */
public class PunchDAO {
    private static final String QUERY_FIND_ID = "SELECT * FROM event WHERE id = ?";
    private static final String QUERY_FIND_BADGE = "SELECT description FROM badge WHERE id = ?";
    
    private final int DEFAULT_ID = 0;
    private final DAOFactory daoFactory;
    
    
    public PunchDAO(DAOFactory daoFactory){
        this.daoFactory = daoFactory;
    }
     public Punch find(int id) {

        Punch punch = null;

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
 
                    while(rs.next()){
                        
                        //getting info for badge and making badge
                        PreparedStatement psForBadge = conn.prepareStatement(QUERY_FIND_BADGE);
                        String badgeId = rs.getString("badgeid");
                        psForBadge.setString(1, badgeId);
                        
                        //if badge found run
                        if(psForBadge.execute()){
                            
                            ResultSet rsForBadge = psForBadge.getResultSet();
                            
                            //move cursor forwards
                            rsForBadge.next();

                            int typeId = rs.getInt("eventtypeid");
                            

                            //setting event type based on type id
                            EventType et = null;
                            switch(typeId){
                                case 0 -> et = EventType.CLOCK_OUT;
                                case 1 -> et = EventType.CLOCK_IN;
                                case 2 -> et = EventType.TIME_OUT;
                            }
                         
                            //creating badge, creating punch
                            Badge badge = new Badge(badgeId, rsForBadge.getString("description"));
                            
                            System.out.println(rs.getTimestamp("timestamp"));
                            
                            //formatting timestamp and removing unused data.
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            LocalDateTime dateTime = LocalDateTime.parse(rs.getTimestamp("timestamp").toString().substring(0, 19), formatter);
                             
                            //creating punch
                            punch = new Punch(id, rs.getInt("terminalid"), badge, dateTime,et);
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
                    throw new DAOException(e.getMessage());
                }
            }

        }

        return punch;

    }
    
     //bolier plate class to be used later
    public ArrayList<Punch> List(Badge badge){
       
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
        
        return null;
    }
  
    
}
