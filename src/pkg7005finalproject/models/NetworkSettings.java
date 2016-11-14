/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg7005finalproject.models;

import java.net.InetAddress;

/**
 *
 * @author Richard Gosse
 */
public class NetworkSettings {

    private InetAddress senderAddress;
    private int senderPort;

    private InetAddress receiverAddress;
    private int receiverPort;

    private int dropRate;
    private int delay;

    private int networkPort;

    /**
     *
     * @param senderAddress
     * @param senderPort
     * @param receiverAddress
     * @param receiverPort
     * @param dropRate
     * @param delay
     * @param networkPort
     */
    public NetworkSettings(InetAddress senderAddress, int senderPort, InetAddress receiverAddress, int receiverPort, int dropRate, int delay, int networkPort) {
        this.senderAddress = senderAddress;
        this.senderPort = senderPort;
        this.receiverAddress = receiverAddress;
        this.receiverPort = receiverPort;
        this.dropRate = dropRate;
        this.delay = delay;
        this.networkPort = networkPort;
    }

    public NetworkSettings() {
       // throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    public int getDelay() {
        return delay;
    }

    public int getDropRate() {
        return dropRate;
    }

    public int getNetworkPort() {
        return networkPort;
    }

    public InetAddress getReceiverAddress() {
        return receiverAddress;
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

    public void setDelay(int delay) {
        this.delay = delay;
    }

    public void setDropRate(int dropRate) {
        this.dropRate = dropRate;
    }

    public void setNetworkPort(int networkPort) {
        this.networkPort = networkPort;
    }

    public void setReceiverAddress(InetAddress receiverAddress) {
        this.receiverAddress = receiverAddress;
    }

    public void setReceiverPort(int receiverPort) {
        this.receiverPort = receiverPort;
    }

    public void setSenderAddress(InetAddress senderAddress) {
        this.senderAddress = senderAddress;
    }

    public void setSenderPort(int senderPort) {
        this.senderPort = senderPort;
    }

    @Override
    public String toString() {
        return "Network Settings: \nSender Address: " + senderAddress.getHostAddress() + " Port: " + senderPort
                + "\nReceiver Address: " + receiverAddress.getHostAddress() + " Port: " + receiverPort
                + "\nEmulator Port: " + networkPort + " Drop Rate: " + dropRate + " Delay: " + delay;
    }

}
