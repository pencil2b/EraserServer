package Gamer;

public class Player {
    
    private final int SPEED=20;
    private final int id; 
    public float x; 
    public float y; 
    private short age = 0;
    private byte status = 0;
    private final UDPSocket udp;
    
    public Player(int id, String ip, int port, float x, float y){
        this.id =  id;
        this.x = x;
        this.y = y;
        this.status = status;
        udp = new UDPSocket(ip,port);
        
        new Thread(() -> {
            try {
                Thread.sleep(1000);
            } catch (InterruptedException ex) {}
            age += 1;
        }).start();
        
    }

    public void set(float x, float y){
        this.x += x*SPEED;
        this.y += y*SPEED;
    }
    
    public int getID(){
        return id;
    }

    public float getX() {
        return x;
    }

    public float getY() {
        return y;
    }


    public short getAge() {
        return age;
    }


    public byte getStatus() {
        return status;
    }
    
    public void setStatus(byte status){
        this.status = status;
    }
    
    public void sendWorld(byte [] data){
        udp.write(data);
    }
    
}
