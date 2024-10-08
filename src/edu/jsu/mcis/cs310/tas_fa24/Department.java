package edu.jsu.mcis.cs310.tas_fa24;

/**
 *
 * @author joshwhaley
 * 
 * This is the constructor class for the department. Inside the fields of the 
 * department's identification number, the description or the department name,
 * the terminal id assigned to that department are created and assigned as well
 * as their coinciding getter methods. Also includes the proper toString() 
 * override method to properly composition the output format. 
  */
public class Department {
    
    // field creation
    private final int deptID;
    private final String description; 
    private final int terminalID; 
    
    // constructor
    public Department(int deptID, String description, int terminalID){
        this.deptID = deptID;
        this.description = description;
        this.terminalID = terminalID;
        
    }
    
    // Getters
    public int getDeptID(){
        return deptID;
    }
    
    public String getDescription(){
        return description;
    }
    
    public int getTerminalID(){
        return terminalID;
    }
    
    
    @Override
    public String toString(){
        
        StringBuilder s = new StringBuilder();
        s.append("#").append(deptID).append(" (");
        s.append(description).append("), Terminal ID: ").append(terminalID);
        return s.toString();
    
    }
    
}
