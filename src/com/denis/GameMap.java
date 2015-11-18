package com.denis;

import javax.swing.*;
import javax.swing.Timer;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;


public class GameMap extends JPanel implements ActionListener {

    Counter counter = new Counter();                                        // creating object of class "Counter" for counting spheres of same color
    Coordinates coordinates = new Coordinates();                            // creating object of class "Coordinates" which will store spheres' coordinates if they have same color
    Counter blockCounter = new Counter();                                   // creating object for counting puyos which were blocked

    ArrayList<Puyo> puyoList = new ArrayList<Puyo>();                          // creating List to realize work with pairs of puyo

    Image img;                                                              // img will represent background image
    Timer mainTimer = new Timer(250, this);                                 // creating object "Timer" with retention = 250
    Random random = new Random();                                           // creating variable for generation random values

    ConcurrentHashMap<Integer, Puyo> puyoMap = new ConcurrentHashMap<Integer, Puyo>();  // creating collection for storing all puyos

    Integer integer = 0;                                                    // variable for generating key for "puyoMap"
    final int imgSize = 34;                                                 // high = width of puyo image in pixels

    int fieldWidth = 6;                                                     // width of field
    int fieldHigh = 12;                                                     // High of field
    int[][][] data = new int[fieldWidth][fieldHigh][2];                     // massive for storing (color, key) values for each cell of field

    public void createPuyo (){                                              // method for creating pair of puyo

        int xVal = 0;                                                       // random value of "x" coordinate
        int xNum;                                                           // helping variable to chose "х" coordinate
        int colorNum1;                                                      // variable which will store color of first puyo in pair
        int colorNum2;                                                      // variable which will store color of second puyo in pair

        colorNum1 = random.nextInt(4) + 1;                                  // generating random color of first puyo
        colorNum2 = random.nextInt(4) + 1;                                  // generating random color of second puyo

        xNum = random.nextInt(4) + 1;                                       // generating random "х" coordinate
        switch (xNum){
            case 1: xVal = imgSize;
                break;
            case 2: xVal = imgSize * 2;
                break;
            case 3: xVal = imgSize * 3;
                break;
            case 4: xVal = imgSize * 4;
                break;
            case 5: xVal = imgSize * 5;
                break;
        }

        Puyo newPuyoMain = new Puyo(colorNum1, xVal, -imgSize, true);                       // creating main puyo (it will not move vile rotating)
        Puyo newPuyoSecondary = new Puyo(colorNum2, xVal - imgSize, -imgSize, false);       // creating secondary puyo (it will move vile rotating)


        puyoMap.put(integer, newPuyoSecondary);                                             // adding new secondary puyo to collection
        integer++;                                                                          // changing key value
        puyoMap.put(integer, newPuyoMain);                                                  // adding new main puyo to collection
        integer++;                                                                          // changing key value
    }


    public GameMap (){                                                                      // constructor of "GameMap" object
        img = ResourceLoader.getImage("field.png");                                         // choosing image for background
        addKeyListener(new MyKeyAdapter());
        mainTimer.start();                                                                  // start of timer
        createPuyo();                                                                       // creating first pair of puyo and begin of the game
        setFocusable(true);
    }


    @Override
    public void paint(Graphics g) {
        g.drawImage(img, 0, 0, null);                                                       // drowwing background image

            for (Map.Entry<Integer, Puyo> entry : puyoMap.entrySet()) {                     // for each element in Map collection
                Puyo value = entry.getValue();
                g.drawImage(value.getImg(), value.getX(), value.getY(), null);              // draw puyo
            }
        }


    @Override
    public void actionPerformed(ActionEvent e) {

            for (Map.Entry<Integer, Puyo> entry : puyoMap.entrySet()) {                     // for each element in Map collection
                Integer currentKey = entry.getKey();
                Puyo currentPuyo = entry.getValue();

                fall(currentPuyo, data, currentKey);                                        // use "fall" method
            }

        if(blockCounter.getCounter() == 2){                                                 // if both puyos are down
            checkNeighbour(data, puyoList);                                                 // check both new puyos if they have neighbors of same color
            puyoList.clear();                                                               // clear List with current pair of puyo
            blowPuyo(data);                                                                 // delete all same colored puyo if there is a link of 4 or more spheres
            createPuyo();                                                                   // create new pair of puyo
            blockCounter.setZero();                                                         // set to zero counter of follen puyos
        }
        restruct(data);                                                                     // drop puyos above the deleted one
        repaint();
    }


    public void checkNeighbour (int[][][] data, ArrayList<Puyo> puyoList) {

        for (int y = 0; y < puyoList.size(); ++y) {                                         // for pair of new puyo
            Puyo checkPuyo = puyoList.get(y);                                               // creating helping Puyo object
            check(data, checkPuyo, puyoMap);                                                // check if helping Puyo has neighbours of same color
            counter.setZero();                                                              // set to zerro counter of same colored spheres
            uncheckAll();                                                                   // set off the check mark from all puyo in Map collection
        }
        coordinates.clearCoords();
    }


    public void check (int[][][] data, Puyo checkPuyo, ConcurrentHashMap puyoMap){

        int key;                                                                            // variable for keys of elements of the Map collection
        for(int i = 0; i < fieldWidth; ++i){                                                // for all elements of data massive
            for(int j = 0; j < fieldHigh; ++j){
                if( (Math.abs(i - checkPuyo.getX() / imgSize) + Math.abs(j - checkPuyo.getY() / imgSize) == 1 ) &&    // if the element is (up, left, right, down)
                        ( data[i][j][0] == data[checkPuyo.getX() / imgSize][checkPuyo.getY() / imgSize][0]) ){        // and if color is same to needed

                    key = data[i][j][1];                                                    // save key of accepted puyo
                    Puyo curPuyo = (Puyo)puyoMap.get(key);                                  // creating helping Puyo object
                    if(!curPuyo.check){                                                     // if this object was not counted
                        coordinates.setMass(i, j);                                          // save it's coordinates to massive
                        counter.setCounter(1);                                              // add 1 to counter
                        checkPuyo.check = true;                                             // make a check mark on this puyo
                        check(data, curPuyo, puyoMap);                                      // check if the found puyo has neighbours of same color
                    }
                }
            }
        }

        for(Integer[] currentMass : coordinates.getCoordinates()){                          // for each element in collection of coordinates
            key = data[currentMass[0]][currentMass[1]][1];                                  // taking key value by coordinates
            ((Puyo) puyoMap.get(key)).setNeighborsCol(counter.getCounter());                // setting count of neighbours of same color
        }
        checkPuyo.setNeighborsCol(counter.getCounter());                                    // setting count of neighbours of same color
    }


    public void blowPuyo (int[][][] data){

        Iterator it = puyoMap.entrySet().iterator();                                        // creating iterator (to avoid exception)
        while (it.hasNext())                                                                // for each element
        {
            Map.Entry item = (Map.Entry) it.next();
            Puyo puyo = (Puyo)item.getValue();                                              // creating helping Puyo object
            if(puyo.neighborsCol > 2){                                                      // if puyo has more then 2 neighbours of same color
                data[puyo.getX() / imgSize][puyo.getY() / imgSize][0] = -1;                 // clear color from massive
                data[puyo.getX() / imgSize][puyo.getY() / imgSize][1] = 0;                  // clear key value from massive
                it.remove();                                                                // delete puyo from collection
            }
        }
    }


    public void restruct (int[][][] data){

        for(int i = 0; i < fieldWidth; ++i){                                                // for each element in massive
            for(int j = fieldHigh - 1; j > 0; --j){
                if( (data[i][j][0] < 0) && (data[i][j - 1][0] > 0) ){                       // if there was puyos above the deleted one
                    data[i][j][0] = data[i][j - 1][0];                                      // drop them down
                    (puyoMap.get(data[i][j - 1][1])).setY(j * 34);                          // save changes in collection
                    data[i][j][1] = data[i][j - 1][1];                                      // change keys
                    data[i][j - 1][0] = -1;                                                 // set off color
                    data[i][j - 1][1] = 0;                                                  // set off key
                    repaint();
                }
            }
        }
        repaint();
    }


    public void uncheckAll (){
        for(Map.Entry<Integer, Puyo> entry : puyoMap.entrySet()) {                          // for each element in "puyoMap"
            Puyo value = entry.getValue();
            value.check = false;                                                            // set it unchecked
        }
    }


    private class MyKeyAdapter extends KeyAdapter {

        @Override
        public void keyPressed(KeyEvent e) {
            keyPress(e);
            repaint();
        }
    }


    public void keyPress(KeyEvent e){
        int key = e.getKeyCode();
        ArrayList<Puyo> timePuyo = new ArrayList<Puyo>();
        Puyo puyo1, puyo2;

        for (Map.Entry<Integer, Puyo> observe : puyoMap.entrySet()) {                       // for each puyo in Map collection
            Puyo actionPuyo = observe.getValue();

            if(actionPuyo.position){                                                        // if it is still falling
                timePuyo.add(actionPuyo);                                                   // add it to temporary list
            }
        }

        if(timePuyo.get(0).priority){                                                       // if first element is "main" puyo
            puyo1 = timePuyo.get(0);
            puyo2 = timePuyo.get(1);
        } else {                                                                            // if the second element is "main"
            puyo2 = timePuyo.get(0);
            puyo1 = timePuyo.get(1);
        }                                                                                   // now main is puyo1, secondary is puyo 2


        if (key == KeyEvent.VK_RIGHT) {                                                                     // if the "right" button is pressed
            if((puyo1.getX() < 160) && (puyo2.getX() < 160) && (puyo1.turnRight) && (puyo2.turnRight)){     // check if it is possible to move right
                puyo1.moveRight();                                                                          // move right by both puyo
                puyo2.moveRight();
            }
        }
        if (key == KeyEvent.VK_LEFT) {                                                                      // if the "left" button is pressed
            if((puyo1.getX() > 33) && (puyo2.getX() > 33) && (puyo1.turnLeft) && (puyo2.turnLeft)){         // check if it is possible to move left
                puyo1.moveLeft();                                                                           // move left by both puyo
                puyo2.moveLeft();
            }
        }
        if (key == KeyEvent.VK_DOWN) {                                                                      // if the "down" button is pressed
            if((puyo1.turnDown) && (puyo2.turnDown) && (puyo1.getY() < 300) && (puyo2.getY() < 300) ) {     // check if it is possible to move down
                puyo1.moveDown();                                                                           // move down by both puyo
                puyo2.moveDown();
            }
        }

        if (key == KeyEvent.VK_W) {                                                                                             // if the "W" button is pressed
            if( ((puyo1.getX() - imgSize) == puyo2.getX()) && (puyo1.getY() == puyo2.getY()) && (puyo2.rotateLB)){              // if secondary puyo is on the left from the main one
                puyo2.rotateLB();                                                                                               // rotate it to the bottom position
            } else if( (puyo1.getX() == puyo2.getX()) && ((puyo1.getY() + imgSize) == puyo2.getY())  && (puyo2.rotateBR)){      // if secondary puyo is under the main one
                puyo2.rotateBR();                                                                                               // rotate it to the right position
            } else if( ((puyo1.getX() + imgSize) == puyo2.getX()) && (puyo1.getY() == puyo2.getY())  && (puyo2.rotateRU)){      // if secondary puto is on the right from the main one
//                puyo2.rotateRU();                                                                                               // rotate it to the up position
            } else if( (puyo1.getX() == puyo2.getX()) && ((puyo1.getY() - imgSize) == puyo2.getY())  && (puyo2.rotateUL)){      // if the secondary puyo is in the up position
                puyo2.rotateUL();                                                                                               // rotate it to the left one
            }
        }

        if (key == KeyEvent.VK_Q) {                                                                                             // if the "Q" button is pressed
            if( ((puyo1.getX() - imgSize) == puyo2.getX()) && (puyo1.getY() == puyo2.getY()) && (puyo2.rotateLU)){              // if secondary puyo is on the left from the main one
//                puyo2.rotateLU();                                                                                               // rotate it to the up position
            } else if( (puyo1.getX() == puyo2.getX()) && ((puyo1.getY() + imgSize) == puyo2.getY())  && (puyo2.rotateBL)){      // if secondary puyo is under the main one
                puyo2.rotateBL();                                                                                               // rotate it to the left position
            } else if( ((puyo1.getX() + imgSize) == puyo2.getX()) && (puyo1.getY() == puyo2.getY())  && (puyo2.rotateRB)){      // if secondary puto is on the right from the main one
                puyo2.rotateRB();                                                                                               // rotate it to the bottom position
            } else if( (puyo1.getX() == puyo2.getX()) && ((puyo1.getY() - imgSize) == puyo2.getY())  && (puyo2.rotateUR)){      // if the secondary puyo is in the up position
                puyo2.rotateUR();                                                                                               // rotate it to the right position
            }
        }
    }


    public void fall (Puyo currentPuyo, int[][][] data, int currentKey){
        if (currentPuyo.position) {                                                                             // if this puyo is still falling
            currentPuyo.fallDown();                                                                             // fall it down

        ifChanseToAction(currentPuyo, data);

            if (data[(currentPuyo.getX() / imgSize)][currentPuyo.getY() / imgSize][0] > 0) {                    // if position is not empty

                if (currentPuyo.getY() / imgSize > 1) {                                                         // for each coordinate except у = 0

                    currentPuyo.setY(currentPuyo.getY() - currentPuyo.dy);                                      // replace puyo on one step back
                    data[currentPuyo.getX() / imgSize][currentPuyo.getY() / imgSize][0] = currentPuyo.color;    // write color information in data massive
                    data[currentPuyo.getX() / imgSize][currentPuyo.getY() / imgSize][1] = currentKey;           // write key in data massive
                    currentPuyo.position = false;                                                               // change position on "fallen"
                    currentPuyo.block = true;                                                                   // set block
                    blockCounter.setCounter(1);                                                                 // counting blocked elements
                    puyoList.add(currentPuyo);                                                                  // add last one puyo to List
                } else {                                                                                        // in case of upper elements
                    currentPuyo.setY(currentPuyo.getY() - currentPuyo.dy);                                      // draw on top place last puyo
                    data[currentPuyo.getX() / imgSize][0][0] = currentPuyo.color;                               // add information to data massive about last puyo in game
                    data[currentPuyo.getX() / imgSize][0][1] = currentKey;
                    currentPuyo.position = false;                                                               // set station "fallen"
                    currentPuyo.block = true;                                                                   // set block
                    mainTimer.stop();                                                                           // stop the timer
                    JOptionPane.showMessageDialog(null, "The end!");                                            // give dialogue window
                }
            }
        } else if (!currentPuyo.block) {                                                                        // if puyo is already fallen
            currentPuyo.block = true;                                                                           // block it (this is needed to provide unstoppable generating of puyos)
            blockCounter.setCounter(1);                                                                         // add 1 to counter of blocked spheres
            data[currentPuyo.getX() / imgSize][currentPuyo.getY() / imgSize][0] = currentPuyo.color;            // save to data massive color
            data[currentPuyo.getX() / imgSize][currentPuyo.getY() / imgSize][1] = currentKey;                   // save to data massive key
            puyoList.add(currentPuyo);                                                                          // add to List
        }
    }

    public void ifChanseToAction(Puyo currentPuyo, int[][][] data){

        if( (currentPuyo.getX() > imgSize) && (currentPuyo.getY() > 0) &&                       // to avoid setting several spheres on one place and out of bound exceptions
                (data[(currentPuyo.getX() / imgSize) - 1][currentPuyo.getY() / 34][0] > 0) ){
            currentPuyo.turnLeft = false;
        }

        if( (currentPuyo.getX() < 160) && (currentPuyo.getY() > 0) &&                           // to avoid setting several spheres on one place and out of bound exceptions
                (data[(currentPuyo.getX() / imgSize) + 1][currentPuyo.getY() / imgSize][0] > 0) ){
            currentPuyo.turnRight = false;
        }

        if((currentPuyo.getY() > 300) || (currentPuyo.getY() > 0) &&                            // to avoid setting several spheres on one place and out of bound exceptions
                (data[(currentPuyo.getX() / imgSize)][(currentPuyo.getY() / imgSize) + 1][0] > 0) ){
            currentPuyo.turnDown = false;
        }

        if(!currentPuyo.priority) {

            if ((currentPuyo.getY() < 0) || (currentPuyo.getY() > 350) || (currentPuyo.getX() > 150)){
                currentPuyo.rotateLB = false;
            } else if(data[(currentPuyo.getX() / imgSize) + 1][(currentPuyo.getY() / imgSize) + 1][0] > 0){
                currentPuyo.rotateLB = false;
            } else {                                                                            // to avoid setting several spheres on one place and out of bound exceptions
                currentPuyo.rotateLB = true;
            }


            if((currentPuyo.getY() < imgSize) || (currentPuyo.getX() > 150) || (currentPuyo.getY() > 350)){
                currentPuyo.rotateBR = false;
            } else if (data[(currentPuyo.getX() / imgSize) + 1][(currentPuyo.getY() / imgSize) - 1][0] > 0){
                currentPuyo.rotateBR = false;
            } else {                                                                            // to avoid setting several spheres on one place and out of bound exceptions
                currentPuyo.rotateBR = true;
            }

//BUG
/*
            if ((currentPuyo.getY() < imgSize) || (currentPuyo.getX() < imgSize) || (currentPuyo.getY() > 300)){
                currentPuyo.rotateRU = false;
            } else if (data[(currentPuyo.getX() / imgSize) - 1][(currentPuyo.getY() / imgSize) - 1][0] > 0) {
                currentPuyo.rotateRU = false;
            } else {                                                                            // to avoid setting several spheres on one place and out of bound exceptions
                currentPuyo.rotateRU = true;
            }
*/

            if ((currentPuyo.getY() < imgSize) || (currentPuyo.getX() < imgSize) || (currentPuyo.getY() > 350)) {
                currentPuyo.rotateUL = false;
            } else if (data[(currentPuyo.getX() / imgSize) - 1][(currentPuyo.getY() / imgSize) + 1][0] > 0) {
                    currentPuyo.rotateUL = false;
            } else {                                                                            // to avoid setting several spheres on one place and out of bound exceptions
                currentPuyo.rotateUL = true;
            }


            if ((currentPuyo.getY() < imgSize) || (currentPuyo.getY() > 350) || (currentPuyo.getX() > 150)){
                currentPuyo.rotateLU = false;
            } else if(data[(currentPuyo.getX() / imgSize) + 1][(currentPuyo.getY() / imgSize) - 1][0] > 0){
                currentPuyo.rotateLU = false;
            } else {                                                                            // to avoid setting several spheres on one place and out of bound exceptions
                currentPuyo.rotateLU = true;
            }


            if((currentPuyo.getY() < imgSize) || (currentPuyo.getX() < imgSize) || (currentPuyo.getY() > 350)){
                currentPuyo.rotateBL = false;
            } else if (data[(currentPuyo.getX() / imgSize) - 1][(currentPuyo.getY() / imgSize) - 1][0] > 0){
                currentPuyo.rotateBL = false;
            } else {                                                                            // to avoid setting several spheres on one place and out of bound exceptions
                currentPuyo.rotateBL = true;
            }

//BUG
/*
            if ((currentPuyo.getY() < 0) || (currentPuyo.getX() < imgSize) || (currentPuyo.getY() > 300)){
                currentPuyo.rotateRB = false;
            } else if (data[(currentPuyo.getX() / imgSize) - 1][(currentPuyo.getY() / imgSize) + 1][0] > 0) {
                currentPuyo.rotateRB = false;
            } else {                                                                            // to avoid setting several spheres on one place and out of bound exceptions
                currentPuyo.rotateRB = true;
            }
*/

            if ((currentPuyo.getY() < imgSize) || (currentPuyo.getX() > 150) || (currentPuyo.getY() > 350)) {
                currentPuyo.rotateUR = false;
            } else if (data[(currentPuyo.getX() / imgSize) + 1][(currentPuyo.getY() / imgSize) + 1][0] > 0) {
                currentPuyo.rotateUR = false;
            } else {                                                                            // to avoid setting several spheres on one place and out of bound exceptions
                currentPuyo.rotateUR = true;
            }
        }
    }
}
