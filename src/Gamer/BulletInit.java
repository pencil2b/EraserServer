package Gamer;

import static java.lang.Math.abs;
import java.util.Random;

public class BulletInit {
    
    private final Random rand;
    private int MAX_SIZE ;
    private final float SPEED_MAX = (float) 5.0;
    private final float SPEED_LEAST = (float) 1.0;
    private final float start[][] = {{0,0},{0,MAX_SIZE},{MAX_SIZE,0},{MAX_SIZE,MAX_SIZE}};
    private float velocity[];
    private float init[];
    
    public BulletInit(int maxSize) {
        this.MAX_SIZE = maxSize;
        rand = new Random();
        int adjust = rand.nextInt(2);
        init = new float[2];
        init = start[rand.nextInt(4)];
        init[adjust] = rand.nextFloat()*MAX_SIZE;
        adjust ^= 1;
        velocity = new float[2];
        createSenseV(adjust);
    }
    
    private void createSenseV(int x){
        velocity[0] = (SPEED_MAX-SPEED_LEAST)*rand.nextFloat()+SPEED_LEAST;
        if(rand.nextInt(2)==0)velocity[0]=-velocity[0];
        velocity[1] = (SPEED_MAX-SPEED_LEAST)*rand.nextFloat()+SPEED_LEAST;
        if(rand.nextInt(2)==0)velocity[1]=-velocity[1];
        if(init[x]< (MAX_SIZE/2) )velocity[x]=abs(velocity[x]);
        else if(velocity[x]>0) velocity[x] = (-1)*velocity[x];
    }

    public float[] getVelocity() {
        return velocity;
    }
    
    public float [] getStart(){
        return init;
    }
    
    
    
}
