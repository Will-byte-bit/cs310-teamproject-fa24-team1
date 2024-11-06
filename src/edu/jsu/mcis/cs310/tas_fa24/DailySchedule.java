/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24;

import java.time.LocalTime;

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
