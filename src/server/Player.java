package server;

import CDC.Bullet;
import CDC.UDPSocket;
import java.util.ArrayList;

public class Player {
    
    public static ArrayList<Bullet> rangeBullet;
    private final int SPEED=20;
    private final int id; 
    private final String name;
    public float x; 
    public float y; 
    public float xv = 0; 
    public float yv = 0; 
    public int age = 0;
    private byte status = 0;
    private final UDPSocket udp;
    
    public Player(int id,String name, String ip, int port, float x, float y){
        this.id =  id;
        this.name = name;
        this.x = x;
        this.y = y;
        udp = new UDPSocket(ip,port);
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

    public String getName(){
        return name;
    }

    public int getAge() {
        return age;
    }

    public void setXV(float xv){
        this.xv = xv;
    }
    public void setYV(float yv){
        this.yv = yv;
    }
    
    public float getXV(){
        return this.xv;
    }
    public float getYV(){
        return this.yv;
    }

    public byte getStatus() {
        return status;
    }
    
    public static synchronized void  clearBullet(){
        rangeBullet = new ArrayList<>();
    }

    public  void setRangeBullet(ArrayList<Bullet> rangeBullet) {
        Player.rangeBullet = rangeBullet;
    }

    
    
    
    
    public void setStatus(byte status){
        this.status = status;
    }
    
    public void sendWorld(byte [] data){
        udp.write(data);
    }
    
    

}
