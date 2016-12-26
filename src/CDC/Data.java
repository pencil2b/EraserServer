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
    
    public static String getRankList(){
        String list = "full\t";
        ArrayList<Object[]> rlist;
        rlist = Data.recordList;
        int recordCount = rlist.size();
        list += (recordCount + "\t");
        
        for(int i=0; i<recordCount; i++){
            for(int j=0; j<recordCount; j++){
                if((Integer)rlist.get(j)[2]<=(Integer)rlist.get(i)[2]){
                    Object temp[] = rlist.get(i);
                    rlist.set(i,rlist.get(j));
                    rlist.set(j,temp);
                }
            }
        }
        for(int i=0; i<recordCount; i++){
            list += rlist.get(i)[0]+"\t";
            list += rlist.get(i)[1]+"\t";
            list += rlist.get(i)[2]+"\t";
        }
        return list;
    }
    
}
