package com.denis;

import java.awt.*;

public class Puyo {

    Image img;                                          // image of puyo
    int color;                                          // color of sphere
    int x, y;                                           // coordinates
    boolean priority;                                   // priority (true - stays wile rotating, false - moves while rotating)
    boolean position = true;                            //  position (true - is falling, false - has fallen)
    boolean block = false;
    int dy = 34;                                        // range of step by y
    int dx = 34;                                        // range of step by x
    int neighborsCol;                                   // count of neighbours which has same color

    boolean turnLeft = true;                            // variable which shows if it is possible to turn left
    boolean turnRight = true;                           // variable which shows if it is possible to turn right
    boolean turnDown = true;                            // variable which shows if it is possible to turn down

    boolean rotateLB = true;                            // variable which shows if it is possible to rotate from Left to Down
    boolean rotateBR = true;                            // variable which shows if it is possible to rotate from Down to Right
    boolean rotateRU = true;                            // variable which shows if it is possible to rotate from Right to Up
    boolean rotateUL = true;                            // variable which shows if it is possible to rotate from Up to Left

    boolean rotateLU = true;                            // variable which shows if it is possible to rotate from Left to Up
    boolean rotateUR = true;                            // variable which shows if it is possible to rotate from Up to Right
    boolean rotateRB = true;                            // variable which shows if it is possible to rotate from Right to Down
    boolean rotateBL = true;                            // variable which shows if it is possible to rotate from Down to Left

    boolean check = false;                              // sets as counted (false - unchecked, true - checked

    public void setNeighborsCol(int neighborsCol) {
        this.neighborsCol = neighborsCol;
    }


    public Puyo(int color, int x, int y, boolean priority) {
        this.color = color;
        this.x = x;
        this.y = y;
        this.priority = priority;

        if (this.color == 1){
            this.img = ResourceLoader.getImage("puyo_red_new.png");             // 1 = red
        }   else  if (this.color == 2){
            this.img = ResourceLoader.getImage("puyo_green_new.png");           // 2 = green
        }   else if (this.color == 3){
            this.img = ResourceLoader.getImage("puyo_yellow_new.png");          // 3 = yellow
        }   else if (this.color == 4){
            this.img = ResourceLoader.getImage("puyo_blue_new.png");            // 4 = blue
        }   else {
            System.out.println("Error: wrong color");
        }
    }


    public void deleteImg(){
        this.img = null;
    }

    public void fallDown(){                                             // method for puyo falling
        if(y <= 350) {                                                  // maximum distance of falling 350 pixels
            y += dy;
        } else {
            position = false;                                           // if y = 350 set puyo as fallen
        }
    }

    public void moveRight(){
        if(x <= 160) {
            x += dx;
        }
    }

    public void moveDown(){
        if(y <= 350) {
            y += dy;
        }
    }

    public void moveLeft(){
        if(x >= dx) {
            x -= dx;
        }
    }

    /*
        this.setX(this.getX() + 34);
        this.setY(this.getY() + 34);
    */

    public void rotateLB() {
        x += dx;
        y += dy;
    }

    public void rotateBR() {
        x += dx;
        y -= dy;
    }

    public void rotateRU() {
        x -= dx;
        y -= dy;
    }

    public void rotateUL() {
        x -= dx;
        y += dy;
    }

    public void rotateLU() {
        x += dx;
        y -= dy;
    }

    public void rotateBL() {
        x -= dx;
        y -= dy;
    }

    public void rotateRB() {
        x -= dx;
        y += dy;
    }

    public void rotateUR() {
        x += dx;
        y += dy;
    }

        /*
        if(!this.priority){
            double x = this.getX() / dx;
            double y = this.getY() / dy;
            double alfa = Math.PI / 90;
            this.setX((int)(Math.cos(alfa) * x - Math.sin(alfa) * y) * dx);
            this.setY((int)(Math.sin(alfa) * x + Math.cos(alfa) * y) * dy);
        }
        */


    public void setY(int y) {
        this.y = y;
    }

    public int getX() {
        return x;
    }

    public void setX(int x) {
        this.x = x;
    }

    public int getY() {
        return y;
    }

    public Image getImg() {
        return img;
    }

}
