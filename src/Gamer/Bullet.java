package Gamer;

public class Bullet {
    
    public float x;
    public float y;
    private byte status=0;
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
    
    public byte getStatus(){
        return status;
    }
    
    public void setStatus(byte status){
        this.status = status;
    }
    
           
}
