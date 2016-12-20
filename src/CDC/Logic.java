/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CDC;

import server.User;
import server.Player;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;

/**
 *
 * @author Neptune
 */
public class Logic {
    

    public static void updatePlayer(byte data[]) throws IOException{
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        int id = dis.readShort();
        float x = dis.readFloat() * Configure.PLAYERSPEED;
        float y = dis.readFloat() * Configure.PLAYERSPEED;
        CDC.Data.playerList.get(id).setXV(x);
        CDC.Data.playerList.get(id).setYV(y);

    }
    
    public static void deadAnnouncment(int id) {
        Player pp = CDC.Data.playerList.get(id);
        User user = CDC.Data.userList.get(id);
        user.deadAction();
        pp.setStatus((byte) 2);
        broadcastRank();
    }

    public static void logic() {
        for (Integer x : CDC.Data.playerList.keySet()) {
            if (CDC.Data.playerList.containsKey(x)) {
                Player pp = CDC.Data.playerList.get(x);
                
                    int age = pp.getAge();
                    float px = pp.getX();
                    float py = pp.getY();
                    ArrayList<Bullet> temp = new ArrayList<>();
                    int bulletCount = CDC.Data.bulletList.size();
                    for (int i = 0; i < bulletCount; i++) {
                        Bullet bb = CDC.Data.bulletList.get(i);
                        double dis = Math.sqrt((px - bb.getX()) * (px - bb.getX()) + (py - bb.getY()) * (py - bb.getY()));
                        double radius = 10 + age / 3;
                        if(dis<radius && pp.getStatus() == 1){
                            deadAnnouncment(pp.getID());
                            break;
                        }
                        else if(dis<CDC.Configure.VISIBLE_RADIUS){
                            temp.add(bb);
                        }
                    }
                    pp.setRangeBullet(temp);
                            
            }
        }
    }

    public static void movePlayer() throws IOException {
        if (!CDC.Data.playerList.isEmpty()) {
            for(Integer ii:CDC.Data.playerList.keySet()){
                Player pp = CDC.Data.playerList.get(ii);

                byte status = pp.getStatus();
                if (status == 0 || status == 1) {
                    if ((pp.x + pp.xv) < Configure.MAX_WIDTH && (pp.x + pp.xv) > 0) {
                        pp.x += pp.xv;
                    } else if ((pp.x + pp.xv) >= Configure.MAX_WIDTH) {
                        pp.x = Configure.MAX_WIDTH;
                    } else if ((pp.x + pp.xv) <= 0) {
                        pp.x = 0;
                    }

                    if ((pp.y + pp.yv) < Configure.MAX_HEIGHT && (pp.y + pp.yv) > 0) {
                        pp.y += pp.yv;
                    } else if ((pp.y + pp.yv) >= Configure.MAX_HEIGHT) {
                        pp.y = Configure.MAX_HEIGHT;
                    } else if ((pp.y + pp.yv) <= 0) {
                        pp.y = 0;
                    }
                }
            }
        }

    }

    public static void broadcast() throws IOException {
        
        if (!CDC.Data.playerList.isEmpty()) {
            CDC.Data.playerList.keySet().stream().forEach((x) -> {
                try{
                    Player p = CDC.Data.playerList.get(x);
                    byte[] buffer = createEverything(p);
                    p.sendWorld(buffer);
                }catch(Exception ex){}
            });
        }
    }

    public static void broadcastRank() {
        CDC.Data.userList.keySet().stream().forEach((x) -> {
            CDC.Data.userList.get(x).updateOnlinePlayer();
        });
    }

    public static byte[] createEverything(Player p) throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        int playerCount = CDC.Data.playerList.keySet().size();
        int bulletCount = p.rangeBullet.size();

        dos.writeShort(playerCount);
        dos.writeShort(bulletCount);

        if (playerCount > 0) {
            for (Integer x : CDC.Data.playerList.keySet()) {
                if (CDC.Data.playerList.containsKey(x)) {
                    Player pp = CDC.Data.playerList.get(x);
                    dos.writeShort(pp.getID());
                    dos.writeFloat(pp.getX());
                    dos.writeFloat(pp.getY());
                    dos.writeShort(pp.getAge());
                    dos.writeByte(pp.getStatus());
                }
            }
        }

        if (bulletCount > 0) {
            for (int i = 0; i < bulletCount; i++) {
                Bullet b = p.rangeBullet.get(i);
                dos.writeFloat(b.getX());
                dos.writeFloat(b.getY());
                float[] x = b.velocity;
                dos.writeFloat(x[0]);
                dos.writeFloat(x[1]);
            }
        }
        
        return baos.toByteArray();
    }

}
