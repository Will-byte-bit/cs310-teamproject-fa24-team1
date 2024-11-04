/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24;

import java.time.DayOfWeek;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;


/**
 * 
 *The Punch class is an object that represents rows from the "event" table.
 *It contains constructors for new and existing punches.
 *It also contains getters for all variables as well as a printOriginal() and ToString().
 *The printOriginal function implements StringBuilder.
 *Added the punch adjust function. Takes a shift object and adjusts the punch according to the rules.
 * 
 * @author William Saint
 * 
 * 
 */
public class Punch {
    
    private final int CLOCK_IN = 1;
    private final int CLOCK_OUT =  0;
    private final int MINUTE_TO_SECOND = 60;
    private final int NEAREST = 8;
    
    private int terminalId;
    private int id;
    
    private String day;
     
    private DateTimeFormatter formatterForFinal = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
    private Badge badge = null;
    private EventType punchType = null;
    private LocalDateTime originalTimeStamp = null;
    private LocalDateTime changedTimeStamp = null;
    private PunchAdjustmentType adjustedTimeStamp = null;
   
    
    
    
   
    
    public Punch(int terminalId, Badge badge, EventType punchType){
        this.terminalId = terminalId;
        this.badge = badge;
        this.punchType = punchType;
        originalTimeStamp = LocalDateTime.now().withNano(0);
        this.day = originalTimeStamp.getDayOfWeek().toString().substring(0, 3);
    }
    public Punch(int id, int terminalid, Badge badge, LocalDateTime originalTimeStamp, EventType punchtype){
        this.id = id;
        this.terminalId = terminalid;
        this.badge = badge;
        this.punchType = punchtype;
        this.originalTimeStamp = originalTimeStamp.withNano(0);
        this.day = originalTimeStamp.getDayOfWeek().toString().substring(0, 3);
       
       
    }
  
    public void adjust(Shift shift){
        
        //note 11/1/24
        //added " && difference > roundingInterval" to none test on both
        LocalTime stamp = originalTimeStamp.toLocalTime();
      
        
        int roundingInterval = shift.getRoundingInterval()*MINUTE_TO_SECOND;
        int gracePeriod = shift.getGracePeriod()*MINUTE_TO_SECOND;
        int dockBy = shift.getDockPenalty();
        
        DayOfWeek dayOfWeek = originalTimeStamp.getDayOfWeek();
            
       
        if(punchType.ordinal() == CLOCK_IN){
            
           LocalTime shiftStart = shift.getShiftStart(), lunchEnd = shift.getLunchEnd(),  lunchStart = shift.getLunchStart();
             
           shiftStart.withSecond(0);
           shiftStart.withNano(0);
           LocalTime shiftStartForNone = shiftStart.truncatedTo(ChronoUnit.MINUTES);
           LocalTime stampForNone = stamp.truncatedTo(ChronoUnit.MINUTES);
           int difference = (int) Math.abs(ChronoUnit.SECONDS.between(shiftStart, stamp));
          
            
            if(dayOfWeek.ordinal() <= 4){
                
                //test if no changes need to be made
                if((int) Math.abs(ChronoUnit.MINUTES.between(stampForNone, shiftStartForNone)) % shift.getRoundingInterval() == 0 && difference > roundingInterval){
                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), stamp.getHour(), stamp.getMinute());
                    adjustedTimeStamp = PunchAdjustmentType.NONE;
                }
                //testing for lunchstart
                else if(stamp.isBefore(lunchEnd) && stamp.isAfter(lunchStart)){

                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), lunchEnd.getHour(), lunchEnd.getMinute());
                    adjustedTimeStamp = PunchAdjustmentType.LUNCH_STOP;
                }
                //test if within rounding interval
                else if(stamp.isBefore(shiftStart) && difference <= roundingInterval){
                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), shiftStart.getHour(), shiftStart.getMinute());
                    adjustedTimeStamp = PunchAdjustmentType.SHIFT_START;
                }//end test for before
                
                //test if within grace period
                else if(stamp.isAfter(shiftStart) && difference <= gracePeriod){
                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), shiftStart.getHour(), shiftStart.getMinute());
                    adjustedTimeStamp = PunchAdjustmentType.SHIFT_START;
                }//end test if in grace
                
                //test for dock
                else if(stamp.isAfter(shiftStart) && difference >= gracePeriod && (int) Math.abs(ChronoUnit.SECONDS.between(stamp, shiftStart)) <= dockBy*60){

                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), shiftStart.getHour(), dockBy);
                    adjustedTimeStamp = PunchAdjustmentType.SHIFT_DOCK;
                }// end test after grace/dock
                
                //end test for lunch
                else if(difference > roundingInterval){
                    stamp = roundByInterval(stamp, roundingInterval/MINUTE_TO_SECOND);
                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), stamp.getHour(), stamp.getMinute());
                    adjustedTimeStamp = PunchAdjustmentType.INTERVAL_ROUND;
                }//end rounding outside inital 15
                
             //weekend clock 
            }else{
                
                 if(stamp.isBefore(shiftStart) && difference <= roundingInterval){
                    stamp = roundByInterval(stamp, roundingInterval/MINUTE_TO_SECOND);
                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), shiftStart.getHour(), stamp.getMinute());
                    adjustedTimeStamp = PunchAdjustmentType.INTERVAL_ROUND;
                    
                 }
                 else if(stamp.isBefore(shiftStart) && difference > roundingInterval){
                     stamp = roundByInterval(stamp, roundingInterval/MINUTE_TO_SECOND);
                     
                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), stamp.getHour(), stamp.getMinute());
                    adjustedTimeStamp = PunchAdjustmentType.INTERVAL_ROUND;
                }//end rounding outside inital 15
            }
        }//end clock in
        else if(punchType.ordinal() == CLOCK_OUT){
         
            LocalTime shiftEnd = shift.getShiftEnd(), lunchEnd = shift.getLunchEnd(), lunchStart = shift.getLunchStart();
            
            int difference = (int) Math.abs(ChronoUnit.SECONDS.between(stamp, shiftEnd));
            LocalTime shiftEndForNone = shiftEnd.truncatedTo(ChronoUnit.MINUTES);
            LocalTime stampForNone = stamp.truncatedTo(ChronoUnit.MINUTES);
               
            if(dayOfWeek.ordinal() <= 4){
               
                if((int) Math.abs(ChronoUnit.MINUTES.between(stampForNone, shiftEndForNone)) % shift.getRoundingInterval() == 0 && difference > roundingInterval){
                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), stamp.getHour(), stamp.getMinute());
                    adjustedTimeStamp = PunchAdjustmentType.NONE;
                }
                else if(stamp.isAfter(lunchStart) && stamp.isBefore(lunchEnd)){

                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), lunchStart.getHour(), lunchStart.getMinute());
                    adjustedTimeStamp = PunchAdjustmentType.LUNCH_START;
                }
                else if(stamp.isAfter(shiftEnd) && difference <= gracePeriod){
                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), shiftEnd.getHour(), shiftEnd.getMinute());
                    adjustedTimeStamp = PunchAdjustmentType.SHIFT_STOP;
                }//end test if in grace
               
                else if(stamp.isBefore(shiftEnd) && difference >= gracePeriod && (int) Math.abs(ChronoUnit.SECONDS.between(stampForNone, shiftEndForNone)) <= dockBy*60){
                    
                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), shiftEnd.getHour(), dockBy);
                    adjustedTimeStamp = PunchAdjustmentType.SHIFT_DOCK;
                }// end test after grace/dock
                else if(difference <= roundingInterval){

                    //stamp = roundByInterval(stamp, roundingInterval/MINUTE_TO_SECOND);
                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), shiftEnd.getHour(), shiftEnd.getMinute());
                    adjustedTimeStamp = PunchAdjustmentType.SHIFT_STOP;

                }//end test for before
                //end test for lunch
                else if(difference > roundingInterval){
                    
                    stamp = roundByInterval(stamp, roundingInterval/MINUTE_TO_SECOND);
                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), stamp.getHour(), stamp.getMinute());
                    adjustedTimeStamp = PunchAdjustmentType.INTERVAL_ROUND;
                }//end rounding outside inital 15
                
            }// end test day of Week
            else{
                if(difference <= roundingInterval){
                    stamp = roundByInterval(stamp, roundingInterval/MINUTE_TO_SECOND);
                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), stamp.getHour(), stamp.getMinute());
                    adjustedTimeStamp = PunchAdjustmentType.INTERVAL_ROUND;
                 }
                else if(difference > roundingInterval){
                    stamp = roundByInterval(stamp, roundingInterval/MINUTE_TO_SECOND);
                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(),  stamp.getHour(), stamp.getMinute());
                    adjustedTimeStamp = PunchAdjustmentType.INTERVAL_ROUND;
                }//end rounding outside inital 15
               
            }
        }
        
    }
    public LocalTime roundByInterval(LocalTime stamp, int roundBy){
     
       
        int minute = stamp.getMinute();
        
        int adjustMinute;
        
        if(minute%roundBy != 0){

        if ((minute%roundBy) < roundBy/2){
            
            adjustMinute = (Math.round(minute/roundBy) * roundBy);
        }
        else{
           
            adjustMinute = (Math.round(minute/roundBy) * roundBy)+ roundBy;
        }
            stamp = stamp.plusMinutes(adjustMinute-minute);
            stamp.withSecond(0).withNano(0);
        }
        else{
                stamp.withSecond(0).withNano(0);
        }
        

        return stamp; 
    }
    public String printAdjusted(){
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("#").append(badge.getId()).append(" ").append(punchType).append(": ").append(day).append(" ").append(changedTimeStamp.format(formatterForFinal)).append(" (").append(adjustedTimeStamp.toString()).append(")");
        
        return sb.toString();
    }
    public String printOriginal(){
        
        StringBuilder sb = new StringBuilder();
        
        sb.append("#").append(badge.getId()).append(" ").append(punchType).append(": ").append(day).append(" ").append(originalTimeStamp.format(formatterForFinal));

        return sb.toString();
    }
    public String jsonPrintOriginal(){
        StringBuilder sb = new StringBuilder();
        
        sb.append(day).append(" ").append(originalTimeStamp.format(formatterForFinal));
        return sb.toString();
                
    }
    public String jsonPrintAdjusted(){
        
        StringBuilder sb = new StringBuilder();
        
        sb.append(day).append(" ").append(changedTimeStamp.format(formatterForFinal));
        
        return sb.toString();
    }
    public String ToString(){
        return printOriginal();
    }
    public int getTerminalid() {
        return terminalId;
    }

    public int getId() {
        return id;
    }

    public Badge getBadge() {
        return badge;
    }

    public EventType getPunchtype() {
        return punchType;
    }

    public LocalDateTime getOriginaltimestamp() {
        return originalTimeStamp;
    }
    public LocalDateTime getChangetimestamp() {
        return changedTimeStamp;
    }

    public PunchAdjustmentType getAdjustedtimestamp() {
        return adjustedTimeStamp;
    }

}
