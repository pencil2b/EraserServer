package server;

import Gamer.Bullet;
import Gamer.Player;
import Gamer.User;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;

public class Server {

    public static HashMap<Integer, Player> playerList;
    public static ArrayList<Bullet> bulletList;
    public static ArrayList<Object[]> recordList;

    private Runnable broadcastRunnable;
    private Runnable createBulletRunnable;
    private Runnable logicRunnable;
    private Runnable moveBulletRunnable;

    private final int PORT = 20000;
    private short count = 1;

    private final int BULLET_MAXSIZE = 5000;
    private final int max_amount_bullet = 100;

    UDPSocket udp;

    public static void main(String args[]) throws IOException {
        new Server().establish();
    }

    public void establish() throws IOException {

        recordList = new ArrayList<>();
        bulletList = new ArrayList<>();
        playerList = new HashMap<>();
        udp = new UDPSocket(PORT);

        implementsRunnable();
        new Thread(logicRunnable).start();
        new Thread(broadcastRunnable).start();
        new Thread(createBulletRunnable).start();
        new Thread(moveBulletRunnable).start();

        ServerSocket serverSock = new ServerSocket(PORT);
        while (true) {
            Socket cSocket = serverSock.accept();
            new Thread(new User(count++, cSocket)).start();
        }

    }

    public void implementsRunnable() {

        broadcastRunnable = new Runnable() {
            @Override
            public void run() {
                FPS fps = new FPS();
                while (true) {
                    try {
                        fps.adjust(50);
                        broadcast();
                    } catch (IOException ex) {
                    }
                }
            }
        };

        logicRunnable = new Runnable() {
            @Override
            public void run() {
                while (true) {
                    try {
                        deal(udp.read());
                    } catch (IOException ex) {
                    }
                }
            }
        };

        createBulletRunnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < max_amount_bullet; i++) {
                    Bullet bb = new Bullet(BULLET_MAXSIZE);
                    bulletList.add(bb);
                }

            }
        };

        moveBulletRunnable = new Runnable() {
            @Override
            public void run() {
                FPS fps = new FPS();
                while (true) {
                    fps.adjust(100);
                    int bulletSize = bulletList.size();
                    for (int i = 0; i < bulletSize; i++) {
                        Bullet b = bulletList.get(i);
                        if (b.x < BULLET_MAXSIZE && b.x >= -1 && b.y < BULLET_MAXSIZE && b.y >= -1) {
                            b.x += b.velocity[0];
                            b.y += b.velocity[1];
                        } else {
                            b = new Bullet(BULLET_MAXSIZE);
                            bulletList.set(i, b);
                        }
                    }

                }
            }
        };

    }

    public void deal(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        int id = dis.readShort();
        float x = dis.readFloat();
        float y = dis.readFloat();
        // logic 處理 
        System.out.println(id);

        if (!playerList.isEmpty()) {
            Player pp = playerList.get(id);
            pp.x += x * 10;
            pp.y += y * 10;
        }

    }

    public void broadcast() throws IOException {
        byte[] buffer = createEverything();
        if (!playerList.isEmpty()) {
            for (Integer x : playerList.keySet()) {
                playerList.get(x).sendWorld(buffer);
            }
        }
    }

    public byte[] createEverything() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        int playerCount = playerList.keySet().size();
        int bulletCount = bulletList.size();

        dos.writeShort(playerCount);
        dos.writeShort(bulletCount);

        if (playerCount > 0) {
            for (Integer x : playerList.keySet()) {
                Player pp = playerList.get(x);
                dos.writeShort(pp.getID());
                dos.writeFloat(pp.getX());
                dos.writeFloat(pp.getY());
                dos.writeShort(pp.getAge());
                dos.writeByte(pp.getStatus());

            }
        }

        if (bulletCount > 0) {
            for (int i = 0; i < bulletCount; i++) {
                Bullet b = bulletList.get(i);
                dos.writeFloat(b.getX());
                dos.writeFloat(b.getY());
                dos.writeByte(b.getStatus());
            }
        }
        return baos.toByteArray();
    }
    /*
    public class MoveBullet implements Runnable {

        Bullet bullet;
        int id;

        public MoveBullet(int id,Bullet bullet) {
            this.id = id;
            this.bullet = bullet;
        }

        @Override
        public void run() {
            
                FPS fps = new FPS();
            while(true){
                while (bullet.x < BULLET_MAXSIZE && bullet.x >= -1 && bullet.y < BULLET_MAXSIZE && bullet.y >= -1) {
                    
                    fps.adjust(40);
                    bullet.x += bullet.velocity[0];
                    bullet.y += bullet.velocity[1];
                }
                
                this.bullet = new Bullet(BULLET_MAXSIZE);
                Server.bulletList.set(id, bullet);
            }
        }
    }*/

}
