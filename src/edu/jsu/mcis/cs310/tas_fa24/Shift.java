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
    
    // new DailySchedule Objects
    private DailySchedule defaultSchedule;
    private HashMap<DayOfWeek, DailySchedule> dailySchedules;
    
    private final int  DEFAULT = 0;
    
    /**
     * Constructor for Shift v.2
     * @param defaultSchedule, DailuSchedule
     * @param dailySchedules. DailySchedule
     */
    public Shift(DailySchedule defaultSchedule, HashMap<DayOfWeek, DailySchedule> dailySchedules){
        
        this.defaultSchedule = defaultSchedule;
        this.dailySchedules = dailySchedules != null ? dailySchedules : new HashMap<>();
       
        //calculates differences using DailySchedule Object
        this.shiftDuration = calcTimeDifferenceShift(defaultSchedule.getShiftStart(), defaultSchedule.getShiftEnd());
        this.lunchDuration = calcTimeDifferenceLunch(defaultSchedule.getLunchStart(), defaultSchedule.getLunchEnd()); 
        
    }
    

    public int calcTimeDifferenceShift(LocalTime ShiftStart, LocalTime ShiftEnd){
        
        //duration of shift in minutes
        int duration = DEFAULT;
        
        //testing if shift end is greater than shift start
        if(defaultSchedule.getShiftEnd().getMinute() > defaultSchedule.getShiftStart().getMinute()){
        
         return (int) ChronoUnit.MINUTES.between(defaultSchedule.getShiftStart(), defaultSchedule.getShiftEnd());
       
        //testing if it is less than, meaning a different day.
        }else if(defaultSchedule.getShiftEnd().getMinute() < defaultSchedule.getShiftStart().getMinute()){
            
            LocalDateTime shiftStartLDT = LocalDateTime.of(2024,10,6, defaultSchedule.getShiftStart().getHour(), defaultSchedule.getShiftStart().getMinute());
            LocalDateTime shiftEndLDT = LocalDateTime.of(2024,10,7, defaultSchedule.getShiftEnd().getHour(), defaultSchedule.getShiftEnd().getMinute());
            
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
        
        if(defaultSchedule.getLunchEnd().getMinute() > defaultSchedule.getLunchStart().getMinute()){
         return (int) ChronoUnit.MINUTES.between(defaultSchedule.getLunchStart(), defaultSchedule.getLunchEnd());
        
        }
        //testing if it is less than, meaning a different day.
        else if(defaultSchedule.getLunchEnd().getMinute() < defaultSchedule.getLunchStart().getMinute()){
            
            LocalDateTime shiftStartLDT = LocalDateTime.of(2024,10,6, defaultSchedule.getLunchStart().getHour(), defaultSchedule.getLunchStart().getMinute());
            LocalDateTime shiftEndLDT = LocalDateTime.of(2024,10,7, defaultSchedule.getLunchEnd().getHour(), defaultSchedule.getLunchEnd().getMinute());
            
            Duration difference = Duration.between(shiftStartLDT, shiftEndLDT);
            duration = (difference.toHoursPart() * 60) + difference.toMinutesPart();
            
            
        }
        
        return duration;
    }
    
     /**
     * Checks if lunch should be deducted based on the threshold.
     * @param totalWorkedMinutes The total minutes worked by the employee.
     * @return true if lunch deduction applies, false otherwise.
     * @author samca
     */
    public boolean isLunchDeductible(int totalWorkedMinutes) {
        return totalWorkedMinutes >= defaultSchedule.getLunchThreshold();
    }
 
    
    // Upated toString() to get values from defaultSchedule
    public String toString(){
        String result;
        
        StringBuilder sb = new StringBuilder();
   
        
        //appending through shift
        sb.append(defaultSchedule.getDescription()).append(": ").append(getShiftStart().format(outTake)).append(" - ").append(getShiftEnd().format(outTake)).append(" (").append(String.valueOf(shiftDuration));
        sb.append(" minutes); ");
        
        //appending through lunch
        sb.append("Lunch: ").append(getLunchStart().format(outTake)).append(" - ").append(getLunchEnd().format(outTake));
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

    public int getRoundingInterval() {
        return defaultSchedule.getRoundingInterval();
    }

    public int getGracePeriod() {
        return defaultSchedule.getGracePeriod();
    }

    public int getDockPenalty() {
        return defaultSchedule.getDockPenalty();
    }

    public int getLunchThreshold() {
        return defaultSchedule.getLunchThreshold();
    }

    public LocalTime getShiftStart() {
        return defaultSchedule.getShiftStart();
    }

    public LocalTime getShiftEnd() {
        return defaultSchedule.getShiftEnd();
    }

    public LocalTime getLunchStart() {
        return defaultSchedule.getLunchStart();
    }

    public LocalTime getLunchEnd() {
        return defaultSchedule.getLunchEnd();
    }
    
    public int getDailyScheduledMinutes() {
	return shiftDuration - lunchDuration;
    }
    
    public DailySchedule getDefaultSchedule(){
        return defaultSchedule;
    }
    
    // retrieves schedule for specific day
    public DailySchedule getScheduleForDay(DayOfWeek day){
        return dailySchedules.getOrDefault(day, defaultSchedule);
    }
    
}
