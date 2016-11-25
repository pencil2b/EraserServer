package Gamer;

import static java.lang.Math.abs;
import java.util.Random;

public class BulletInit {
    
    private final Random rand;
    private final int MAX_WIDTH ;
    private final int MAX_HEIGHT ;
    private final float SPEED_MAX = (float) 5.0;
    private final float SPEED_LEAST = (float) 1.0;
    private float velocity[];
    private float init[];
    
    public BulletInit(int maxWidth, int maxHeight) {
        this.MAX_WIDTH = maxWidth;
        this.MAX_HEIGHT = maxHeight;
        float start[][] = {{0,0},{0,MAX_HEIGHT},{MAX_WIDTH,0},{MAX_WIDTH,MAX_HEIGHT}};
        
        rand = new Random();
        int adjust = rand.nextInt(2);
        int x =0;
        if(adjust == 0)x = maxWidth;
        if(adjust == 1)x = maxHeight;
        
        init = new float[2];
        init = start[rand.nextInt(4)];
        init[adjust] = rand.nextFloat()* x ;
        adjust ^= 1;
        
        velocity = new float[2];
        createSenseV(adjust);
    }
    
    private void createSenseV(int x){
        velocity[0] = (SPEED_MAX-SPEED_LEAST)*rand.nextFloat()+SPEED_LEAST;
        if(rand.nextInt(2)==0)velocity[0]=-velocity[0];
        velocity[1] = (SPEED_MAX-SPEED_LEAST)*rand.nextFloat()+SPEED_LEAST;
        if(rand.nextInt(2)==0)velocity[1]=-velocity[1];
        int temp =0;
        if(x == 0)temp = MAX_WIDTH;
        if(x == 1)temp = MAX_HEIGHT;
        if( init[x]< (temp/2) )velocity[x]=abs(velocity[x]);
        else if(velocity[x]>0) velocity[x] = (-1)*velocity[x];
    }

    public float[] getVelocity() {
        return velocity;
    }
    
    public float [] getStart(){
        return init;
    }
    
    
    
}
