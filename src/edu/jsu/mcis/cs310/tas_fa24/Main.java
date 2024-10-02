package edu.jsu.mcis.cs310.tas_fa24;

import edu.jsu.mcis.cs310.tas_fa24.dao.*;
import java.time.*;
import java.util.*;
import java.time.temporal.ChronoUnit;
import java.time.format.DateTimeFormatter;

public class Main {

    public static void main(String[] args) {
        
	/**
	 * Comments to check repo connection and permissions
	 * Sam Cain - Check (testing netbeans push/pull)
	 * Will Saint - Check testing 3
	 * Mathew Sjostrom - This is a test
	 * Josh Whaley - Testing push/pull
	 * Madison Latham - Testing push/pull(2)
	 */
	
	    
	    
	    
        // test database connectivity; get DAOs

        DAOFactory daoFactory = new DAOFactory("tas.jdbc");
        BadgeDAO badgeDAO = daoFactory.getBadgeDAO();
        
        // find badge

        Badge b = badgeDAO.find("C4F37EFF");
        
        // output should be "Test Badge: #C4F37EFF (Welch, Travis C)"
        
        System.err.println("Test Badge: " + b.toString());

    }

}
