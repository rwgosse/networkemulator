/*
 * Model to represent settings used by sender & receiver, as loaded
 * from configuration file.
 * 
 */
package pkg7005finalproject.models;

import java.net.InetAddress;

/**
 *
 * @author Richard Gosse 2016
 */
public class ClientSettings {
    
    private InetAddress emulatorAddress;
    private int emulatorPort;
    
    private InetAddress senderAddress;
    private int senderPort;
    
    private InetAddress receiverAddress;
    private int receiverPort;
    
    private int maxPackets;
    private int windowSize;
    private int maxTimeOut;

    /**
     * Empty Constructor
     */
    public ClientSettings() {
    }

    /**
     * Constructor
     * 
     * @param emulatorAddress
     * @param emulatorPort
     * @param senderAddress
     * @param senderPort
     * @param receiverAddress
     * @param receiverPort
     * @param maxPackets
     * @param windowSize
     * @param maxTimeOut 
     */
    public ClientSettings(InetAddress emulatorAddress, int emulatorPort, InetAddress senderAddress, int senderPort, InetAddress receiverAddress, int receiverPort, int maxPackets, int windowSize, int maxTimeOut) {
        this.emulatorAddress = emulatorAddress;
        this.emulatorPort = emulatorPort;
        this.senderAddress = senderAddress;
        this.senderPort = senderPort;
        this.receiverAddress = receiverAddress;
        this.receiverPort = receiverPort;
        this.maxPackets = maxPackets;
        this.windowSize = windowSize;
        this.maxTimeOut = maxTimeOut;
    }

    public InetAddress getEmulatorAddress() {
        return emulatorAddress;
    }

    public int getEmulatorPort() {
        return emulatorPort;
    }

    public int getMaxPackets() {
        return maxPackets;
    }

    public int getMaxTimeOut() {
        return maxTimeOut;
    }

    public InetAddress getReceiverAddress() {
        return receiverAddress;
    }

    public void setEmulatorAddress(InetAddress emulatorAddress) {
        this.emulatorAddress = emulatorAddress;
    }

    public void setEmulatorPort(int emulatorPort) {
        this.emulatorPort = emulatorPort;
    }

    public void setMaxPackets(int maxPackets) {
        this.maxPackets = maxPackets;
    }

    public int getReceiverPort() {
        return receiverPort;
    }

    public InetAddress getSenderAddress() {
        return senderAddress;
    }

    public int getSenderPort() {
        return senderPort;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setReceiverAddress(InetAddress receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public void setReceiverPort(int receiverPort) {
        this.receiverPort = receiverPort;
    }

    public void setSenderPort(int senderPort) {
        this.senderPort = senderPort;
    }

    public void setSenderAddress(InetAddress senderAddress) {
        this.senderAddress = senderAddress;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    public void setMaxTimeout(int maxTimeout) {
        this.maxTimeOut = maxTimeout;
    }

    @Override
    public String toString() {
        return "Client Settings: \nEmulator Address: " + emulatorAddress.getHostAddress() + " Port: " + emulatorPort 
                + "\nSender Address: " + senderAddress.getHostAddress() + " Port: " + senderPort 
                + "\nReceiver Address: " + receiverAddress.getHostAddress() + " Port: " + receiverPort 
                + "\nMax # of Packets: " + maxPackets + " Window Size: " + windowSize + " Max Timeout: " + maxTimeOut;
    }
    

    
    
    
    
}
