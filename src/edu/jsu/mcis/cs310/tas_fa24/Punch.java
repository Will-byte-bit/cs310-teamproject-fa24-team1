/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 *
 * @author Will
 */
public class Punch {
    private int terminalId;
    private int id;
    private Badge badge = null;
    private EventType punchType = null;
    private LocalDateTime originalTimeStamp = null;
    private PunchAdjustmentType adjustedTimeStamp = null;
   
    
    public Punch(int terminalId, Badge badge, EventType punchType){
        this.terminalId = terminalId;
        this.badge = badge;
        this.punchType = punchType;
        originalTimeStamp = LocalDateTime.now();
    }
    public Punch(int id, int terminalid, Badge badge, LocalDateTime originalTimeStamp, EventType punchtype){
        this.id = id;
        this.terminalId = terminalId;
        this.badge = badge;
        this.punchType = punchtype;
        this.originalTimeStamp = originalTimeStamp;
        
    }
    public String printOriginal(){
        
        //get day of week abriv
        String day = originalTimeStamp.getDayOfWeek().toString().substring(0, 3);
        
        //format string
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MM/dd/yyyy HH:mm:ss");
        StringBuilder sb = new StringBuilder();
        
        sb.append("#").append(badge.getId()).append(" ").append(punchType).append(": ").append(day).append(" ").append(originalTimeStamp.format(formatter));

        return sb.toString();
    }
    public int getTerminalId() {
    return terminalId;
    }

    public int getId() {
        return id;
    }

    public Badge getBadge() {
        return badge;
    }

    public EventType getPunchType() {
        return punchType;
    }

    public LocalDateTime getOriginalTimeStamp() {
        return originalTimeStamp;
    }

    public PunchAdjustmentType getAdjustedTimeStamp() {
        return adjustedTimeStamp;
    }

}
