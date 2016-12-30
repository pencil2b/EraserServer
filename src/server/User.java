package server;

import CDC.*;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

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
    private Socket sock;

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
        writer.println("id\t" + id);
        writer.flush();
        writer.println("size\t" + MaxWidth + "\t" + MaxHeight);
        writer.flush();

        // Get Client info
        try {
            String con = reader.readLine();
            String[] get = con.split("\t");
            name = get[0];
            port = Integer.parseInt(get[1]);
        } catch (IOException ex) {
        }

        // Start Connection
        ip = cSocket.getInetAddress().getHostAddress();

        Date now = new Date();
        System.out.println(now.toString() + "\nIP : " + ip + "\nName : " + name + "\nStart to Connect.\n");

        player = new Player(id, name, ip, port, MaxWidth * (new Random().nextFloat()) + OFFSET, MaxHeight * (new Random().nextFloat()) + OFFSET);
        CDC.data.playerList.put(id, player);

    }

    public void updateOnlinePlayer() {
        String list = "list\t";
        int playerCount = CDC.data.playerList.size();

        ArrayList<Integer[]> rlist = new ArrayList();

        int count = 0;
        for (Integer i : CDC.data.playerList.keySet()) {
            if (count++ >= playerCount) {
                break;
            }
            Player pp = CDC.data.playerList.get(i);
            if (pp.getStatus() < 2) {
                Integer temp[] = new Integer[2];
                temp[0] = pp.getID();
                temp[1] = pp.getAge();
                rlist.add(temp);
            } else {
            }
        }

        list += (rlist.size() + "\t");

        for (int i = 0; i < rlist.size(); i++) {
            for (int j = 0; j < rlist.size(); j++) {
                if (rlist.get(j)[1] < rlist.get(i)[1]) {
                    Integer temp[] = rlist.get(i);
                    rlist.set(i, rlist.get(j));
                    rlist.set(j, temp);
                }
            }
        }
        for (int i = 0; i < rlist.size(); i++) {
            int tempID = rlist.get(i)[0];
            list += tempID + "\t";
            list += CDC.data.playerList.get(tempID).getName() + "\t";
        }

        writer.println(list);
        writer.flush();
    }

    public void deadAction() {
        ArrayList<Object[]> nr = (ArrayList<Object[]>) CDC.data.recordList.clone();

        writer.println("die");
        Object obj[] = {this.id, this.name, CDC.data.playerList.get(id).getAge()};
        nr.add(obj);

        CDC.data.recordList = nr;
    }

    @Override
    public void run() {
        String message;
        while (!sock.isClosed()) {
            try {
                message = this.reader.readLine();
                if (message == null) {
                    sock.close();
                    continue;
                }
                switch (message) {
                    case "full":
                        writer.println(CDC.data.getRankList());
                        System.out.println("HAHAHA");
                        writer.flush();
                        break;
                    case "restart":
                        // send rank
                        this.player = new Player(id, name, ip, port, MaxWidth * (new Random().nextFloat()) + OFFSET, MaxHeight * (new Random().nextFloat()) + OFFSET);
                        
                        HashMap<Integer, Player> np = (HashMap<Integer, Player>) CDC.data.playerList.clone();
                        np.put(id, player);
                        CDC.data.playerList = np;
                        
                        Logic.broadcastRank();
                        break;
                }
            } catch (IOException ex) {
                Logger.getLogger(User.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        
        HashMap<Integer, Player> np = (HashMap<Integer, Player>) CDC.data.playerList.clone();
        ArrayList<Object[]> nr = (ArrayList<Object[]>) CDC.data.recordList.clone();
        Object x[] = {id, name, age};
        nr.add(x);
        np.remove(id);
        CDC.data.recordList = nr;
        CDC.data.playerList = np;

        try {
            Thread.sleep(500);
        } catch (InterruptedException ex) {
        }
        Logic.broadcastRank();
        System.out.println("ID: " + id + " Name: " + name + " Disconnect.");
        
        HashMap<Integer, User> nu = (HashMap<Integer, User>) CDC.data.userList.clone();
        nu.remove(id);
        CDC.data.userList = nu;
    }

}
