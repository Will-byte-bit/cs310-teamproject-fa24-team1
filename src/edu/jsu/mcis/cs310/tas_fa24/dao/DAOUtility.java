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
        
        DateTimeFormatter formatterForFinal = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        int totalMinutes = 0;
        boolean clockInFound = false;
        LocalDateTime clockInTime = null;
        
        //change variable clockinTime to punch and use punch for both clocking and out
        for (Punch punch : dailypunchlist) {
            System.out.println(punch.getId());
            punch.adjust(shift);
            if (punch.getPunchtype() == EventType.CLOCK_IN) {
                // Found a clock-in punch, record the time
                clockInFound = true;
                
                System.out.println(punch.getOriginaltimestamp().format(formatterForFinal));
                clockInTime = punch.getChangetimestamp();
                
            }
            else if (punch.getPunchtype() == EventType.CLOCK_OUT && clockInFound) {
                // Found a clock-out punch, calculate time difference from last clock-in
                
                
                LocalDateTime clockOutTime = punch.getChangetimestamp();
                
                int minutesBetween = (int) ChronoUnit.MINUTES.between(clockInTime, clockOutTime);
                totalMinutes += minutesBetween;
                
                clockInFound = false;  // Reset for the next punch pair
                System.out.println(punch.getOriginaltimestamp().format(formatterForFinal));
                System.out.println(clockInTime.format(formatterForFinal));
                System.out.println(clockOutTime.format(formatterForFinal));
            }
            else if (punch.getPunchtype() == EventType.TIME_OUT) {
                // Skip over TIME_OUT punches
                clockInFound = false;
            }
        }

        // Check if lunch deduction is needed
        if (totalMinutes >= shift.getLunchThreshold()) {
            totalMinutes -= shift.getLunchDuration();
        }

        return totalMinutes;
    }
}
