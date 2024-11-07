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
 *
 * @author joshwhaley
 */
public class DailySchedule {
    

        private DateTimeFormatter inTake = DateTimeFormatter.ofPattern("HH:mm:ss");
        //private DateTimeFormatter inTakeConvert = DateTimeFormatter.ofPattern("hh:mm:ss");
        private DateTimeFormatter outTake = DateTimeFormatter.ofPattern("HH:mm");

    	private LocalTime shiftStart = null;
   	private LocalTime shiftEnd = null;
    	private LocalTime lunchStart = null;
   	private LocalTime lunchEnd = null;
    
   	private final int roundingInterval;
    	private final int gracePeriod;
    	private final int dockPenalty;
        private final int lunchThreshold;
        
        private int shiftDuration;
        private int lunchDuration;
    
        private final int  DEFAULT = 0;
        
        // constructor (trying to switch to HashMap per Will's initial design)
        public DailySchedule(LocalTime shiftStart, LocalTime shiftEnd, 
                LocalTime lunchStart, LocalTime lunchEnd, int roundingInterval, 
                int gracePeriod, int dockPenalty, int lunchThreshold){

            this.shiftStart = shiftStart; 
            this.shiftEnd = shiftEnd; 
            this.lunchStart = lunchStart;
            this.lunchEnd = null;
    
            this.roundingInterval = roundingInterval;
            this.gracePeriod = gracePeriod;
            this.dockPenalty = dockPenalty;
            this.lunchThreshold = lunchThreshold;

        }
        
        
        public DailySchedule(HashMap<String, String> map){

        this.shiftStart = LocalTime.parse(map.get("shiftstart"), inTake);
             System.out.println(shiftStart.format(inTake));


        this.shiftEnd = LocalTime.parse(map.get("shiftstop"), inTake);


        this.lunchStart = LocalTime.parse(map.get("lunchstart"), inTake);
        this.lunchEnd = LocalTime.parse(map.get("lunchstop"), inTake);

        this.dockPenalty =  Integer.parseInt(map.get("dockpenalty"));
        this.gracePeriod = Integer.parseInt(map.get("graceperiod"));
        this.roundingInterval = Integer.parseInt(map.get("roundinterval"));
        this.lunchThreshold = Integer.parseInt(map.get("lunchthreshold"));


        }
         
        
         public int calcTimeDifferenceShift(LocalTime ShiftStart, LocalTime ShiftEnd){
        
        //duration of shift in minutes
        int duration = DEFAULT;
        
        //testing if shift end is greater than shift start
        if(getShiftEnd().getMinute() > getShiftStart().getMinute()){
        
         return (int) ChronoUnit.MINUTES.between(getShiftStart(), getShiftEnd());
       
        //testing if it is less than, meaning a different day.
        }else if(getShiftEnd().getMinute() < getShiftStart().getMinute()){
            
            LocalDateTime shiftStartLDT = LocalDateTime.of(2024,10,6, getShiftStart().getHour(), getShiftStart().getMinute());
            LocalDateTime shiftEndLDT = LocalDateTime.of(2024,10,7, getShiftEnd().getHour(), getShiftEnd().getMinute());
            
            Duration difference = Duration.between(shiftStartLDT, shiftEndLDT);
            duration = (difference.toHoursPart() * 60) + difference.toMinutesPart();
            
            
        }
        
        
        return duration;
    }
     
    public int calcTimeDifferenceLunch(LocalTime lunchStart, LocalTime lunchEnd){
        /*
        \Calcuates difference between times.
        */
        
        int duration = DEFAULT;
        
        if(getLunchEnd().getMinute() > getLunchStart().getMinute()){
         return (int) ChronoUnit.MINUTES.between(getLunchStart(), getLunchEnd());
        
        }
        //testing if it is less than, meaning a different day.
        else if(getLunchEnd().getMinute() < getLunchStart().getMinute()){
            
            LocalDateTime shiftStartLDT = LocalDateTime.of(2024,10,6, getLunchStart().getHour(), getLunchStart().getMinute());
            LocalDateTime shiftEndLDT = LocalDateTime.of(2024,10,7, getLunchEnd().getHour(), getLunchEnd().getMinute());
            
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
    
}
