/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import static org.junit.Assert.*;
        
import CDC.*;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import server.Player;
import server.Server;
import server.User;

/**
 *
 * @author Neptune
 */
public class ConnectionTest {

    private ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private ByteArrayOutputStream errContent = new ByteArrayOutputStream();
    public static Server server;
    public static ClientSocket cs;
    private static int count = 1;
    public ConnectionTest() throws InterruptedException {
        
        System.setOut(new PrintStream(outContent));
        System.setErr(new PrintStream(errContent));
        server = new Server();
        cs = new ClientSocket("Tester");
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    server.main(new String[]{"20000"});
                } catch (IOException ex) {
                    Logger.getLogger(ConnectionTest.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }).start();
        Thread.sleep(100);
    }
    
    @Before
    public void setUp() {
        
    }
    
    @After
    public void tearDown() {
        System.setOut(null);
        System.setErr(null);
        
    }

     @Test
     public void TestConnection() {
        assertEquals("Constructing ... \nSuccess.\nOpen port :20000\n", outContent.toString() );
     }
     
     @Test
     public void TestUserInit() throws InterruptedException, IOException {
        cs.connect();
        assertEquals("id\t"+count,cs.recv());
        assertEquals("size\t"+Configure.MAX_WIDTH+"\t"+Configure.MAX_HEIGHT,cs.recv());
        Thread.sleep(100);
        User user = CDC.data.userList.get(count);
        assertNotEquals(user, null);
        count ++;
     }
     
     @Test
     public void TestPlayerInit() throws InterruptedException, IOException {
        cs.connect();
        Thread.sleep(100);
        Player player = CDC.data.playerList.get(count);
        assertEquals(player.getID(), count);
        assertEquals(player.getName(), "Tester");
        count ++;
     }
     
     
}
