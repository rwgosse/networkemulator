/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg7005finalproject.models;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;




/**
 *
 * @author Richard
 */
public class Network {
    
    public static DatagramSocket createServer(int port) throws SocketException {
        return new DatagramSocket(port);
        
    }
    
    
    
    public static Packet getPacket(DatagramSocket socket) throws IOException, ClassNotFoundException {
        byte[] dataBytes = new byte[1024];
        DatagramPacket datagramPacket = new DatagramPacket(dataBytes, dataBytes.length);
       
      
        socket.receive(datagramPacket);

        dataBytes = datagramPacket.getData();

        ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(dataBytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteArrayInputStream);

        Packet packet = (Packet) objectInputStream.readObject();
        return packet;
    }
    
     /**
     * Creates a socket on any available port.
     * 
     * @return 
     */
    public static DatagramSocket createSocket() throws SocketException
    {
        return new DatagramSocket();
    }
    
    public static void sendPacket(DatagramSocket socket, Packet packet) throws IOException
    {
        InetAddress destinationAddress = InetAddress.getByName(packet.getDestinationAddress());
        sendPacket(socket, packet, destinationAddress, packet.getDestinationPort());
    }
    
    public static void sendPacket(DatagramSocket socket, Packet packet,
            InetAddress destinationAddress, int destinationPort) throws IOException
    {
        byte[] dataBytes = new byte[1024];

        ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);

        objectOutputStream.writeObject(packet);
        objectOutputStream.close();

        dataBytes = byteArrayOutputStream.toByteArray();

        DatagramPacket datagramPacket =
                new DatagramPacket(dataBytes, dataBytes.length, destinationAddress, destinationPort);

        socket.send(datagramPacket);

    }
    
}
