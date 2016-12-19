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
    public static HashMap<Integer, User> userList;
    public static ArrayList<Bullet> bulletList;
    public static ArrayList<Object[]> recordList;

    private Runnable broadcastRunnable;
    private Runnable createBulletRunnable;
    private Runnable logicRunnable;
    private Runnable moveBulletRunnable;
    private Runnable playerGrowRunnable;

    private int count = 1;
    private final int PORT = 20000;
    private final int MAX_WIDTH = 3600;
    private final int MAX_HEIGHT = 3600;
    private final int PLAYERSPEED = 10;
    private final byte INVINCIBLE_TIME = 5;
    private final int MAX_AMOUNT_BULLET = 200;
    private final int BULLET_RADIUS = 5;

    UDPSocket udp;

    public static void main(String args[]) throws IOException {
        new Server().establish();
    }

    public void establish() throws IOException {

        recordList = new ArrayList<>();
        bulletList = new ArrayList<>();
        userList = new HashMap<>();
        playerList = new HashMap<>();
        udp = new UDPSocket(PORT);

        implementsRunnable();
        new Thread(logicRunnable).start();
        new Thread(broadcastRunnable).start();
        new Thread(createBulletRunnable).start();
        new Thread(moveBulletRunnable).start();
        new Thread(playerGrowRunnable).start();

        ServerSocket serverSock = new ServerSocket(PORT);
        while (true) {
            Socket cSocket = serverSock.accept();
            User user = new User(count, cSocket, MAX_WIDTH, MAX_HEIGHT);
            userList.put(count++, user);
            new Thread(user).start();
            broadcastRank();
        }

    }

    // Judge whether this player bump into a bullet or not.
    public boolean conflict(float px, float py, float bx, float by, int age) {
        double x = Math.sqrt((px - bx) * (px - bx) + (py - by) * (py - by));
        double player_radius = 10 + age / 3;
        return x < player_radius+BULLET_RADIUS;
    }

    // Announcent Player die.
    public void deadAnnouncment(int id) {
        Player pp = playerList.get(id);
        User user = userList.get(id);
        user.deadAction();
        pp.setStatus((byte) 2);
        broadcastRank();
    }

    // Judge all players alive or not.
    public void logic() {
        for (Integer x : playerList.keySet()) {
            if (playerList.containsKey(x)) {
                Player pp = playerList.get(x);
                if (pp.getStatus() == 1) {
                    int age = pp.getAge();
                    float px = pp.getX();
                    float py = pp.getY();
                    int bulletCount = bulletList.size();
                    for (int i = 0; i < bulletCount; i++) {
                        Bullet bb = bulletList.get(i);
                        if (conflict(px, py, bb.getX(), bb.getY(), age)) {
                            deadAnnouncment(pp.getID());
                            break;
                        }
                    }
                }
            }
        }
    }

    // How to deal with input udp data ?
    public void deal(byte[] data) throws IOException {
        ByteArrayInputStream bais = new ByteArrayInputStream(data);
        DataInputStream dis = new DataInputStream(bais);
        int id = dis.readShort();
        float x = dis.readFloat();
        float y = dis.readFloat();

        if (!playerList.isEmpty() && playerList.containsKey(id)) {
            Player pp = playerList.get(id);
            byte status = pp.getStatus();
            if (status == 0 || status == 1) {
                if ((pp.x + x * PLAYERSPEED) < MAX_WIDTH && (pp.x + x * PLAYERSPEED) > 0) {
                    pp.x += x * PLAYERSPEED;
                } else if ((pp.x + x * PLAYERSPEED) >= MAX_WIDTH) {
                    pp.x = MAX_WIDTH;
                } else if ((pp.x + x * PLAYERSPEED) <= 0) {
                    pp.x = 0;
                }

                if ((pp.y + y * PLAYERSPEED) < MAX_HEIGHT && (pp.y + y * PLAYERSPEED) > 0) {
                    pp.y += y * PLAYERSPEED;
                } else if ((pp.y + y * PLAYERSPEED) >= MAX_HEIGHT) {
                    pp.y = MAX_HEIGHT;
                } else if ((pp.y + y * PLAYERSPEED) <= 0) {
                    pp.y = 0;
                }
            }
        }
        logic();
    }

    // Broadcast all Player's .
    public void broadcast() throws IOException {
        byte[] buffer = createEverything();
        if (!playerList.isEmpty()) {
            playerList.keySet().stream().forEach((x) -> {
                playerList.get(x).sendWorld(buffer);
            });
        }
    }

    // Broadcast Rank to all Player.
    public static void broadcastRank() {
        userList.keySet().stream().forEach((x) -> {
            userList.get(x).updateOnlinePlayer();
        });
    }

    // Create the World, give udp, broadcast to all player.
    public byte[] createEverything() throws IOException {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(baos);

        int playerCount = playerList.keySet().size();
        int bulletCount = bulletList.size();

        dos.writeShort(playerCount);
        dos.writeShort(bulletCount);

        if (playerCount > 0) {
            for (Integer x : playerList.keySet()) {
                if (playerList.containsKey(x)) {
                    Player pp = playerList.get(x);
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
                Bullet b = bulletList.get(i);
                dos.writeFloat(b.getX());
                dos.writeFloat(b.getY());
                dos.writeByte(b.getStatus());
            }
        }
        return baos.toByteArray();
    }

    // Thread Maker A_A
    public void implementsRunnable() {

        broadcastRunnable = new Runnable() {
            @Override
            public void run() {
                FPS fps = new FPS();
                while (true) {
                    try {
                        fps.adjust(30);
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
                for (int i = 0; i < MAX_AMOUNT_BULLET; i++) {
                    Bullet bb = new Bullet(MAX_WIDTH, MAX_HEIGHT);
                    bulletList.add(bb);
                }

            }
        };

        moveBulletRunnable = new Runnable() {
            @Override
            public void run() {
                FPS fps = new FPS();
                while (true) {
                    fps.adjust(50);
                    int bulletSize = bulletList.size();
                    for (int i = 0; i < bulletSize; i++) {
                        Bullet b = bulletList.get(i);
                        if (b.x <= MAX_WIDTH + BULLET_RADIUS  && b.x >= -BULLET_RADIUS && b.y <= MAX_HEIGHT +BULLET_RADIUS && b.y >= -BULLET_RADIUS) {
                            b.x += b.velocity[0];
                            b.y += b.velocity[1];
                        } else {
                            b = new Bullet(MAX_WIDTH, MAX_HEIGHT);
                            bulletList.set(i, b);
                        }
                    }

                }
            }
        };

        playerGrowRunnable = new Runnable() {
            @Override
            public void run() {
                FPS fps = new FPS();
                while (true) {
                    fps.adjust(1);
                    Server.playerList.forEach((Integer id, Player player) -> {
                        if (player.getStatus() < 2) {
                            player.age += 1;
                        }
                        if (player.age >= INVINCIBLE_TIME && player.getStatus() == 0) {
                            player.setStatus((byte) 1);
                        }
                    });
                }
            }
        };

    }

}
