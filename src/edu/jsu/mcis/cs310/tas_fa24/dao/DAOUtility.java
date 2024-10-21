package edu.jsu.mcis.cs310.tas_fa24.dao;

import edu.jsu.mcis.cs310.tas_fa24.Punch;
import edu.jsu.mcis.cs310.tas_fa24.Shift;
import edu.jsu.mcis.cs310.tas_fa24.EventType;
import java.time.*;
import java.util.*;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import com.github.cliftonlabs.json_simple.*;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;

/**
 * 
 * Utility class for DAOs.  This is a final, non-constructable class containing
 * common DAO logic and other repeated and/or standardized code, refactored into
 * individual static methods.
 * 
 */
public final class DAOUtility {

    /**
     * Converts a ResultSet to a HashMap.
     * The keys being ints matched to the index of the columns.
     * The values being strings of data in the rows of said columns.
     * @param rs, ResultSet of data. Cursor must before data.
     * @return mapOfShift, a map of the values 0-the size of columns.
     * @author William Saint
     * 
     */
    public static HashMap<String, String> resultSetToHashMap(ResultSet rs){

        //Hash map of the raw shift data, key is 0 through length of shift.
        HashMap<String, String> mapOfShift = new HashMap<>();

        try{
            ResultSetMetaData rsMeta = rs.getMetaData();

            int numberOfCols = rsMeta.getColumnCount();


            while(rs.next()) {

                //iterate over cols
                for (int i=1; i<=numberOfCols; i++) {
                    String colName = rsMeta.getColumnName(i);

                    mapOfShift.put(colName, rs.getString(colName));


                }

            }// end while
            }
            catch(SQLException e){
                throw new DAOException(e.getMessage());
            }

            return mapOfShift;
        }

    /**
     * Calculates the total number of minutes accrued by an employee within a single day.
     * This method takes a list of punches and a shift object as arguments, iterates through 
     * the punches, and calculates the number of minutes between clock-in and clock-out 
     * pairs, subtracting lunch breaks as necessary.
     * 
     * Time-out punches are ignored, and if an employee exceeds the shift's lunch threshold, 
     * the lunch break duration is deducted.
     * 
     * @param dailypunchlist A list of Punch objects representing the employee's daily punches
     * @param shift A Shift object representing the employee's shift rules
     * @return The total number of accrued minutes as an integer
     */
    public static int calculateTotalMinutes(ArrayList<Punch> dailypunchlist, Shift shift) {

      int totalMinutes = 0;
      LocalDateTime clockInTime = null;
      LocalDateTime clockOutTime = null;
    
      for (Punch punch : dailypunchlist) {
        // Adjust from Will's Adjust method, handles shift rules
        punch.adjust(shift);  
        EventType punchType = punch.getPunchtype();
        // clock-in punches, store timestamp
        if (punchType == EventType.CLOCK_IN) {
            clockInTime = punch.getOriginaltimestamp();
        } 
        
        // Time-out punches, store timestamp (not required)
        else if (punchType == EventType.TIME_OUT){
            continue;
        }
        // clock-out punches
        //refactor to use PunchAdjustmentType ENUM - William
        else if (punchType == EventType.CLOCK_OUT && clockInTime != null) {
            clockOutTime = punch.getOriginaltimestamp();
            // difference between in and out punches
            int minutesBetween = (int) ChronoUnit.MINUTES.between(clockInTime, clockOutTime);
            DayOfWeek dayOfWeek = clockInTime.getDayOfWeek();
             // Check if lunch deduction is applicable
            boolean isLunchPeriod = (clockInTime.toLocalTime().isBefore(shift.getLunchEnd()) && 
                                     clockOutTime.toLocalTime().isAfter(shift.getLunchStart()));
            // weekday & clock-in and clock-out span lunch
            if (dayOfWeek != DayOfWeek.SATURDAY && dayOfWeek != DayOfWeek.SUNDAY && isLunchPeriod) {
                int lunchBreak = shift.getLunchDuration();
                minutesBetween -= lunchBreak; // Deduct lunch time
            }
            // Accumulate the total minutes 
            totalMinutes += minutesBetween; 
            // Reset clock-in 
            clockInTime = null;
        }
    }

        // Return accrued minutes
        return totalMinutes;
    }
}

