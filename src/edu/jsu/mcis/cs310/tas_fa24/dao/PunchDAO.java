package edu.jsu.mcis.cs310.tas_fa24.dao;

import edu.jsu.mcis.cs310.tas_fa24.Badge;
import edu.jsu.mcis.cs310.tas_fa24.Punch;
import edu.jsu.mcis.cs310.tas_fa24.EventType;
import java.sql.*;
import java.time.LocalDate;
import java.time.LocalDateTime; // Add this import
import java.time.format.DateTimeFormatter; // Add this import
import java.util.ArrayList;

/**
 * PunchDAO is a class that grabs punches from the database. 
 * Like shift DAO, creates a DAO factory object to be used.
 * The find method retrieves information from the database with the ID provided.
 * @author William Saint
 * 
 */
public class PunchDAO {
    private static final String QUERY_FIND_ID = "SELECT * FROM event WHERE id = ?";
    private static final String QUERY_FIND_BADGE = "SELECT description FROM badge WHERE id = ?";
    private static final String QUERY_LIST_BY_BADGE_DATE = 
        "SELECT * FROM event WHERE badgeid = ? AND DATE(timestamp) = ? ORDER BY timestamp";
    private static final String QUERY_INSERT = "INSERT INTO event (terminalid, badgeid, timestamp, eventtypeid) VALUES (?, ?, ?, ?)";
    private static final String QUERY_DEPARTMENT = "SELECT terminalid FROM department WHERE id = ?";
    private static final String QUERY_EMPLOYEE = "SELECT departmentid FROM employee WHERE badgeid = ?";

    private final int DEFAULT_ID = 0;
    private final DAOFactory daoFactory;

    /**
     * Constructor for PunchDAO.
     * @param daoFactory - DAOFactory instance to be used for database connections
     */
    public PunchDAO(DAOFactory daoFactory) {
        this.daoFactory = daoFactory;
    }

    /**
     * Find a punch using its unique ID from the database.
     * @param id - Punch ID
     * @return Punch object corresponding to the ID
     */
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

                    while (rs.next()) {
                        //getting info for badge and making badge
                        PreparedStatement psForBadge = conn.prepareStatement(QUERY_FIND_BADGE);
                        String badgeId = rs.getString("badgeid");
                        psForBadge.setString(1, badgeId);

                        if (psForBadge.execute()) {
                            ResultSet rsForBadge = psForBadge.getResultSet();
                            rsForBadge.next();

                            int typeId = rs.getInt("eventtypeid");

                            //setting event type based on type id
                            EventType et = null;
                            switch (typeId) {
                                case 0 -> et = EventType.CLOCK_OUT;
                                case 1 -> et = EventType.CLOCK_IN;
                                case 2 -> et = EventType.TIME_OUT;
                            }

                            //creating badge, creating punch
                            Badge badge = new Badge(badgeId, rsForBadge.getString("description"));

                            //formatting timestamp and removing unused data.
                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
                            LocalDateTime dateTime = LocalDateTime.parse(rs.getTimestamp("timestamp").toString().substring(0, 19), formatter);

                            //creating punch
                            punch = new Punch(id, rs.getInt("terminalid"), badge, dateTime, et);
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

    /**
     * Create a new Punch record in the database.
     * @param punch - The Punch object to be inserted
     * @return int - The newly created Punch ID
     */
    public int create(Punch punch) {
        int punchID = DEFAULT_ID;  // Default ID if insertion fails

        PreparedStatement psInsert = null;
        PreparedStatement psEmployee = null;
        PreparedStatement psDepartment = null;
        ResultSet rs = null;

        try {
            Connection conn = daoFactory.getConnection();

            if (conn.isValid(0)) {

                // If terminal ID is zero, bypass authorization (admin entry)
                if (punch.getTerminalid() == 0) {
                    punchID = insertPunch(conn, punch);
                } 
                else {
                    // Step 1: Find the employee's department terminal ID
                    psEmployee = conn.prepareStatement(QUERY_EMPLOYEE);
                    psEmployee.setString(1, punch.getBadge().getId());
                    rs = psEmployee.executeQuery();

                    if (rs.next()) {
                        int departmentID = rs.getInt("departmentid");

                        // Step 2: Find the department's terminal ID
                        psDepartment = conn.prepareStatement(QUERY_DEPARTMENT);
                        psDepartment.setInt(1, departmentID);
                        rs = psDepartment.executeQuery();

                        if (rs.next()) {
                            int departmentTerminalID = rs.getInt("terminalid");

                            // Step 3: Check if the punch terminal ID matches the department terminal ID
                            if (departmentTerminalID == punch.getTerminalid()) {
                                punchID = insertPunch(conn, punch);  // Authorized punch, proceed to insert
                            }
                        }
                    }
                }
            }
        } 
        catch (SQLException e) {
            throw new DAOException(e.getMessage());
        } 
        finally {
            if (rs != null) {
                try {
                    rs.close();
                } 
                catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
            if (psInsert != null) {
                try {
                    psInsert.close();
                } 
                catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
            if (psEmployee != null) {
                try {
                    psEmployee.close();
                } 
                catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
            if (psDepartment != null) {
                try {
                    psDepartment.close();
                } 
                catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
        }
        return punchID;
    }

    // Helper method to insert punch into the database
    private int insertPunch(Connection conn, Punch punch) throws SQLException {
        PreparedStatement psInsert = null;
        ResultSet rs = null;
        int punchID = 0;

        try {
            psInsert = conn.prepareStatement(QUERY_INSERT, Statement.RETURN_GENERATED_KEYS);
            psInsert.setInt(1, punch.getTerminalid());
            psInsert.setString(2, punch.getBadge().getId());
            psInsert.setTimestamp(3, Timestamp.valueOf(punch.getOriginaltimestamp()));
            psInsert.setInt(4, punch.getPunchtype().ordinal());

            int affectedRows = psInsert.executeUpdate();

            // Retrieve generated punch ID
            if (affectedRows == 1) {
                rs = psInsert.getGeneratedKeys();
                if (rs.next()) {
                    punchID = rs.getInt(1);
                }
            }
        } 
        finally {
            if (rs != null) {
                try {
                    rs.close();
                } 
                catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
            if (psInsert != null) {
                try {
                    psInsert.close();
                } 
                catch (SQLException e) {
                    throw new DAOException(e.getMessage());
                }
            }
        }
        return punchID;
    }

    /**
     * Retrieves a list of punches for the given badge and date.
     * 
     * @param badge The employee's badge
     * @param date The date for which punches should be retrieved
     * @return A list of Punch objects
     */
    public ArrayList<Punch> list(Badge badge, LocalDate date) {
        ArrayList<Punch> punchList = new ArrayList<>();
        PreparedStatement ps = null;
        ResultSet rs = null;

        try {
            Connection conn = daoFactory.getConnection();

            if (conn.isValid(0)) {
                String query = "SELECT event.*, badge.description FROM event "
                             + "JOIN badge ON event.badgeid = badge.id "
                             + "WHERE event.badgeid = ? AND DATE(event.timestamp) = ? "
                             + "ORDER BY event.timestamp";

                ps = conn.prepareStatement(query);
                ps.setString(1, badge.getId());
                ps.setDate(2, Date.valueOf(date));

                rs = ps.executeQuery();

                while (rs.next()) {
                    int id = rs.getInt("id");
                    int terminalId = rs.getInt("terminalid");
                    String badgeId = rs.getString("badgeid");
                    LocalDateTime originalTimestamp = rs.getTimestamp("timestamp").toLocalDateTime();
                    int eventTypeId = rs.getInt("eventtypeid");

                    EventType eventType = null;
                    switch (eventTypeId) {
                        case 0 -> eventType = EventType.CLOCK_OUT;
                        case 1 -> eventType = EventType.CLOCK_IN;
                        case 2 -> eventType = EventType.TIME_OUT;
                    }

                    String description = rs.getString("description");
                    Badge punchBadge = new Badge(badgeId, description);
                    Punch punch = new Punch(id, terminalId, punchBadge, originalTimestamp, eventType);
                    punchList.add(punch);
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

        return punchList;
    }
}
