package server;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */



/**
 *
 * @author dorian
 */
public class FPS {
    
    private final double NANO_TO_BASE = 1.0e9;
    private long lastUpdateTime;
    private double deltaTime;
    
    public FPS() {
        lastUpdateTime = System.nanoTime();
    }
    
    public void adjust(int fps) {
        deltaTime = getDelta();
        if (deltaTime < 1.0 / fps) {
            try {
                Thread.sleep((long) ((1.0 / fps - deltaTime) * 1000));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        deltaTime = getDelta();
        lastUpdateTime = System.nanoTime();
    }

    private double getDelta() {
        return (System.nanoTime() - this.lastUpdateTime) / NANO_TO_BASE;
    }
}