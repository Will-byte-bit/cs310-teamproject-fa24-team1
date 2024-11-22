/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24;

import java.util.HashMap;
import java.time.*;
import java.time.format.DateTimeFormatter;


/**
 * The Shift class is an object that represents the shift data.
 * It contains a HashMap of keys and values.
 * The key is the index value of each data entry, i.e., key 0: ID, key 1: description, etc.

 *
 * 
 * @author William Saint
 * 
 * 
 *
 */

//refactor code to all instance
public class Shift {
    
  
    private final HashMap<String, String> shift = new HashMap<>();
    private String description;
   
    
    private final DailySchedule defaultSchedule;
    
    //might need to change to scheduleWeek
    private HashMap<DayOfWeek, DailySchedule> dailySchedules = new HashMap<>();
    
    private final DateTimeFormatter outTake;
    
    private int shiftDuration;
    private int lunchDuration;
    
    /**
     * Constructor for Shift v.2
     * @param defaultSchedule, DailuSchedule
     * @param dailySchedules. DailySchedule
     */
    
    public Shift(DailySchedule defaultSchedule, HashMap<DayOfWeek, DailySchedule> dailySchedules){
        
        this.defaultSchedule = defaultSchedule;
        this.dailySchedules =  new HashMap<>(dailySchedules);
        this.outTake = DateTimeFormatter.ofPattern("HH:mm");
      

    }
    
    public Shift(int id, String description, DailySchedule daily){
        this.description = description;
        this.defaultSchedule = daily;
        this.shiftDuration = defaultSchedule.calcTimeDifferenceShift();
        this.lunchDuration = defaultSchedule.calcTimeDifferenceLunch();
        this.outTake = DateTimeFormatter.ofPattern("HH:mm");
 
     
    }
    

    public int calcTimeDifferenceShift(LocalTime ShiftStart, LocalTime ShiftEnd){
        //Method moved to DailySchedule.Java. Method stub kept for legacy compatibility.
        return this.shiftDuration;
    }
     
    public int calcTimeDifferenceLunch(LocalTime lunchStart, LocalTime lunchEnd){
       
        return this.lunchDuration;
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

    /**
     *
     * @return
     */
    @Override
    public String toString(){
        String result;
        
        StringBuilder sb = new StringBuilder();
   
        
        //appending through shift
        sb.append(description).append(": ").append(getShiftStart().format(outTake)).append(" - ").append(getShiftEnd().format(outTake)).append(" (").append(String.valueOf(shiftDuration));
        sb.append(" minutes); ");
        
        //appending through lunch
        sb.append("Lunch: ").append(getLunchStart().format(outTake)).append(" - ").append(getLunchEnd().format(outTake));
        sb.append(" (").append(String.valueOf(lunchDuration)).append(" minutes)");
        
        result = sb.toString();
  
        return result;
    }
    
    //getters
    public String getDescription(){
        return this.description;
    }
    
    public HashMap<String, String>getShift() {
        return shift;
    }

    public int getShiftDuration() {
        
        return defaultSchedule.getShiftDuration();
        
    }

    public int getLunchDuration() {
        return defaultSchedule.getLunchDuration();
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
	return shiftDuration -  lunchDuration;
    }
    
    public DailySchedule getDefaultSchedule(){
        
        return this.defaultSchedule;
    }
    
    // retrieves schedule for specific day
    public DailySchedule getDefaultSchedule(DayOfWeek day){
        return dailySchedules.getOrDefault(day, defaultSchedule);
    }
    public HashMap<DayOfWeek, DailySchedule> GET_ALL(){
        return dailySchedules;
    }
    public void PRINT_ALL(){
       
        for(int i = 1; i <= dailySchedules.size(); i++){
            System.out.println(dailySchedules.get(DayOfWeek.of(i%7)).getShiftStart());
            System.out.println(dailySchedules.get(DayOfWeek.of(i%7)).getShiftEnd());
        }
    }
  
}
