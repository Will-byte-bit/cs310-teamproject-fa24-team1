/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24;


import java.util.HashMap;
import java.time.*;

/**
 *
 * @author Will
 * 
 * Shift class, map matches layout of table. Key is index value of each data entry. I.E key 0 ID, key 1 descript, etc.
 */
public class Shift {
    private HashMap<Integer, String> shift = new HashMap<Integer, String>();
    private int shiftDuration, lunchDuration;
    private String shiftStartStr = null;
    private String shiftEndStr = null;
    private String lunchStartStr= null;
    private String lunchEndStr = null;
    
    public Shift(HashMap<Integer, String> shift){
        this.shift = shift;
        this.shiftDuration = calcTimeDifferenceShift(shift.get(2), shift.get(3));
        this.lunchDuration = calcTimeDifferenceLunch(shift.get(7), shift.get(8)); 
        
        
        
    }
    public int calcTimeDifferenceShift(String shiftStart, String shiftEnd){
        /*
        \Calcuates difference between times.
        */
        
       
        LocalTime shiftStartLT= LocalTime.parse(shiftStart);
        LocalTime shiftEndLT = LocalTime.parse(shiftEnd);
        
        shiftStartStr = shiftStartLT.toString();
        shiftEndStr = shiftEndLT.toString();
        
        Duration difference = Duration.between(shiftStartLT, shiftEndLT);
        
        int duration = (difference.toHoursPart() * 60) + difference.toMinutesPart();
        
        
        return duration;
    }
    public int calcTimeDifferenceLunch(String lunchStart, String lunchEnd){
        /*
        \Calcuates difference between times.
        */
        
        
        LocalTime lunchStartLT= LocalTime.parse(lunchStart);
        LocalTime lunchEndLT = LocalTime.parse(lunchEnd);
        
        lunchStartStr = lunchStartLT.toString();
        lunchEndStr = lunchEndLT.toString();
        
        Duration difference = Duration.between(lunchStartLT, lunchEndLT);
        
        int duration = (difference.toHoursPart() * 60) + difference.toMinutesPart();
        
        
        return duration;
    }
 
    
    public String toString(){
        String result = null;
        
        StringBuilder sb = new StringBuilder();
   
        
        //appending through shift
        sb.append(shift.get(1)).append(": ").append(shiftStartStr).append(" - ").append(shiftEndStr).append(" (").append(String.valueOf(shiftDuration));
        sb.append(" minutes); ");
        
        //appending through lunch
        sb.append("Lunch: ").append(lunchStartStr).append(" - ").append(lunchEndStr);
        sb.append(" (").append(String.valueOf(lunchDuration)).append(" minutes)");
        
        result = sb.toString();
  
        return result;
    }
    
}
