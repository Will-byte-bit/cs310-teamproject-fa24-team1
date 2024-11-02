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
    
    
    	private LocalTime shiftStart = null;
   	private LocalTime shiftEnd = null;
    	private LocalTime lunchStart = null;
   	private LocalTime lunchEnd = null;
    
   	private int roundingInterval;
    	private int gracePeriod;
    	private int dockPenalty;
        private int lunchThreshold;
        
        // constructor (trying to switch to HashMap per Will's initial design)
        public DailySchedule(LocalTime shiftStart, LocalTime shiftEnd, 
                LocalTime lunchStart, LocalTime lunchEnd, int roundingInterverval, 
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

    
}
