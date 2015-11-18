package com.denis;

public class Counter {
    int counter = 0;

    public int getCounter() {
        return counter;
    }

    public void setCounter(int counter) {
        this.counter += counter;
    }

    public void setZero(){
        this.counter = 0;
    }
}
