package com.denis;

import javax.swing.*;

public class Main {

    public static void main(String[] args) {

        JFrame mainFrame = new JFrame("Puyo - Puyo (0.1)");             // creating new frame
        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);       // close if close button is pressed
        mainFrame.setSize(208, 438);                                    // size if mane window (208, 438)
        mainFrame.setResizable(false);                                  // set frame unresizable
        mainFrame.setLocation(600, 200);
        mainFrame.add(new GameMap());                                   // Creating GameMap
        mainFrame.setVisible(true);                                     // set the window visible

    }
}
