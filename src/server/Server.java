package server;

import CDC.*;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    private static int port = 20000;
    private int count = 1;

    public static UDPSocket udp;

    public static void main(String args[]) throws IOException {
        if (args.length > 1) {
            port = Integer.parseInt(args[1]);
        }
        new Server().establish();
    }

    public void establish() throws IOException {

        udp = new UDPSocket(port);
        CDC.data = new Data();
        Logic logic = new Logic();
        Threads threads = new Threads();


        ServerSocket serverSock = new ServerSocket(port);
        while (true) {
            Socket cSocket = serverSock.accept();
            CDC.data.addPlayer(count++,cSocket);
            Logic.broadcastRank();
        }

    }


}
