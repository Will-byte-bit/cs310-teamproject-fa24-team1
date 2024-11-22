package edu.jsu.mcis.cs310.tas_fa24.dao;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import edu.jsu.mcis.cs310.tas_fa24.EmployeeType;
import edu.jsu.mcis.cs310.tas_fa24.Shift;
import edu.jsu.mcis.cs310.tas_fa24.Badge;
import edu.jsu.mcis.cs310.tas_fa24.Punch;
import edu.jsu.mcis.cs310.tas_fa24.DailySchedule;
import static edu.jsu.mcis.cs310.tas_fa24.dao.DAOUtility.getWeeklyWorkedMinutes;
import edu.jsu.mcis.cs310.tas_fa24.dao.ShiftDAO;
import edu.jsu.mcis.cs310.tas_fa24.dao.PunchDAO;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.TemporalAdjusters;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

/**
 * ReportDAO is an object access class that generates a list of Json objects of an employee's badge, full name, department, and type.
 * It uses a method to take these objects and return them as a Json array.
 * Author: samca
 */
public class ReportDAO {
    private final DAOFactory daoFactory;
    public ReportDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /**
     * Retrieves a badge summary report for employees in JSON format.
     * If departmentId is specified, it filters by that department; otherwise, includes all.
     *
     * @param departmentId The department ID to filter by, or null for all departments.
     * @return JsonArray containing the badge summary data.
     */
    public String getBadgeSummary(Integer departmentId) {
        List<JsonObject> badgeSummaryList = new ArrayList<>();
        ResultSet rs = null;
        PreparedStatement ps = null;
        try {
            Connection conn = daoFactory.getConnection();
            String query;
            // Set up query based on presence of departmentId
            if (departmentId == null) {
                query = "SELECT e.badgeid, e.firstname, e.middlename, e.lastname, d.description AS department, e.employeetypeid " +
                        "FROM employee e " +
                        "JOIN department d ON e.departmentid = d.id " +
                        "ORDER BY e.lastname, e.firstname, e.middlename";
                ps = conn.prepareStatement(query);
            } else {
                query = "SELECT e.badgeid, e.firstname, e.middlename, e.lastname, d.description AS department, e.employeetypeid " +
                        "FROM employee e " +
                        "JOIN department d ON e.departmentid = d.id " +
                        "WHERE e.departmentid = ? " +
                        "ORDER BY e.lastname, e.firstname, e.middlename";
                ps = conn.prepareStatement(query);
                ps.setInt(1, departmentId);
            }
            rs = ps.executeQuery();
            // Process each result
            while (rs.next()) {
                String badgeId = rs.getString("badgeid");
                String firstName = rs.getString("firstname");
                String middleName = rs.getString("middlename");
                String lastName = rs.getString("lastname");
                String department = rs.getString("department");
                int employeeTypeId = rs.getInt("employeetypeid");
                // Map employee type to expected values
                String employeeType;
                if (employeeTypeId == 1) {
                    employeeType = "Full-Time Employee";
                } else {
                    employeeType = "Temporary Employee";
                }
                // Create full name
                String fullName = lastName + ", " + firstName + (middleName != null ? " " + middleName : "");
                // Create JSON object for each employee record
                JsonObject jsonObject = new JsonObject();
                jsonObject.put("badgeid", badgeId);
                jsonObject.put("name", fullName);
                jsonObject.put("department", department);
                jsonObject.put("type", employeeType);
                badgeSummaryList.add(jsonObject);
            }
        } catch (SQLException e) {
            throw new DAOException("Error retrieving badge summary: " + e.getMessage());
        } finally {
        try { if (rs != null) rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); }
        try { if (ps != null) ps.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); }
    }
    // Convert to JSON array and return pretty-printed JSON string
        JsonArray jsonArray = new JsonArray();
        jsonArray.addAll(badgeSummaryList);
        return Jsoner.prettyPrint(jsonArray.toJson());
    }

    /**
     * Retrieves an employee summary report in JSON format.
     * If departmentId is specified, it filters by that department; otherwise, includes all.
     *
     * @param departmentId The department ID to filter by, or null for all departments.
     * @return JSON-formatted string containing the employee summary data.
     */
    public String getEmployeeSummary(Integer departmentId) {
    List<JsonObject> employeeSummaryList = new ArrayList<>();
    ResultSet rs = null;
    PreparedStatement ps = null;
    
    SimpleDateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
    
    try {
        Connection conn = daoFactory.getConnection();
        String query;

        if (departmentId == null) {
            query = "SELECT e.badgeid, e.firstname, e.middlename, e.lastname, d.description AS department, " +
                    "t.description AS employeetype, s.description AS shift, e.active " +
                    "FROM employee e " +
                    "JOIN department d ON e.departmentid = d.id " +
                    "JOIN employeetype t ON e.employeetypeid = t.id " +
                    "JOIN shift s ON e.shiftid = s.id " +
                    "ORDER BY d.description, e.firstname, e.lastname, e.middlename";
            ps = conn.prepareStatement(query);
        } else {
            query = "SELECT e.badgeid, e.firstname, e.middlename, e.lastname, d.description AS department, " +
                    "t.description AS employeetype, s.description AS shift, e.active " +
                    "FROM employee e " +
                    "JOIN department d ON e.departmentid = d.id " +
                    "JOIN employeetype t ON e.employeetypeid = t.id " +
                    "JOIN shift s ON e.shiftid = s.id " +
                    "WHERE e.departmentid = ? " +
                    "ORDER BY d.description, e.firstname, e.lastname, e.middlename";
            ps = conn.prepareStatement(query);
            ps.setInt(1, departmentId);
        }
        
        rs = ps.executeQuery();

        while (rs.next()) {
            String badgeId = rs.getString("badgeid");
            String firstName = rs.getString("firstname");
            String middleName = rs.getString("middlename");
            String lastName = rs.getString("lastname");
            String department = rs.getString("department");
            String employeeType = rs.getString("employeetype");
            String shift = rs.getString("shift");
            String activeDate = dateFormat.format(rs.getDate("active"));

            // Construct JSON object with expected keys for each employee
            JsonObject jsonObject = new JsonObject();
            jsonObject.put("badgeid", badgeId);
            jsonObject.put("firstname", firstName);
            jsonObject.put("middlename", middleName);
            jsonObject.put("lastname", lastName);
            jsonObject.put("department", department);
            jsonObject.put("employeetype", employeeType);
            jsonObject.put("shift", shift);
            jsonObject.put("active", activeDate);

            employeeSummaryList.add(jsonObject);
        }

    } catch (SQLException e) {
        throw new DAOException("Error retrieving employee summary: " + e.getMessage());
    } finally {
        try { if (rs != null) rs.close(); } catch (SQLException e) { System.err.println("Error closing ResultSet: " + e.getMessage()); }
        try { if (ps != null) ps.close(); } catch (SQLException e) { System.err.println("Error closing PreparedStatement: " + e.getMessage()); }
    }
    
    // Convert to JSON array and return pretty-printed JSON string
    JsonArray jsonArray = new JsonArray();
    jsonArray.addAll(employeeSummaryList);
    return Jsoner.prettyPrint(jsonArray.toJson());
    }
    
    /**
     * Retrieves who's clocked in for work and who's not based on a time and date, as well optionally a specific department.
     * @param time
     * @param departmentID
     * @Author William Saint
     * @return Serialized String
     */
    public String getWhosInWhosOut(LocalDateTime time, Integer departmentID){
        time.withNano(0);
        
        JsonArray result = new JsonArray();
       
        
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        DateTimeFormatter formatterForRecord = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        
        LocalDateTime beginningOfDay = LocalDateTime.of(time.toLocalDate(), LocalTime.of(0,0));

        Timestamp end = Timestamp.valueOf(time.format(formatter));
        Timestamp begin = Timestamp.valueOf(beginningOfDay.format(formatter));
        
        String query = "";
        ResultSet rs = null;
        PreparedStatement ps = null;
        try{
            Connection conn = daoFactory.getConnection();

            if(departmentID != null){
                query = "WITH new_table AS (\n" +
                        "    SELECT \n" +
                        "        employee.badgeid AS newbadgeid,\n" +
                        "        employee.employeetypeid,\n" +
                        "        employee.lastname,\n" +
                        "        employee.firstname, \n" +
                        "        employee.shiftid,\n" +
                        "        employee.departmentid,\n" +
                        "        event.eventtypeid,\n" +
                        "        event.timestamp\n" +
                        "    FROM employee\n" +
                        "    INNER JOIN event \n" +
                        "        ON employee.badgeid = event.badgeid\n" +
                        "    WHERE employee.departmentid = ?\n" +
                        "      AND event.eventtypeid != 2\n" +
                        "      AND event.timestamp >= ?\n" +
                        "      AND event.timestamp <= ?\n" +
                        ")\n" +
                        "select * from new_table\n" +
                        "UNION ALL\n" +
                        " SELECT \n" +
                        "    employee.badgeid,\n" +
                        "    employee.employeetypeid,\n" +
                        "    employee.lastname,\n" +
                        "    employee.firstname, \n" +
                        "    employee.shiftid,\n" +
                        "    employee.departmentid,\n" +
                        "    0 as timestamp, \n" +
                        "    0 as eventtypeid\n" +
                        "FROM employee\n" +
                        "WHERE employee.departmentid = ?\n" +
                        "  AND employee.badgeid NOT IN (\n" +
                        "        SELECT newbadgeid\n" +
                        "        FROM new_table\n" +
                        "    )\n" +
                        "ORDER BY eventtypeid desc, employeetypeid DESC, lastname, firstname;";
                        ps = conn.prepareStatement(query);
                        ps.setInt(1, departmentID);
                        ps.setTimestamp(2, begin);
                        ps.setTimestamp(3, end);
                        ps.setInt(4, departmentID);
                        
                        rs = ps.executeQuery();
                        StringBuilder sb = new StringBuilder();
                        while(rs.next()){
                            JsonObject record = new JsonObject();
                            ShiftDAO shiftDAO = new ShiftDAO(this.daoFactory);
                            Shift shift = shiftDAO.find(rs.getInt("shiftid"));
                            
                            if(rs.getInt("eventtypeid") == 1){
                                LocalDateTime arrived = rs.getTimestamp("timestamp").toLocalDateTime().withNano(0);
                                String day = arrived.getDayOfWeek().toString().substring(0, 3).concat(" ").concat(arrived.format(formatterForRecord));
                                record.put("arrived", day);
                            }
                           
                            if(rs.getInt("employeetypeid") == 1){
                                record.put("employeetype", "Full-Time Employee");
                            }
                            else if (rs.getInt("employeetypeid") == 0){
                                record.put("employeetype", "Temporary Employee");
                            }
                            record.put("firstname", rs.getString("firstname"));
                            record.put("badgeid", rs.getString("newbadgeid"));
                            record.put("shift", shift.getDescription());
                            record.put("lastname", rs.getString("lastname"));
                            if(rs.getInt("eventtypeid") == 1 || rs.getInt("eventtypeid") == 2){
                                record.put("status", "In");
                               
                            }
                            else if(rs.getInt("eventtypeid") == 0){
                                record.put("status", "Out");
                            }
                            result.add(record);
                        }
                        
            }else{
                query = "WITH new_table AS (\n" +
                        "    SELECT \n" +
                        "        employee.badgeid AS newbadgeid,\n" +
                        "        employee.employeetypeid,\n" +
                        "        employee.lastname,\n" +
                        "        employee.firstname, \n" +
                        "        employee.shiftid,\n" +
                        "        employee.departmentid,\n" +
                        "        event.eventtypeid,\n" +
                        "        event.timestamp\n" +
                        "    FROM employee\n" +
                        "    INNER JOIN event \n" +
                        "        ON employee.badgeid = event.badgeid\n" +
                        "      AND event.eventtypeid != 2\n" +
                        "      AND event.timestamp >= ?\n" +
                        "      AND event.timestamp <= ?\n" +
                        ")\n" +
                        "select * from new_table\n" +
                        "UNION ALL\n" +
                        "\n" +
                        "SELECT \n" +
                        "    employee.badgeid,\n" +
                        "    employee.employeetypeid,\n" +
                        "    employee.lastname,\n" +
                        "    employee.firstname, \n" +
                        "    employee.shiftid,\n" +
                        "    employee.departmentid,\n" +
                        "    0 as timestamp, \n" +
                        "    0 as eventtypeid\n" +
                        "FROM employee \n" +
                        "where employee.badgeid NOT IN (\n" +
                        "        SELECT newbadgeid\n" +
                        "        FROM new_table\n" +
                        "    )\n" +
                        "ORDER BY eventtypeid desc, employeetypeid DESC, lastname, firstname;";
                         ps = conn.prepareStatement(query);
             
                        ps.setTimestamp(1, begin);
                        ps.setTimestamp(2, end);
                        
                        rs = ps.executeQuery();
                        while(rs.next()){
                            
                            
                           
                            JsonObject record = new JsonObject();
                            ShiftDAO shiftDAO = new ShiftDAO(this.daoFactory);
                            Shift shift = shiftDAO.find(rs.getInt("shiftid"));
                             if(rs.getInt("eventtypeid") == 1){
                                LocalDateTime arrived = rs.getTimestamp("timestamp").toLocalDateTime().withNano(0);
                                String day = arrived.getDayOfWeek().toString().substring(0, 3).concat(" ").concat(arrived.format(formatterForRecord));
                                record.put("arrived", day);
                            }
                            if(rs.getInt("employeetypeid") == 1){
                                record.put("employeetype", "Full-Time Employee");
                            }
                            else if (rs.getInt("employeetypeid") == 0){
                                record.put("employeetype", "Temporary Employee");
                            }
                            record.put("firstname", rs.getString("firstname"));
                            record.put("badgeid", rs.getString("newbadgeid"));
                            record.put("shift", shift.getDescription());
                            record.put("lastname", rs.getString("lastname"));
                            
                            if(rs.getInt("eventtypeid") == 1){
                                record.put("status", "In");
                            }
                            else if (rs.getInt("eventtypeid") == 0){
                                
                                record.put("status", "Out");
                            }
                            result.add(record);
                        }

            }
            
        }catch (SQLException e) {
            throw new DAOException("Error retrieving whos in whos out: " + e.getMessage());
        }
       
        return Jsoner.serialize(result);
    }
    
    
    
    
    /**
     * Retrieves list of employees who worked during a specific pay period, 
     * and the number of regular and overtime hours worked, in JSON Format.
     * 
     * @param date - day within desired pay period
     * @param departmentId - dept to filter by or null for all 
     * @param employeeType - full time or part time
     * 
     * @author Josh Whaley
     * 
     */

    public String getHoursSummary(LocalDate date, Integer departmentId, EmployeeType employeeType) {
        JsonArray reportArray = new JsonArray();
        PunchDAO punchDAO = daoFactory.getPunchDAO();
        ShiftDAO shiftDAO = daoFactory.getShiftDAO();

        String sql = """
            SELECT e.firstname, e.middlename, e.lastname, d.description AS DEPT_NAME,
               et.description AS EMPLOYEE_TYPE, s.description AS SHIFT_NAME, e.badgeid
            FROM employee e
            JOIN department d ON e.departmentid = d.ID
            JOIN employeetype et ON e.employeetypeid = et.ID
            JOIN shift s ON e.shiftid = s.ID
            JOIN dailyschedule ds ON e.shiftid = ds.ID
            WHERE (? IS NULL OR e.departmentid = ?)
            AND (? IS NULL OR et.description = ?)
            ORDER BY e.lastname, e.firstname, e.middlename;
            """;

        try (Connection conn = daoFactory.getConnection();
            PreparedStatement ps = conn.prepareStatement(sql)) {

            // Set query parameters
            ps.setObject(1, departmentId);
            ps.setObject(2, departmentId);
            ps.setString(3, employeeType != null ? employeeType.toString() : null);
            ps.setString(4, employeeType != null ? employeeType.toString() : null);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String firstName = rs.getString("FIRSTNAME");
                    String middleName = rs.getString("MIDDLENAME");
                    String lastName = rs.getString("LASTNAME");
                    String department = rs.getString("DEPT_NAME");
                    String employeeTypeDesc = rs.getString("EMPLOYEE_TYPE");
                    String shiftName = rs.getString("SHIFT_NAME");
                    String badgeId = rs.getString("BADGEID");

                    // Construct full name
                    String middleInitial = (middleName != null && !middleName.isEmpty()) ? middleName.substring(0, 1) : "";
                    String name = String.format("%s, %s %s", lastName, firstName, middleInitial);

                    // Retrieve employee badge and shift
                    Badge badge = new Badge(badgeId, "");
                    LocalDate payPeriodStart = date.with(DayOfWeek.MONDAY);
                    LocalDate payPeriodEnd = date.with(DayOfWeek.SUNDAY);

                    ArrayList<Punch> punches = punchDAO.list(badge, payPeriodStart, payPeriodEnd);
                    Shift shift = shiftDAO.find(badge, payPeriodStart);
                    
                    System.out.println(badge);
                    DateTimeFormatter formatterForFinal = DateTimeFormatter.ofPattern("HH:mm:ss");
                    System.out.println(shift.getDefaultSchedule(DayOfWeek.FRIDAY).getShiftStart().format(formatterForFinal));
                    System.out.println(shift.getDefaultSchedule(DayOfWeek.FRIDAY).getShiftEnd().format(formatterForFinal));
                    
                    double weeklyRegularHours = 0.0;
                    double weeklyOvertimeHours = 0.0;
                    double weeklyScheduledHours = 0.0;
                    
                    double scheduledMinutes = 0.00;
                        
                        for(int day = 1; day <= 5; day++){
                            scheduledMinutes += shift.getDefaultSchedule(DayOfWeek.of(day)).getShiftDuration() - shift.getDefaultSchedule(DayOfWeek.of(day)).getLunchDuration();
                            scheduledMinutes = (long)(scheduledMinutes / 60.0);
                            weeklyScheduledHours += scheduledMinutes;
                        }
                        

                    for (LocalDate currentDay = payPeriodStart; !currentDay.isAfter(payPeriodEnd); currentDay = currentDay.plusDays(1)) {

                        ArrayList<Punch> dailyPunches;
                        dailyPunches = punchDAO.list(badge, currentDay);

                        // Calculate total worked minutes for the day
                        int dailyMinutes = DAOUtility.calculateTotalMinutes(dailyPunches, shift);

                        // Calculate daily regular hours
                        double dailyRegularHours = dailyMinutes / 60.0;

                        // Add daily hours to weekly totals
                        weeklyRegularHours += dailyRegularHours;
   
                    }

                    
                    // Cap weekly regular hours at 40 and allocate the excess to overtime
                    if (weeklyRegularHours > weeklyScheduledHours) {
                        double excessHours = weeklyRegularHours - weeklyScheduledHours;
                        weeklyRegularHours = weeklyScheduledHours;
                        weeklyOvertimeHours += excessHours;
                    }
                    

                    // Skip employees with no hours worked (both regular and overtime)
                    if (weeklyRegularHours != 0 ) {

                    // Create JSON object for each employee
                    JsonObject record = new JsonObject();
                    record.put("name", name);
                    record.put("middlename", middleName);
                    record.put("lastname", lastName);
                    record.put("department", department);
                    record.put("employeetype", employeeTypeDesc);
                    record.put("shift", shiftName);
                    record.put("regular", String.format("%.2f", weeklyRegularHours));
                    record.put("overtime", String.format("%.2f", weeklyOvertimeHours));

                    reportArray.add(record);
                    
                    }
                }
            }
        } catch (SQLException e) {
            throw new DAOException("Error retrieving hours summary: " + e.getMessage());
        }

        return Jsoner.prettyPrint(reportArray.toJson());
    }
    
    /**
     * Method to return a Json object containing a Json list of an employee's lifetime absenteeism history.
     * @param employeeId
     * @return 
     */
    
    public String getAbsenteeismHistory(Integer employeeId) {
	JsonObject report = new JsonObject();
	JsonArray historyList = new JsonArray();

	ResultSet rsEmployee = null;
	ResultSet rsAbsenteeism = null;
	PreparedStatement psEmployee = null;
	PreparedStatement psAbsenteeism = null;

	try {
	    Connection conn = daoFactory.getConnection();

	    // Query to get employee details
	    String queryEmployee = """
		SELECT e.firstname, e.middlename, e.lastname, e.badgeid, d.description AS department
		FROM employee e
		JOIN department d ON e.departmentid = d.id
		WHERE e.id = ?
	    """;
	    psEmployee = conn.prepareStatement(queryEmployee);
	    psEmployee.setInt(1, employeeId);
	    rsEmployee = psEmployee.executeQuery();

	    if (rsEmployee.next()) {
		// Build the full name
		String fullname = String.format("%s, %s%s",
		    rsEmployee.getString("lastname"),
		    rsEmployee.getString("firstname"),
		    (rsEmployee.getString("middlename") != null) ? " " + rsEmployee.getString("middlename").substring(0, 1) + "." : "");

		report.put("name", fullname);
		report.put("badgeid", rsEmployee.getString("badgeid"));
		report.put("department", rsEmployee.getString("department"));
	    }

	    // Query to get absenteeism records
	    String queryAbsenteeism = """
		SELECT payperiod, percentage
		FROM absenteeism
		WHERE employeeid = ?
		ORDER BY payperiod DESC
		LIMIT 12
	    """;
	    psAbsenteeism = conn.prepareStatement(queryAbsenteeism);
	    psAbsenteeism.setInt(1, employeeId);
	    rsAbsenteeism = psAbsenteeism.executeQuery();

	    double cumulativeAbsenteeism = 0.0;
	    int recordCount = 0;

	    while (rsAbsenteeism.next()) {
		String payPeriod = rsAbsenteeism.getString("payperiod");
		double absenteeism = rsAbsenteeism.getDouble("percentage");

		// Update cumulative absenteeism and calculate lifetime average
		cumulativeAbsenteeism += absenteeism;
		recordCount++;
		double lifetimeAbsenteeism = cumulativeAbsenteeism / recordCount;

		// Create JSON object for the record
		JsonObject record = new JsonObject();
		record.put("payperiod", payPeriod);
		record.put("percentage", String.format("%.2f", absenteeism));
		record.put("lifetime", String.format("%.2f", lifetimeAbsenteeism));

		// Add to history
		historyList.add(record);
	    }

	    // Handle case where there are no absenteeism records
	    if (historyList.isEmpty()) {
		historyList.add(new JsonObject());
	    }

	    report.put("absenteeismhistory", historyList);

	} catch (SQLException e) {
	    throw new DAOException("Error retrieving absenteeism history " + e.getMessage());
	} finally {
	    try { if (rsEmployee != null) rsEmployee.close(); } catch (SQLException e) { System.err.println("Error closing Employee ResultSet: " + e.getMessage()); }
	    try { if (rsAbsenteeism != null) rsAbsenteeism.close(); } catch (SQLException e) { System.err.println("Error closing Absenteeism ResultSet: " + e.getMessage()); }
	    try { if (psEmployee != null) psEmployee.close(); } catch (SQLException e) { System.err.println("Error closing Employee PreparedStatement: " + e.getMessage()); }
	    try { if (psAbsenteeism != null) psAbsenteeism.close(); } catch (SQLException e) { System.err.println("Error closing Absenteeism PreparedStatement: " + e.getMessage()); }
	}


	return Jsoner.prettyPrint(report.toJson());
    }



}