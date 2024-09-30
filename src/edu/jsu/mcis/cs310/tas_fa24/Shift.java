/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24;

import java.util.HashMap;

/**
 *
 * @author Will
 * 
 * Shift class, map matches layout of table. Key is index value of each data entry. I.E key 0 ID, key 1 descript, etc.
 */
public class Shift {
    private HashMap<Integer, String> shift = new HashMap<Integer, String>();
    
    public Shift(HashMap<Integer, String> shift){
        this.shift = shift;
        
    }
    
}
