package server;

import CDC.Data;
import CDC.Logic;
import CDC.Threads;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {



    private final int PORT = 20000;
    private int count = 1;

    public static UDPSocket udp;

    public static void main(String args[]) throws IOException {
        new Server().establish();
    }

    public void establish() throws IOException {

        udp = new UDPSocket(PORT);
        Data dd = new Data();
        Logic logic = new Logic();
        Threads threads = new Threads();


        ServerSocket serverSock = new ServerSocket(PORT);
        while (true) {
            Socket cSocket = serverSock.accept();
            CDC.Data.addPlayer(count++,cSocket);
            CDC.Logic.broadcastRank();
        }

    }


}
