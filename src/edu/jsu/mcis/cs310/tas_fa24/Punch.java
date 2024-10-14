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
        originalTimeStamp = LocalDateTime.now();
        this.day = originalTimeStamp.getDayOfWeek().toString().substring(0, 3);
    }
    public Punch(int id, int terminalid, Badge badge, LocalDateTime originalTimeStamp, EventType punchtype){
        this.id = id;
        this.terminalId = terminalid;
        this.badge = badge;
        this.punchType = punchtype;
        this.originalTimeStamp = originalTimeStamp;
        this.day = originalTimeStamp.getDayOfWeek().toString().substring(0, 3);
        
        
    }
    public void adjust(Shift shift){
        
       
        LocalTime stamp = originalTimeStamp.toLocalTime();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        
        int roundingInterval = shift.getRoundingInterval()*MINUTE_TO_SECOND;
        int gracePeriod = shift.getGracePeriod()*MINUTE_TO_SECOND;
        
        DayOfWeek dayOfWeek = originalTimeStamp.getDayOfWeek();
            
        if(punchType.ordinal() == CLOCK_IN){
            
            
            LocalTime shiftStart = shift.getShiftStart();
            LocalTime lunchEnd = shift.getLunchEnd();
            LocalTime lunchStart = shift.getLunchStart();
     
            int difference = (int) Math.abs(ChronoUnit.SECONDS.between(stamp, shiftStart));
            
            
            System.out.println(gracePeriod);
            System.out.println(difference);
            
           
            if(dayOfWeek != DayOfWeek.SATURDAY || dayOfWeek != DayOfWeek.SUNDAY){
                if(stamp.isBefore(lunchEnd) && stamp.isAfter(lunchStart)){

                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), lunchEnd.getHour(), lunchEnd.getMinute());
                    adjustedTimeStamp = PunchAdjustmentType.LUNCH_STOP;
                }
                else if(stamp.isBefore(shiftStart) && difference <= roundingInterval){


                    System.out.println("start 1");
                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), shiftStart.getHour(), shiftStart.getMinute());
                    adjustedTimeStamp = PunchAdjustmentType.SHIFT_START;


                }//end test for before
                else if(stamp.isAfter(shiftStart) && difference <= gracePeriod){

                    System.out.println("start 2");
                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), shiftStart.getHour(), shiftStart.getMinute());
                    adjustedTimeStamp = PunchAdjustmentType.SHIFT_START;
                }//end test if in grace
                else if((int) Math.abs(ChronoUnit.MINUTES.between(stamp, shiftStart)) % shift.getRoundingInterval() == 0){
                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), shiftStart.getHour(), shiftStart.getMinute());
                    adjustedTimeStamp = PunchAdjustmentType.NONE;
                }
                else if(stamp.isAfter(shiftStart) && difference >= gracePeriod){


                    shiftStart.plusSeconds(roundingInterval);
                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), shiftStart.getHour(), shiftStart.getMinute());
                    adjustedTimeStamp = PunchAdjustmentType.SHIFT_DOCK;
                }// end test after grace/dock
                //end test for lunch
                else if(stamp.isBefore(shiftStart) && difference > roundingInterval){

                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), shiftStart.getHour(), roundByInterval(lunchEnd.getMinute(), roundingInterval));
                    adjustedTimeStamp = PunchAdjustmentType.INTERVAL_ROUND;
                }//end rounding outside inital 15


                System.out.println(id);
                System.out.println(originalTimeStamp.format(formatter));
                System.out.println(adjustedTimeStamp.toString());
            }else{
                 if(stamp.isBefore(shiftStart) && difference > roundingInterval){

                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), shiftStart.getHour(), roundByInterval(lunchEnd.getMinute(), roundingInterval));
                    adjustedTimeStamp = PunchAdjustmentType.INTERVAL_ROUND;
                }//end rounding outside inital 15
            }
        }//end clock in
        else if(punchType.ordinal() == CLOCK_OUT){
            
            LocalTime shiftEnd = shift.getShiftEnd();
            LocalTime lunchEnd = shift.getLunchEnd();
            LocalTime lunchStart = shift.getLunchStart();
     
            int difference = (int) Math.abs(ChronoUnit.SECONDS.between(stamp, shiftEnd));
          
            System.out.println(gracePeriod);
            System.out.println(difference);
            
            if(dayOfWeek != DayOfWeek.SATURDAY || dayOfWeek != DayOfWeek.SUNDAY){
                if(stamp.isAfter(lunchStart) && stamp.isBefore(lunchEnd)){

                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), lunchStart.getHour(), lunchStart.getMinute());
                    adjustedTimeStamp = PunchAdjustmentType.LUNCH_START;
                }
                else if(stamp.isAfter(shiftEnd) && difference <= roundingInterval){


                    System.out.println("end 1");
                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), shiftEnd.getHour(), shiftEnd.getMinute());
                    adjustedTimeStamp = PunchAdjustmentType.SHIFT_STOP;


                }//end test for before
                else if(stamp.isAfter(shiftEnd) && difference <= gracePeriod){

                    System.out.println("start 2");
                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), shiftEnd.getHour(), shiftEnd.getMinute());
                    adjustedTimeStamp = PunchAdjustmentType.SHIFT_STOP;
                }//end test if in grace
                else if(stamp.isAfter(shiftEnd) && (int) Math.abs(ChronoUnit.MINUTES.between(stamp, shiftEnd)) % shift.getRoundingInterval() == 0){
                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), originalTimeStamp.getHour(), originalTimeStamp.getMinute());
                    adjustedTimeStamp = PunchAdjustmentType.NONE;
                }
                else if(stamp.isAfter(shiftEnd) && difference >= gracePeriod){


                    shiftEnd.plusSeconds(roundingInterval);
                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), shiftEnd.getHour(), shiftEnd.getMinute());
                    adjustedTimeStamp = PunchAdjustmentType.SHIFT_DOCK;
                }// end test after grace/dock
                //end test for lunch
                else if(stamp.isAfter(shiftEnd) && difference > roundingInterval){

                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), shiftEnd.getHour(), roundByInterval(shiftEnd.getMinute(), roundingInterval));
                    adjustedTimeStamp = PunchAdjustmentType.INTERVAL_ROUND;
                }//end rounding outside inital 15


                System.out.println(id);
                System.out.println(changedTimeStamp.format(formatter));
                System.out.println(adjustedTimeStamp.toString());
                
            }// end test day of Week
            else{
                if(stamp.isAfter(shiftEnd) && difference > roundingInterval){

                    changedTimeStamp = LocalDateTime.of(originalTimeStamp.getYear(), originalTimeStamp.getMonthValue(), originalTimeStamp.getDayOfMonth(), shiftEnd.getHour(), roundByInterval(shiftEnd.getMinute(), roundingInterval));
                    adjustedTimeStamp = PunchAdjustmentType.INTERVAL_ROUND;
                }//end rounding outside inital 15
            }
        }
        
    }
    public int roundByInterval(int minutes, int roundBy){
        
        int mod = minutes % roundBy; 
        int rounded;
        
        if((mod) >= NEAREST){
            rounded = minutes+(roundBy - mod);
        }
        else{
            rounded = minutes-mod;
        }
        
        return rounded; 
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

    public PunchAdjustmentType getAdjustedtimestamp() {
        return adjustedTimeStamp;
    }

}
