/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.HashMap;

/**
 *
 * @author joshwhaley
 * refactored extensively by William Saint.
 * 
 * This class holds the daily schedules for an employee. More information about specific functions below.
 * 
 * calcTimeDifferenceShift() takes two strings, converts them into local time, calculates the difference, and returns an integer.
 * calcTimeDifferenceLunch() is the same as shift; I have them in separate functions for future proofing.
* 
 */
public class DailySchedule {
    

        private final DateTimeFormatter inTake = DateTimeFormatter.ofPattern("HH:mm:ss");
    	private LocalTime shiftStart = null;
   	private LocalTime shiftEnd = null;
    	private LocalTime lunchStart = null;
   	private LocalTime lunchEnd = null;
    
   	private final int roundingInterval;
    	private final int gracePeriod;
    	private final int dockPenalty;
        private final int lunchThreshold;
        
        private final int shiftDuration;
        private final int lunchDuration;
    
        private final int  DEFAULT = 0;
        
        // constructor (trying to switch to HashMap per Will's initial design)
        public DailySchedule(LocalTime shiftStart, LocalTime shiftEnd, LocalTime lunchStart, LocalTime lunchEnd, int roundingInterval, 
                            int gracePeriod, int dockPenalty, int lunchThreshold){

            this.shiftStart = shiftStart; 
            this.shiftEnd = shiftEnd; 
            this.lunchStart = lunchStart;
            this.lunchEnd = lunchEnd;
    
            this.roundingInterval = roundingInterval;
            this.gracePeriod = gracePeriod;
            this.dockPenalty = dockPenalty;
            this.lunchThreshold = lunchThreshold;
            this.lunchDuration = calcTimeDifferenceLunch();
            this.shiftDuration = calcTimeDifferenceShift();

        }
        
        
        public DailySchedule(HashMap<String, String> map){

        this.shiftStart = LocalTime.parse(map.get("shiftstart"), inTake);
        this.shiftEnd = LocalTime.parse(map.get("shiftstop"), inTake);
        this.lunchStart = LocalTime.parse(map.get("lunchstart"), inTake);
        this.lunchEnd = LocalTime.parse(map.get("lunchstop"), inTake);

        this.dockPenalty =  Integer.parseInt(map.get("dockpenalty"));
        this.gracePeriod = Integer.parseInt(map.get("graceperiod"));
        this.roundingInterval = Integer.parseInt(map.get("roundinterval"));
        this.lunchThreshold = Integer.parseInt(map.get("lunchthreshold"));
        this.lunchDuration = calcTimeDifferenceLunch();
        this.shiftDuration = calcTimeDifferenceShift();
  

        }
         
        
       public final int calcTimeDifferenceShift(){
        
        //duration of shift in minutes
        int duration = DEFAULT;
        
        //testing if shift end is greater than shift start
        if(shiftEnd.getMinute() > shiftStart.getMinute()){
        
         return (int) ChronoUnit.MINUTES.between(getShiftStart(), getShiftEnd());
       
        //testing if it is less than, meaning a different day.
        }else if(shiftEnd.getMinute() < shiftStart.getMinute()){
            
            LocalDateTime shiftStartLDT = LocalDateTime.of(2024,10,6,  shiftStart.getHour(), shiftStart.getMinute());
            LocalDateTime shiftEndLDT = LocalDateTime.of(2024,10,7, shiftEnd.getHour(), shiftEnd.getMinute());
            
            Duration difference = Duration.between(shiftStartLDT, shiftEndLDT);
            duration = (difference.toHoursPart() * 60) + difference.toMinutesPart();
            
            
        }
        
        
        return duration;
    }
     
    public final int calcTimeDifferenceLunch(){
        /*
        \Calcuates difference between times.
        */
        
        int duration = DEFAULT;
        
        if(lunchEnd.getMinute() > lunchStart.getMinute()){
         return (int) ChronoUnit.MINUTES.between(getLunchStart(), getLunchEnd());
        
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


        
    // getters
    public LocalTime getShiftStart(){ 
        return shiftStart;
    }
    
    public LocalTime getShiftEnd(){ 
        return shiftEnd; 
    }
    
    public LocalTime getLunchStart(){ 
        return lunchStart;
    }
    
    public LocalTime getLunchEnd(){ 
        return lunchEnd;
    }
    
    public int getRoundingInterval() { 
        return roundingInterval;
    }
    
    public int getGracePeriod() { 
        return gracePeriod;
    }
    
    public int getDockPenalty(){ 
        return dockPenalty;
    }
    public int getLunchThreshold(){
        return lunchThreshold;
    }
    public int getLunchDuration(){
        return lunchDuration;
    }
    public int getShiftDuration(){
        return shiftDuration;
    }
    public int getDailyScheduledMinutes(){
        return shiftDuration - lunchThreshold;
    }
}
