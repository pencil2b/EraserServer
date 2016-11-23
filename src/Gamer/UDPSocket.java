/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package Gamer;

import java.io.IOException;
import java.net.*;
import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author dorian
 */
public class UDPSocket {
    
    private InetAddress ip;
    private int port;
    private DatagramSocket socket;
    
    public UDPSocket(String remoteIp, int remotePort) {
        try {
            ip = InetAddress.getByName(remoteIp);
        } catch (UnknownHostException ex) {
        }
        port = remotePort;
        try {
            socket = new DatagramSocket(0);
        } catch (SocketException ex) {
            Logger.getLogger(UDPSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void write(byte[] data) {
        DatagramPacket packet = new DatagramPacket(data, data.length, ip, port);
        try {
            socket.send(packet);
        } catch (IOException ex) {
            Logger.getLogger(UDPSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public byte[] read() {
        byte[] buffer = new byte[4096];
        DatagramPacket packet = new DatagramPacket(buffer, buffer.length);
        try {
            socket.receive(packet);
        } catch (IOException ex) {
            Logger.getLogger(UDPSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
        return Arrays.copyOf(buffer, packet.getLength());
    }
    
}
