/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.lanyastarhotelreservation.model;

/**
 *
 * @author johnm
 */
public class Room {
    private int roomId;
    private String type;
    private int capacity;
    private int maxExtraBed;
    private int availableRooms;
    private String rate;

    public Room(int roomId, String type, int capacity, int maxExtraBed, int availableRooms, String rate) {
        this.roomId = roomId;
        this.type = type;
        this.capacity = capacity;
        this.maxExtraBed = maxExtraBed;
        this.availableRooms = availableRooms;
        this.rate = rate;
    }

    public int getRoomId() { return roomId; }
    public String getType() { return type; }
    public int getCapacity() { return capacity; }
    public int getMaxExtraBed() { return maxExtraBed; }
    public int getAvailableRooms() { return availableRooms; }
    public String getRate() { return rate; }
}
