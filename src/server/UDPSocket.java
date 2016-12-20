/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package server;

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
    
    private DatagramSocket socket;
    
    public UDPSocket(int port) {
        try {
            socket = new DatagramSocket(port);
        } catch (SocketException ex) {
            Logger.getLogger(UDPSocket.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    public void write(InetAddress ip, int port, byte[] data) {
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
