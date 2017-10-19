
import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
 
public class ClientSocket {
    private BufferedReader reader;
    private PrintStream writer;
    private Socket client;
    public static int openPort = 10001; //我開的 port
    private String address = "127.0.0.1";// 連線的ip
    private int port = 20000;// 連線的port
    private String name;
    
    public ClientSocket(String name) {
        this.name = name;
        client = new Socket();
        
    }
    public void connect(){
        InetSocketAddress isa = new InetSocketAddress(this.address, this.port);
        try {
            client.connect(isa, openPort);
            this.reader = new BufferedReader(new InputStreamReader(client.getInputStream()));
            this.writer = new PrintStream(client.getOutputStream());
            // 送出字串
            String payload = name +"\t"+ openPort;
            writer.println(payload);
            writer.flush();
            
            openPort += 1;
 
        } catch (java.io.IOException e) {
            System.out.println( e.toString());
        }
    }
    
    public void send(String payload){
        writer.println(payload);
        writer.flush();
    }
    
    public String recv() throws IOException{
        return reader.readLine();
    }
    
    public void close() throws IOException{
            writer.close();
            writer = null;
            client.close();
            client = null;
    }
}