package edu.jsu.mcis.cs310.tas_fa24.dao;

import com.github.cliftonlabs.json_simple.JsonArray;
import com.github.cliftonlabs.json_simple.JsonObject;
import com.github.cliftonlabs.json_simple.Jsoner;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

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
}