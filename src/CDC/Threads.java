/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CDC;

import static CDC.Logic.logic;
import server.Player;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.FPS;

/**
 *
 * @author Neptune
 */
public class Threads {
    
    private Runnable broadcastRunnable;
    private Runnable createBulletRunnable;
    private Runnable logicRunnable;
    private Runnable moveBulletRunnable;
    private Runnable movePlayerRunnable;
    private Runnable outRunnable;
    private Runnable updatePlayerVeocityRunnable;
    private Runnable playerGrowRunnable;

    public Threads(){
        implementsRunnable();
        new Thread(logicRunnable).start();
        new Thread(broadcastRunnable).start();
        new Thread(createBulletRunnable).start();
        new Thread(moveBulletRunnable).start();
        new Thread(playerGrowRunnable).start();
        new Thread(movePlayerRunnable).start();
        new Thread(outRunnable).start();
        new Thread(updatePlayerVeocityRunnable).start();
    }
    
    // 製作各種執行緒
    public void implementsRunnable() {

        updatePlayerVeocityRunnable = new Runnable(){
            @Override
            public void run() {
                while (true) {
                    try {
                        CDC.Logic.updatePlayer(server.Server.udp.read());
                    } catch (IOException ex) {
                    }
                }
            }
        };
       movePlayerRunnable = new Runnable() {
            @Override
            public void run() {
                FPS fps = new FPS();
                while (true) {
                    try {
                        fps.adjust(120);
                        CDC.Logic.movePlayer();
                    } catch (IOException ex) {
                    }
                }
            }
        };
       
        outRunnable= new Runnable() {
            @Override
            public void run() {
                FPS fps = new FPS();
                while (true) {
                    fps.adjust(30);
                    CDC.Logic.logic();
                }
            }
        };
        
        broadcastRunnable = new Runnable() {
            @Override
            public void run() {
                FPS fps = new FPS();
                while (true) {
                    try {
                        fps.adjust(30);
                        CDC.Logic.broadcast();
                    } catch (IOException ex) {
                    }
                }
            }
        };

        createBulletRunnable = new Runnable() {
            @Override
            public void run() {
                for (int i = 0; i < Configure.MAX_AMOUNT_BULLET; i++) {
                    Bullet bb = new Bullet(Configure.MAX_WIDTH, Configure.MAX_HEIGHT);
                    CDC.Data.bulletList.add(bb);
                }

            }
        };

        moveBulletRunnable = new Runnable() {
            @Override
            public void run() {
                FPS fps = new FPS();
                while (true) {
                    fps.adjust(120);
                    int bulletSize = CDC.Data.bulletList.size();
                    for (int i = 0; i < bulletSize; i++) {
                        Bullet b = CDC.Data.bulletList.get(i);
                        if (b.x <= Configure.MAX_WIDTH + Configure.BULLET_OFFSET  && b.x >= -Configure.BULLET_OFFSET && b.y <= Configure.MAX_HEIGHT +Configure.BULLET_OFFSET && b.y >= -Configure.BULLET_OFFSET) {
                            b.x += b.velocity[0];
                            b.y += b.velocity[1];
                        } else {
                            b = new Bullet(Configure.MAX_WIDTH, Configure.MAX_HEIGHT);
                            CDC.Data.bulletList.set(i, b);
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
                    CDC.Data.playerList.forEach((Integer id, Player player) -> {
                        if (player.getStatus() < 2) {
                            player.age += 1;
                        }
                        if (player.age >= Configure.INVINCIBLE_TIME && player.getStatus() == 0) {
                            player.setStatus((byte) 1);
                        }
                    });
                }
            }
        };

    }
}
