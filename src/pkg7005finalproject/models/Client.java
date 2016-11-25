/*
 * Abstract class to hold common variables & methids for Sender & Receiver Modules
 * 
 */
package pkg7005finalproject.models;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Richard Gosse 2016
 */
public abstract class Client {
    
    
    protected ClientSettings clientSettings;
    protected DatagramSocket listener;
    
    public Client() {}
    
    public abstract void start();
    protected abstract Packet createPacket(int packetType);
    
    /**
     * Initialize the Server
     */
    protected void initializeServer(int port)
    {
        try
        {
            this.listener = Network.createServer(port);
        } catch (SocketException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }

    }
    
     /**
     * Send a packet to the emulator.
     * 
     * @param packet the packet to send.
     */
    protected void sendPacket(Packet packet)
    {
        try
        {
            DatagramSocket socket = Network.createSocket();

            // send packet to the network emulator
            Network.sendPacket(socket, packet, this.clientSettings.getEmulatorAddress(),
                    this.clientSettings.getEmulatorPort());
        }
        catch (SocketException e)
        {
           // Log.d(e.getMessage());

            System.exit(0);
        
        } catch (IOException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
    
    /**
     * Enter Client Settings manually
     * 
     * @param emulatorAddress
     * @param networkPort
     * @param senderAddress
     * @param transmitterPort
     * @param receiverAddress
     * @param receiverPort
     * @param maxPacketsToSend
     * @param windowSize
     * @param maxTimeout 
     */
    private void setClientSettings(String emulatorAddress, int networkPort,
            String senderAddress, int transmitterPort, String receiverAddress,
            int receiverPort, int maxPacketsToSend, int windowSize, int maxTimeout)
    {
        try
        {
            this.clientSettings.setEmulatorAddress(InetAddress.getByName(emulatorAddress));
            this.clientSettings.setEmulatorPort(networkPort);
            this.clientSettings.setSenderAddress(InetAddress.getByName(senderAddress));
            this.clientSettings.setSenderPort(transmitterPort);
            this.clientSettings.setReceiverAddress(InetAddress.getByName(receiverAddress));
            this.clientSettings.setReceiverPort(receiverPort);
            this.clientSettings.setMaxPackets(maxPacketsToSend);
            this.clientSettings.setWindowSize(windowSize);
            this.clientSettings.setMaxTimeout(maxTimeout);
        } catch (UnknownHostException ex) {
            Logger.getLogger(Client.class.getName()).log(Level.SEVERE, null, ex);
        }
    
    }
    /**
     * Apply settings normally
     * @param clientSettings 
     */
    public void setClientSettings(ClientSettings clientSettings)
    {
        this.clientSettings = clientSettings;
    }
    
    /**
     * Prints configuration for the Client Module.
     */
    public void reportSettings()
    {
        System.out.println(clientSettings);
    }
            
    
            
}


