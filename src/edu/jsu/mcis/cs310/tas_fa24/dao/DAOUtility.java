package edu.jsu.mcis.cs310.tas_fa24.dao;

import edu.jsu.mcis.cs310.tas_fa24.Punch;
import edu.jsu.mcis.cs310.tas_fa24.PunchAdjustmentType;
import edu.jsu.mcis.cs310.tas_fa24.Shift;
import edu.jsu.mcis.cs310.tas_fa24.EventType;
import java.time.*;
import java.util.*;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;
import com.github.cliftonlabs.json_simple.*;
import java.math.BigDecimal;
import java.math.RoundingMode;
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
	boolean lunchDeducted = false;

	for (Punch punch : dailypunchlist) {
	    punch.adjust(shift);  // Adjust the punch according to the shift rules
	    EventType punchType = punch.getPunchtype();

	    // Handle CLOCK_IN punches
	    if (punchType == EventType.CLOCK_IN) {
		clockInTime = punch.getChangetimestamp();  // Set clock-in time
	    }
	    // Handle CLOCK_OUT punches if clockInTime is already set
	    else if (punchType == EventType.CLOCK_OUT && clockInTime != null) {
		clockOutTime = punch.getChangetimestamp();  // Set clock-out time

		// Calculate minutes worked between clock-in and clock-out
		int minutesWorked = (int) ChronoUnit.MINUTES.between(clockInTime, clockOutTime);

		// Check if the clock-in and clock-out span the lunch period
		boolean spansLunch = (clockInTime.toLocalTime().isBefore(shift.getLunchEnd()) && 
				      clockOutTime.toLocalTime().isAfter(shift.getLunchStart()));

		// Deduct lunch if necessary and ensure it's only deducted once
		if (spansLunch && !lunchDeducted) {
		    minutesWorked -= shift.getLunchDuration();
		    lunchDeducted = true;
		}

		// Accumulate the total minutes with any necessary rounding
		totalMinutes += applyRounding(minutesWorked, shift);

		// Reset clockInTime for the next pair
		clockInTime = null;
	    }
	}
	return totalMinutes;
    }







    /**
     * Helper method to apply rounding based on the shift's rounding rules.
     * @param minutesWorked The total minutes worked for a clock-in/clock-out pair
     * @param shift The shift object containing rounding rules
     * @return Rounded minutes based on the shift's rounding interval
     */
    private static int applyRounding(int minutesWorked, Shift shift) {
        int roundingInterval = shift.getRoundingInterval();
        int remainder = minutesWorked % roundingInterval;

        if (remainder >= (roundingInterval / 2)) {
            return minutesWorked + (roundingInterval - remainder);
        } else {
            return minutesWorked - remainder;
        }
    }

    /**
     * Helper method to check if the given day is a weekday (Mon-Fri).
     * @param timestamp The LocalDateTime to check
     * @return true if the day is a weekday, false otherwise
     */
    private static boolean isWeekday(LocalDateTime timestamp) {
        DayOfWeek day = timestamp.getDayOfWeek();
        return (day != DayOfWeek.SATURDAY && day != DayOfWeek.SUNDAY);
    }
    
  
    /**
     * Calculate Absenteeism from actual worked days compared to scheduled worked days.
     * @author samca
     * @param punchList The list of Punch objects representing the employee's punches within a pay period.
     * @param shift The Shift object containing the scheduled work hours and break times.
     * @return A BigDecimal representing the absenteeism percentage.
     */
    
    public static BigDecimal calculateAbsenteeism(ArrayList<Punch> punchList, Shift shift) {
	// Calculate total worked minutes
	int totalWorkedMinutes = getWeeklyWorkedMinutes(punchList, shift);
	int scheduledMinutes = (shift.getShiftDuration() * 5 - (shift.getLunchDuration() * 5));
	
	// Absenteeism formula
	double percentage = ((double) totalWorkedMinutes / scheduledMinutes);
	return BigDecimal.valueOf((1 - percentage) * 100).setScale(2, RoundingMode.HALF_UP);
    }
    
    /**
     * @author samca
     * @param punchList
     * @param shift
     * @return Returns total worked minutes from a range of dates.
     */
    public static int getWeeklyWorkedMinutes(ArrayList<Punch> punchList, Shift shift) {
        Map<LocalDate, ArrayList<Punch>> dailyPunches = new HashMap<>();
        
        // Group punches by date
        for (Punch punch : punchList) {
            LocalDate date = punch.getOriginaltimestamp().toLocalDate();
            dailyPunches.putIfAbsent(date, new ArrayList<>());
            dailyPunches.get(date).add(punch);
        }

        int totalWorkedMinutes = 0;
        
        // Calculate total worked minutes across all days
        for (ArrayList<Punch> dailyPunchList : dailyPunches.values()) {
            totalWorkedMinutes += calculateTotalMinutes(dailyPunchList, shift);
        }
        
        return totalWorkedMinutes;
    }
    
    public static JsonObject convertPunchToJSONMap(Punch punch){
        JsonObject mapOfPunch = new JsonObject();

        mapOfPunch.put("terminalid", Integer.toString(punch.getTerminalid()));
        mapOfPunch.put("id", Integer.toString(punch.getId()));
        mapOfPunch.put("badgeid", punch.getBadge().getId());
        mapOfPunch.put("punchtype", punch.getPunchtype().toString());
        mapOfPunch.put("adjustmenttype", punch.getAdjustedtimestamp().toString());
        mapOfPunch.put("originaltimestamp", punch.jsonPrintOriginal());
        mapOfPunch.put("adjustedtimestamp", punch.jsonPrintAdjusted());
        
        return mapOfPunch;
    }
    
    /**
     * Function for taking an arrayList of punches and converts to Json. Author: William Saint
     * @param dailyPunchList
     * @return JsonObject of punch lists.
     */
  
    public static String getPunchListAsJSON(ArrayList<Punch> dailyPunchList){
      
        JsonArray arrayOfPunches = new JsonArray();
        
        for(Punch punch: dailyPunchList){
            
            arrayOfPunches.add(convertPunchToJSONMap(punch));
            
        }

        return Jsoner.serialize(arrayOfPunches);
    }
   
    /**
     * Serializes an ArrayList of punch data spanding a pay period, as well as the time worked in minutes and absences.
     * @author William Saint
     * @param punchList
     * @param shift
     * @return serialized string.
     */
    
    public static String getPunchListPlusTotalsAsJSON(ArrayList<Punch> punchList, Shift shift){
        JsonObject object = new JsonObject();
        JsonArray arrayOfPunches = new JsonArray();
        
        int timeWorked = 0;
        BigDecimal absence = calculateAbsenteeism(punchList, shift);
        LocalDate date = punchList.get(0).getOriginaltimestamp().toLocalDate();
        
        ArrayList<Punch> punches = new ArrayList<>();
        
        for(int i = 0; i< punchList.size(); i++){
            Punch punch = punchList.get(i);
            arrayOfPunches.add(convertPunchToJSONMap(punch));
            if(!punch.getOriginaltimestamp().toLocalDate().equals(date)){
                timeWorked += calculateTotalMinutes(punches, shift);
                date = punch.getOriginaltimestamp().toLocalDate();
                punches = new ArrayList<>();
                punches.add(punch);
            }else{
                punches.add(punch);
            }
            if(i == punchList.size()-1){
               timeWorked += calculateTotalMinutes(punches, shift);
            }
            
        }
        
        
       
        object.put("absenteeism", String.valueOf(absence).concat("%"));
        object.put("totalminutes", timeWorked);
        object.put("punchlist", arrayOfPunches);
        
        return Jsoner.serialize(object);
    }
}