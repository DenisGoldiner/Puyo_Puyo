package com.denis;

import java.util.ArrayList;

public class Coordinates {

    ArrayList<Integer[]> coordinates = new ArrayList<Integer[]>();                      // List of massives with coordinates of spheres with same color
    Integer[] mass = new Integer[2];                                            // massive with coordinates of spheres with same color

    public ArrayList<Integer[]> getCoordinates() {
        return coordinates;
    }

    public Integer[] getMass() {
        return mass;
    }

    public void setMass(Integer i, Integer j) {                                 // method to add coordinates to list
        this.mass[0] = i;
        this.mass[1] = j;
        this.coordinates.add(mass);
    }

    public void clearCoords(){
        this.coordinates.clear();
    }
}
