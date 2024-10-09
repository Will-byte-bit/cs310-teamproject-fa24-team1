/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24;

import java.util.HashMap;
import java.time.*;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;

/**
 * The Shift class is an object that represents the shift data.
 * It contains a HashMap of keys and values.
 * The key is the index value of each data entry, i.e., key 0: ID, key 1: description, etc.
 * calcTimeDifferenceShift() takes two strings, converts them into local time, calculates the difference, and returns an integer.
 * calcTimeDifferenceLunch() is the same as shift; I have them in separate functions for future proofing.
 *
 * @author William Saint
 * 
 * 
 *
 */

//refactor code to all instance
public class Shift {
    
    //chronounit.mintues.between - local times
    private HashMap<String, String> shift = new HashMap<>();
    
    private DateTimeFormatter inTake = DateTimeFormatter.ofPattern("HH:mm:ss");
    //private DateTimeFormatter inTakeConvert = DateTimeFormatter.ofPattern("hh:mm:ss");
    private DateTimeFormatter outTake = DateTimeFormatter.ofPattern("HH:mm");
    
    private int shiftDuration, lunchDuration;
    
    private String shiftStartStr = null;
    private String shiftEndStr = null;
    private String lunchStartStr= null;
    private String lunchEndStr = null;
    
    private LocalTime shiftStart = null;
    private LocalTime shiftEnd = null;
    private LocalTime lunchStart = null;
    private LocalTime lunchEnd = null;
    
    private int roundingInterval;
    private int gracePeriod;
    private int dockPenalty;
    private int lunchThreshold;
    
    
    private final int  DEFAULT = 0;
    
    /**
     * Constructor for shift
     * @param shift, integer string.
     * 
     * 
     */
    public Shift(HashMap<String, String> shift){
        
        this.shift = shift;
        
        this.shiftStart = LocalTime.parse(shift.get("shiftstart"), inTake);
       // this.shiftStart = LocalTime.parse(shiftStart.format(inTakeConvert));
        
        this.shiftEnd = LocalTime.parse(shift.get("shiftstop"), inTake);
       // this.shiftEnd = LocalTime.parse(shiftEnd.format(inTakeConvert));
       // System.out.println("converting works");
        
        this.lunchStart = LocalTime.parse(shift.get("lunchstart"), inTake);
        this.lunchEnd = LocalTime.parse(shift.get("lunchstop"), inTake);
        
        this.dockPenalty =  Integer.parseInt(shift.get("dockpenalty"));
        this.gracePeriod = Integer.parseInt(shift.get("graceperiod"));
        this.roundingInterval = Integer.parseInt(shift.get("roundinterval"));
        this.lunchThreshold = Integer.parseInt(shift.get("lunchthreshold"));
        System.out.println("cant parse");
        
        
        this.shiftStartStr = shiftStart.format(outTake);
        this.shiftEndStr = shiftEnd.format(outTake);
        
        this.lunchStartStr = lunchStart.format(outTake);
        this.lunchEndStr = lunchEnd.format(outTake);
        
        this.shiftDuration = calcTimeDifferenceShift();
        this.lunchDuration = calcTimeDifferenceLunch(); 
        
        
        
    }
     
    public int calcTimeDifferenceShift(){
    
        int duration = DEFAULT;
        
        //testing if shift end is greater than shift start
        if(shiftEnd.getMinute() > shiftStart.getMinute()){
       
        
        Duration difference = Duration.between(shiftStart, shiftEnd);
      
        
         duration = (difference.toHoursPart() * 60) + difference.toMinutesPart();
        
        //testing if it is less than, meaning a different day.
        }else if(shiftEnd.getMinute() < shiftStart.getMinute()){
            
            LocalDateTime shiftStartLDT = LocalDateTime.of(2024,10,6, shiftStart.getHour(), shiftStart.getMinute());
            LocalDateTime shiftEndLDT = LocalDateTime.of(2024,10,7, shiftEnd.getHour(), shiftEnd.getMinute());
            
            Duration difference = Duration.between(shiftStartLDT, shiftEndLDT);
            duration = (difference.toHoursPart() * 60) + difference.toMinutesPart();
            
            
        }
        
        return duration;
    }
     
    public int calcTimeDifferenceLunch(){
        /*
        \Calcuates difference between times.
        */
        
        int duration = DEFAULT;
        
        if(lunchEnd.getMinute() > lunchStart.getMinute()){
        Duration difference = Duration.between(lunchStart, lunchEnd);
        
        duration = (difference.toHoursPart() * 60) + difference.toMinutesPart();
        
        }
        //testing if it is less than, meaning a different day.
        else if(lunchEnd.getMinute() < lunchStart.getMinute()){
            
            LocalDateTime shiftStartLDT = LocalDateTime.of(2024,10,6, lunchStart.getHour(), lunchStart.getMinute());
            LocalDateTime shiftEndLDT = LocalDateTime.of(2024,10,7, lunchEnd.getHour(), lunchEnd.getMinute());
            
            Duration difference = Duration.between(shiftStartLDT, shiftEndLDT);
            duration = (difference.toHoursPart() * 60) + difference.toMinutesPart();
            
            
        }
        
        return duration;
    }
 
    
    
    public String toString(){
        String result = null;
        
        StringBuilder sb = new StringBuilder();
   
        
        //appending through shift
        sb.append(shift.get("description")).append(": ").append(shiftStartStr).append(" - ").append(shiftEndStr).append(" (").append(String.valueOf(shiftDuration));
        sb.append(" minutes); ");
        
        //appending through lunch
        sb.append("Lunch: ").append(lunchStartStr).append(" - ").append(lunchEndStr);
        sb.append(" (").append(String.valueOf(lunchDuration)).append(" minutes)");
        
        result = sb.toString();
  
        return result;
    }
    
    //getters
    public HashMap<String, String> getShift() {
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
