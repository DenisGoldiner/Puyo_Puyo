package com.denis;

import java.awt.*;

public class ResourceLoader {

    public static ResourceLoader resourceLoader = new ResourceLoader();

    public static Image getImage(String filename){
        return Toolkit.getDefaultToolkit().getImage(resourceLoader.getClass().getClassLoader().getResource("com/denis/res/" + filename));
    }
                        // method which makes process of adding images easier
}
