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
        int id = DEFAULT_ID;
        return id;
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
