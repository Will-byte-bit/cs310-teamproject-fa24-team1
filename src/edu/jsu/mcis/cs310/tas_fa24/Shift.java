/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24;

import java.time.temporal.ChronoUnit;
import java.util.HashMap;
import java.time.*;

/**
 *
 * @author Will
 * 
 * Shift class, map matches layout of table. Key is the index value of each data entry. I.E., key 0 ID, key 1 descript, etc.
 * calcTimeDifferenceShift takes two strings, converts them into local time, calculates the difference, and returns an integer.
 * calcTimeDifference Lunch is the same as shift; I have them in separate functions for future proofing.
 */
public class Shift {
    private HashMap<Integer, String> shift = new HashMap<Integer, String>();
    private int shiftDuration, lunchDuration;
    private String shiftStartStr = null;
    private String shiftEndStr = null;
    private String lunchStartStr= null;
    private String lunchEndStr = null;
    
    private final int  DEFAULT = 0;
    
    public Shift(HashMap<Integer, String> shift){
        this.shift = shift;
        this.shiftDuration = calcTimeDifferenceShift(shift.get(2), shift.get(3));
        this.lunchDuration = calcTimeDifferenceLunch(shift.get(7), shift.get(8)); 
        
        
        
    }
    public int calcTimeDifferenceShift(String shiftStart, String shiftEnd){
        /*
        \Calcuates difference between times.
        */
        
        //duration of shift in minutes
        int duration = DEFAULT;
        
        
        //local time of shifts
        LocalTime shiftStartLT= LocalTime.parse(shiftStart);
        LocalTime shiftEndLT = LocalTime.parse(shiftEnd);
        
        //for to string
        shiftStartStr = shiftStartLT.toString();
        shiftEndStr = shiftEndLT.toString();
        
        //testing if shift end is greater than shift start
        if(shiftEndLT.getMinute() > shiftStartLT.getMinute()){
       
        
        Duration difference = Duration.between(shiftStartLT, shiftEndLT);
      
        
        duration = (difference.toHoursPart() * 60) + difference.toMinutesPart();
        
        //testing if it is less than, meaning a different day.
        }else if(shiftEndLT.getMinute() < shiftStartLT.getMinute()){
            
            LocalDateTime shiftStartLDT = LocalDateTime.of(2024,10,6, shiftStartLT.getHour(), shiftStartLT.getMinute());
            LocalDateTime shiftEndLDT = LocalDateTime.of(2024,10,7, shiftEndLT.getHour(), shiftEndLT.getMinute());
            
            Duration difference = Duration.between(shiftStartLDT, shiftEndLDT);
            duration = (difference.toHoursPart() * 60) + difference.toMinutesPart();
            
            
        }
        
        return duration;
    }
    public int calcTimeDifferenceLunch(String lunchStart, String lunchEnd){
        /*
        \Calcuates difference between times.
        */
        
        //duration of shift in minutes
        int duration = DEFAULT;
        
        LocalTime lunchStartLT= LocalTime.parse(lunchStart);
        LocalTime lunchEndLT = LocalTime.parse(lunchEnd);
        
        lunchStartStr = lunchStartLT.toString();
        lunchEndStr = lunchEndLT.toString();
        
        if(lunchEndLT.getMinute() > lunchStartLT.getMinute()){
        Duration difference = Duration.between(lunchStartLT, lunchEndLT);
        
        duration = (difference.toHoursPart() * 60) + difference.toMinutesPart();
        
        }
        //testing if it is less than, meaning a different day.
        else if(lunchEndLT.getMinute() < lunchStartLT.getMinute()){
            
            LocalDateTime shiftStartLDT = LocalDateTime.of(2024,10,6, lunchStartLT.getHour(), lunchStartLT.getMinute());
            LocalDateTime shiftEndLDT = LocalDateTime.of(2024,10,7, lunchEndLT.getHour(), lunchEndLT.getMinute());
            
            Duration difference = Duration.between(shiftStartLDT, shiftEndLDT);
            duration = (difference.toHoursPart() * 60) + difference.toMinutesPart();
            
            
        }
        
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
    
    //getters
    public HashMap<Integer, String> getShift() {
        return shift;
    }

    public int getShiftDuration() {
        return shiftDuration;
    }

    public int getLunchDuration() {
        return lunchDuration;
    }

    public String getShiftStartStr() {
        return shiftStartStr;
    }

    public String getShiftEndStr() {
        return shiftEndStr;
    }

    public String getLunchStartStr() {
        return lunchStartStr;
    }

    public String getLunchEndStr() {
        return lunchEndStr;
    }

}
