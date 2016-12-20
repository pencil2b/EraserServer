/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package CDC;

import server.User;
import server.Player;
import java.net.Socket;
import java.util.ArrayList;
import java.util.HashMap;


public class Data {
    
    
    public static HashMap<Integer, Player> playerList;
    public static HashMap<Integer, User> userList;
    public static ArrayList<Bullet> bulletList;
    public static ArrayList<Object[]> recordList;
    
    public Data(){
        recordList = new ArrayList<>();
        bulletList = new ArrayList<>();
        userList = new HashMap<>();
        playerList = new HashMap<>();
    }
    
    public static void addPlayer(int count,Socket cSocket){
        User user = new User(count, cSocket, Configure.MAX_WIDTH, Configure.MAX_HEIGHT);
        CDC.Data.userList.put(count, user);
        new Thread(user).start();
    }
    
}
