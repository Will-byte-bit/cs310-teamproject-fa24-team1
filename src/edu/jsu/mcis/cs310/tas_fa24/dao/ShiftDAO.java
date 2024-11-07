/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24.dao;
import edu.jsu.mcis.cs310.tas_fa24.Badge;
import edu.jsu.mcis.cs310.tas_fa24.DailySchedule;
import edu.jsu.mcis.cs310.tas_fa24.Shift;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.DayOfWeek;
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
    private static final String QUERY_FIND_ID_SHIFT= "SELECT * FROM shift WHERE id = ?";
    private static final String QUERY_FIND_ID_DAILY= "SELECT * FROM dailyschedule WHERE id = ?";
    private static final String QUERY_FIND_BADGE = "SELECT shiftid FROM employee WHERE badgeid = ?";
    
    private final int DEFAULT_ID = 0;
 

    private final DAOFactory daoFactory;
    
    
    public ShiftDAO(DAOFactory daoFactory){
        this.daoFactory = daoFactory;
    }
    
    /**
    * 
    * returns a shift object based on the ID provided. Refactored by William Sint
    * @param id, id of shift
    * @return shift, created shift object.
    */

    public Shift find(int id) {
        
        HashMap<String, String> map = new HashMap<>();

        Shift shift = null;
        DailySchedule daily  = null;
        PreparedStatement ps = null;
        ResultSet rs = null;
  

        try {

            Connection conn = daoFactory.getConnection();

            if (conn.isValid(0)) {

                ps = conn.prepareStatement(QUERY_FIND_ID_DAILY);
                ps.setInt(1, id);

                boolean hasresults = ps.execute();
                if (hasresults) {

                    rs = ps.getResultSet();
                   
                   daily = new DailySchedule(DAOUtility.resultSetToHashMap(rs));
                }


                ps = conn.prepareStatement(QUERY_FIND_ID_SHIFT);
                ps.setInt(1, id);
                hasresults = ps.execute();
                if (hasresults) {
                    

                    rs = ps.getResultSet();
                    rs.next();
                    
                   
                   shift = new Shift(Integer.parseInt(rs.getString("id")), rs.getString("description"), daily);
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
    
    
     /**
     * Finds a Shift with specific daily schedule overrides for an employee's badge
     * and pay period. Retrieves both the default schedule and overrides.
     * 
     * @param badge employee badge
     * @param payPeriodStartDate start date of pay period
     * @return a Shift with daily schedule overrides
     * 
     * @author Josh Whaley
     */
    public Shift find(Badge badge, LocalDate payPeriodStartDate) {
        DailySchedule defaultSchedule = null;
        HashMap<DayOfWeek, DailySchedule> dailyOverrides = new HashMap<>();
        Connection conn = daoFactory.getConnection();
                    
        try {
            // find Default Schedule for the Shift
            String defaultScheduleQuery = "SELECT * FROM dailyschedule WHERE is_default = true";
            try (PreparedStatement pst = conn.prepareStatement(defaultScheduleQuery)) {
                ResultSet rs = pst.executeQuery();
                if (rs.next()) {
                    defaultSchedule = extractDailySchedule(rs);
                }
            }

            // find Schedule Overrides from scheduleOverride table matching employee's badgeid && when start and end overlap
            String overrideQuery = "SELECT * FROM tas_fa24_v2.scheduleoverride WHERE badgeid = ? AND start <= ? AND end >= ?";
            try (PreparedStatement pst = conn.prepareStatement(overrideQuery)) {
                
                // sets badge to string for the ResultSet
                String badge1 = badge.toString();
                pst.setString(1, badge1);
                pst.setDate(2, java.sql.Date.valueOf(payPeriodStartDate));
                pst.setDate(3, java.sql.Date.valueOf(payPeriodStartDate));
                ResultSet rs = pst.executeQuery();
                
                while (rs.next()) {
                    int dailyScheduleId = rs.getInt("dailyscheduleid");
                    DayOfWeek dayOfWeek = DayOfWeek.of(rs.getInt("day"));

                    // If there is an override, set it to the proper default schedule depending on the day
                    DailySchedule dailySchedule = getDailyScheduleById(dailyScheduleId);
                    if (dailySchedule != null) {
                        dailyOverrides.put(dayOfWeek, dailySchedule);
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new Shift(defaultSchedule, dailyOverrides);
    }

    /**
     * Helper Method that gets DailySchedule by its ID from the dailyschedule table.
     * 
     * @param id the ID of DailySchedule object
     * @return DailySchedule object
     * 
     * @author Josh Whaley
     */
    
    private DailySchedule getDailyScheduleById(int id) {
        DailySchedule schedule = null;
        String query = "SELECT * FROM dailyschedule WHERE id = ?";
        Connection conn = daoFactory.getConnection();
        
        // run Query
        try (PreparedStatement pst = conn.prepareStatement(query)) {
            
            // set id and store query results
            pst.setInt(1, id);
            ResultSet rs = pst.executeQuery();
            
            if (rs.next()) {
                // gets to the proper schedule 
                schedule = extractDailySchedule(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        
        // return results as schedule for day.
        return schedule;
    }

    /**
     * Helper Method gets a DailySchedule from the ResultSet.
     *
     * @param rs the ResultSet containing schedule data
     * @return a DailySchedule object with populated fields
     * 
     * @author Josh Whaley
     */
    
    // after researching the way I found to use the resultset to get the schedule contents
    private DailySchedule extractDailySchedule(ResultSet rs) throws SQLException {
        LocalTime shiftStart = rs.getTime("shift_start").toLocalTime();
        LocalTime shiftEnd = rs.getTime("shift_end").toLocalTime();
        LocalTime lunchStart = rs.getTime("lunch_start").toLocalTime();
        LocalTime lunchEnd = rs.getTime("lunch_end").toLocalTime();
        int interval = rs.getInt("interval");
        int gracePeriod = rs.getInt("grace_period");
        int dockPenalty = rs.getInt("dock_penalty");
        int lunchThreshold = rs.getInt("lunch_deduction_threshold");

        return new DailySchedule(shiftStart, shiftEnd, lunchStart, lunchEnd, interval, gracePeriod, dockPenalty, lunchThreshold);
    }

}
