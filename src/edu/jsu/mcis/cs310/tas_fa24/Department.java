/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24;

/**
 *
 * @author joshwhaley
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
    
    public int getDeptID(){
        return deptID;
    }
    
    public String getDescription(){
        return description;
    }
    
    public int getTerminalID(){
        return terminalID;
    }
    
    public String toString(){
        return "#" + deptID + " (" + description + "), Terminal ID: " + terminalID;
    }
    
}
