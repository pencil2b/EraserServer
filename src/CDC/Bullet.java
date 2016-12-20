package CDC;

public class Bullet {
    
    public float x;
    public float y;
    public final float[] velocity;
    
    public Bullet(int MAX_WIDTH_SIZE, int MAX_HEIGHT_SIZE){
        BulletInit init = new BulletInit(MAX_WIDTH_SIZE,MAX_HEIGHT_SIZE);
        velocity = init.getVelocity();
        float temp[] = init.getStart();
        this.x = temp[0];
        this.y = temp[1];
        
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }
    
    public float[] getV(){
        return velocity;
    }
           
}
