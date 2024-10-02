package edu.jsu.mcis.cs310.tas_fa24;

import edu.jsu.mcis.cs310.tas_fa24.dao.*;
import org.junit.*;
import static org.junit.Assert.*;

public class BadgeFindTest {

    private DAOFactory daoFactory;

    @Before
    public void setup() {

        daoFactory = new DAOFactory("tas.jdbc");

    }

    @Test
    public void testFindBadge1() {
        
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();

        /* Retrieve Badges from Database */

        Badge b1 = badgeDAO.find("12565C60");

        /* Compare to Expected Values */
        
        assertEquals("#12565C60 (Chapman, Joshua E)", b1.toString());

    }

    @Test
    public void testFindBadge2() {
        
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();

        /* Retrieve Badges from Database */

        Badge b2 = badgeDAO.find("08D01475");

        /* Compare to Expected Values */
        
        assertEquals("#08D01475 (Littell, Amie D)", b2.toString());

    }
    
    @Test
    public void testFindBadge3() {
        
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();

        /* Retrieve Badges from Database */

        Badge b3 = badgeDAO.find("D2CC71D4");

        /* Compare to Expected Values */
        
        assertEquals("#D2CC71D4 (Lawson, Matthew J)", b3.toString());

    }
    
    @Test
    public void testFindBadge4() {
	    BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
	    
	    /* Retrieve Badges from Database */
	    
	    Badge b4 = badgeDAO.find("124A2DED");
	    
	    // Compare to Expected Values
	    
	    assertEquals("#124A2DED (Ford, Nicholas R)", b4.toString());
    }
    
    @Test public void testFindBadge5() {
	    BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
	    
	    // Retrieve Badges from Database
	    
	    Badge b5 = badgeDAO.find("DFD9BB5C");
	    
	    // Compare to Expected Values
	    
	    assertEquals("#DFD9BB5C (Gallegos, Phillip M)", b5.toString());
    }
}
