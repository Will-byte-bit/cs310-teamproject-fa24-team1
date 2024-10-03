package edu.jsu.mcis.cs310.tas_fa24.dao;

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
    public static HashMap<Integer, String> resultSetToHashMap(ResultSet rs){

        //Hash map of the raw shift data, key is 0 through length of shift.
        HashMap<Integer, String> mapOfShift = new HashMap<Integer, String>();

        try{
            ResultSetMetaData rsMeta = rs.getMetaData();

            int numberOfCols = rsMeta.getColumnCount();
            while(rs.next()) {

                //iterate over cols
                for (int i=1; i<=numberOfCols; i++) {
                    String colName = rsMeta.getColumnName(i);
              
                    mapOfShift.put(i-1, rs.getString(colName));

                }

            }// end while
            }
            catch(SQLException e){
                throw new DAOException(e.getMessage());
            }
      
            return mapOfShift;
        }
}