package server;

import server.Player;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.Random;
import server.Server;

public final class User implements Runnable {

    private int OFFSET = 10;
    int id;
    int port;
    int age;
    int MaxWidth;
    int MaxHeight;
    String ip;
    String name;
    Player player;
    PrintStream writer;
    BufferedReader reader;
    Socket sock;
    

    public User(int id, Socket cSocket, int MaxWidth, int MaxHeight) {
        this.MaxWidth = MaxWidth;
        this.MaxHeight = MaxHeight;
        this.sock = cSocket;
        this.id = id;

        try {
            
            this.reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            this.writer = new PrintStream(sock.getOutputStream());
            
        } catch (IOException ex) {
        }
        
        // Set Client id
        writer.println("id\t"+id);
        writer.flush();
        writer.println("size\t"+MaxWidth+"\t"+MaxHeight);
        writer.flush();
        
        // Get Client info
        try {
            String con = reader.readLine();
            String [] get = con.split("\t");
            name = get[0];
            port = Integer.parseInt(get[1]);
        } catch (IOException ex) {
        }
        
        // Start Connection
        ip = cSocket.getInetAddress().getHostAddress();
        
        Date now = new Date();
        System.out.println(now.toString() + "\nIP : " + ip + "\nName : " + name + "\nStart to Connect.\n");
        
        player = new Player(id,name, ip, port, MaxWidth * (new Random().nextFloat()) + OFFSET, MaxHeight * (new Random().nextFloat()) + OFFSET);
        CDC.Data.playerList.put(id, player);
        
    }
    
    
    
    public void updateOnlinePlayer(){
        String list = "list\t";
        int playerCount = CDC.Data.playerList.size();
        
        ArrayList<Integer[]> rlist = new ArrayList();
        
        int count = 0;
        for(Integer i : CDC.Data.playerList.keySet()){
            if(count++>= playerCount)break;
            Player  pp = CDC.Data.playerList.get(i);
            if(pp.getStatus()<2){
                Integer temp[] = new Integer[2];
                temp[0] = pp.getID();
                temp[1] = pp.getAge();
                rlist.add(temp);
            }else{}
        }
        
        list += (rlist.size() + "\t");
        
        
        for(int i=0; i<rlist.size(); i++){
            for(int j=0; j<rlist.size(); j++){
                if(rlist.get(j)[1]<rlist.get(i)[1]){
                    Integer temp[] = rlist.get(i);
                    rlist.set(i,rlist.get(j));
                    rlist.set(j,temp);
                }
            }
        }
        for(int i=0; i<rlist.size(); i++){
            int tempID = rlist.get(i)[0];
            list += tempID+"\t";
            list += CDC.Data.playerList.get(tempID).getName()+"\t";
        }
        
        writer.println(list);
        writer.flush();
    }
    
    
    
    
    public void deadAction(){
        writer.println("die");
        Object obj[] = {this.id,this.name,CDC.Data.playerList.get(id).getAge()};
        
        CDC.Data.recordList.add(obj);
    }
    
    public void getRankList(){
        String list = "full\t";
        ArrayList<Object[]> rlist = CDC.Data.recordList;
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
        System.out.println(list);
        writer.println(list);
        writer.flush();
    }
    
    
    @Override
    public void run() {
        String message;
        try {
            while ( !sock.isClosed() && (message = this.reader.readLine()) != null) {
                switch(message){
                    case "full" :
                        getRankList();
                        break;
                    case "restart" :
                        // send rank
                        this.player = new Player(id,name, ip, port, MaxWidth * (new Random().nextFloat()) + OFFSET, MaxHeight * (new Random().nextFloat()) + OFFSET);
                        CDC.Data.playerList.put(id, player);
                        CDC.Logic.broadcastRank();
                        break;
                    case "exit" : 
                        throw new Exception();
                }

            }
        } catch (Exception ex) {
            
        }
        CDC.Data.playerList.get(id).setStatus((byte)2);
        Object x[] = {age,name};
        CDC.Data.recordList.add(x);
        CDC.Data.playerList.remove(id);
        CDC.Data.userList.remove(id);
        try {
            writer.close();
            reader.close();
            sock.close();
        } catch (IOException ex1) {
        }
        CDC.Logic.broadcastRank();
        System.out.println( "ID: "+ id + " Name: "+name+" Disconnect.");
    }

}