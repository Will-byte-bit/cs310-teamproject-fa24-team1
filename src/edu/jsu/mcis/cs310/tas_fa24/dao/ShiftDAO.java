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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.HashMap;
import java.sql.Timestamp;
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
     * Helper Method gets a DailySchedule from the ResultSet.
     *
     * @param rs the ResultSet containing schedule data
     * @return a DailySchedule object with populated fields
     * 
     * @author Josh Whaley
     */
    
    // after researching the way I found to use the resultset to get the schedule contents
    private DailySchedule extractDailySchedule(ResultSet rs) throws SQLException {
        LocalTime shiftStart = rs.getTime("shiftstart").toLocalTime();
        LocalTime shiftEnd = rs.getTime("shiftstop").toLocalTime();
        LocalTime lunchStart = rs.getTime("lunchstart").toLocalTime();
        LocalTime lunchEnd = rs.getTime("lunchstop").toLocalTime();
        int roundinterval = rs.getInt("roundinterval");
        int gracePeriod = rs.getInt("graceperiod");
        int dockPenalty = rs.getInt("dockpenalty");
        int lunchThreshold = rs.getInt("lunchthreshold");

        return new DailySchedule(shiftStart, shiftEnd, lunchStart, lunchEnd, roundinterval, gracePeriod, dockPenalty, lunchThreshold);
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
               
                ps.setString(1, badge.getId());
                

                boolean hasresults = ps.execute();
              

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
     * @author William Saint, sub author Josh Whaley.
     */
    public Shift find(Badge badge, LocalDate payPeriodStartDate) {
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        //grab defualt schedule based on badge WS
        DailySchedule defaultSchedule = find(badge).getDefaultSchedule();
        HashMap<DayOfWeek, DailySchedule> dailyOverrides = new HashMap<>();
        
        LocalDateTime getStartOfPayPeriod = LocalDateTime.of(payPeriodStartDate.with(TemporalAdjusters.previousOrSame(DayOfWeek.SUNDAY)), LocalTime.MIDNIGHT.withNano(0));
        LocalDateTime getEndOfPayPeriod = LocalDateTime.of(payPeriodStartDate.with(TemporalAdjusters.next(DayOfWeek.SATURDAY)), LocalTime.MAX.withNano(0));

        Timestamp begin = Timestamp.valueOf(getStartOfPayPeriod.format(formatter));
        Timestamp end = Timestamp.valueOf(getEndOfPayPeriod.format(formatter));
        
        
        //populating hashmap with defualt shift.
        for(int day = 1; day <= 5; day++){
            dailyOverrides.put(DayOfWeek.of(day), defaultSchedule);
        }
     
        Connection conn = daoFactory.getConnection();
                    
        //grab whole database
        String overrideQuery = "SELECT * FROM scheduleoverride";
        try {
            
            PreparedStatement pst = conn.prepareStatement(overrideQuery);
            
            ResultSet rs = pst.executeQuery();
            while(rs.next()){
                int dailyScheduleId = rs.getInt("dailyscheduleid");
                DayOfWeek dayOfWeek = DayOfWeek.of(rs.getInt("day"));
                DailySchedule dailySchedule = getDailyScheduleById(dailyScheduleId);
                
                boolean fullSend = dailySchedule != null;
                
                String badgeGotten = rs.getString("badgeid");
                Timestamp startGotten = rs.getTimestamp("start");
                Timestamp endGotten = rs.getTimestamp("end");
                
                if(badgeGotten == null && startGotten.compareTo(begin) >= 0 && endGotten == null && fullSend){
                    dailyOverrides.put(dayOfWeek, dailySchedule);
                }
                else  if(badgeGotten != null && badgeGotten.equals(badge.getId()) && startGotten.compareTo(begin) <= 0 && endGotten == null && fullSend){
                    dailyOverrides.put(dayOfWeek, dailySchedule);
                }
                else  if(badgeGotten == null && startGotten.compareTo(begin) >= 0 && endGotten != null && endGotten.compareTo(end) <= 0 && fullSend){
                    dailyOverrides.put(dayOfWeek, dailySchedule);
                }
                else if(badgeGotten != null && badgeGotten.equals(badge.getId()) && startGotten.compareTo(begin) >= 0 && endGotten != null && endGotten.compareTo(end) <= 0 && fullSend){
                    dailyOverrides.put(dayOfWeek, dailySchedule);
                }
            }
            
           
        }
        catch(SQLException e){
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

   
}
