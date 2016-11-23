package Gamer;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Server;

public class User implements Runnable {

    private int OFFSET = 10;
    private int SIZE = 100;
    int id;
    int port;
    int age;
    String name;
    Player player;
    PrintStream writer;
    BufferedReader reader;
    Socket sock;

    public User(int id, Socket cSocket) {
        this.sock = cSocket;
        this.id = id;

        try {
            
            this.reader = new BufferedReader(new InputStreamReader(sock.getInputStream()));
            this.writer = new PrintStream(sock.getOutputStream());
            
        } catch (IOException ex) {
        }

        
        // Set Client id
        writer.println("id\t"+id+"\n");
        writer.flush();
        System.out.println(id);

            
       
            // ****************************************************
            // Get Client info
        try {
            String gesdft = reader.readLine();
            System.out.println(gesdft);
            String [] get = gesdft.split("\t");
            name = get[0];
            port = Integer.parseInt(get[1]);
        } catch (IOException ex) {
            Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        // ****************************************************
            
        /*
        // Start Connection
        Date now = new Date();
        
        System.out.println(now.toString() + "\nIP : " + ip + "\nName : " + name + "\nStart to Connect.\n");
        */
        String ip = cSocket.getInetAddress().getHostAddress();
        
        Server.playerList.put(id, new Player(id, ip, port, SIZE * (new Random().nextFloat()) + OFFSET, SIZE * (new Random().nextFloat()) + OFFSET));
    }
    
    
    private void setCorpseRecord(){
        this.age = Server.playerList.get(id).getAge();
    }
    
    private void deadAction(){
    }
    
    @Override
    public void run() {
        String message;
        try {
            while ((message = this.reader.readLine()) != null) {
                
                //************* 處理資料 ************//
                String ss[] = message.split(" ");
                //************* 處理資料 ************//
                
                switch(ss[0]){
                    case "DEAD" :
                        // send rank
                        setCorpseRecord();
                        deadAction();
                    case "EXIT" : 
                        // quit (disconnect)
                        //Server.playerList.get(id).status = 0;
                        Object x[] = {age,name};
                        Server.recordList.add(x);
                        
                        Server.playerList.remove(id);
                        
                        reader.close();
                        this.sock.close();
                        throw new Exception();
                }

            }
        } catch (Exception ex) {
            System.out.println( "ID: "+ id + " Name: "+name+" Disconnect.");
        }
    }

}
