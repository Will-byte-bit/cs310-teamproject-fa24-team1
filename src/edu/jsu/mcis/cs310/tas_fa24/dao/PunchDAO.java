/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package edu.jsu.mcis.cs310.tas_fa24.dao;

/**
 *
 * @author Will
 */
public class PunchDAO {
     private static final String QUERY_FIND_ID = "SELECT * FROM shift WHERE id = ?";
    private static final String QUERY_FIND_BADGE = "SELECT shiftid FROM employee WHERE badge = ?";
    


    private final DAOFactory daoFactory;
    
    
    public PunchDAO(DAOFactory daoFactory){
        this.daoFactory = daoFactory;
    }
}
