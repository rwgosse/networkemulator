/*
 * Simple UDP packet 
 * 
 * 
 */
package pkg7005finalproject.models;

import java.io.Serializable;

/**
 *
 * @author Richard Gosse
 */
public class Packet implements Serializable{
    
    private int type; // 1 = SOT, 2 = Data, 3 = ACK, 4 = EOT
    private int sequenceNumber;
    private String data; // inconsequential string used as filler, just id's type and sequence #
    private int windowSize;
    private int ackNumber;
    
    private String sourceAddress; // where the packet originated
    private int sourcePort;
    
    private String destinationAddress; // where the packet is destined
    private int destinationPort;

    /**
     * Empty Constructor
     */
    public Packet() {
    }

    /**
     * Constructor
     * 
     * @param type
     * @param sequenceNumber
     * @param data
     * @param windowSize
     * @param ackNumber
     * @param sourceAddress
     * @param sourcePort
     * @param destinationAddress
     * @param destinationPort 
     */
    public Packet(int type, int sequenceNumber, String data, int windowSize, int ackNumber, String sourceAddress, int sourcePort, String destinationAddress, int destinationPort) {
        this.type = type;
        this.sequenceNumber = sequenceNumber;
        this.data = data;
        this.windowSize = windowSize;
        this.ackNumber = ackNumber;
        this.sourceAddress = sourceAddress;
        this.sourcePort = sourcePort;
        this.destinationAddress = destinationAddress;
        this.destinationPort = destinationPort;
    }

    public int getType() {
        return type;
    }

    public int getAcknumber() {
        return ackNumber;
    }

    public String getData() {
        return data;
    }

    public String getDestinationAddress() {
        return destinationAddress;
    }

    public int getDestinationPort() {
        return destinationPort;
    }

    public int getSequenceNumber() {
        return sequenceNumber;
    }

    public String getSourceAddress() {
        return sourceAddress;
    }

    public int getSourcePort() {
        return sourcePort;
    }

    public int getWindowSize() {
        return windowSize;
    }

    public void setAcknumber(int acknumber) {
        this.ackNumber = acknumber;
    }

    public void setData(String data) {
        this.data = data;
    }

    public void setDestinationAddress(String destinationAddress) {
        this.destinationAddress = destinationAddress;
    }
    
    public void setDestinationPort(int destinationPort) {
        this.destinationPort = destinationPort;
    }

    public void setSequenceNumber(int sequenceNumber) {
        this.sequenceNumber = sequenceNumber;
    }

    public void setSourceAddress(String sourceAddress) {
        this.sourceAddress = sourceAddress;
    }

    public void setSourcePort(int sourcePort) {
        this.sourcePort = sourcePort;
    }

    public void setType(int type) {
        this.type = type;
    }

    public void setWindowSize(int windowSize) {
        this.windowSize = windowSize;
    }

    @Override
    public String toString() {
        return "Packet: \nType=" + type + ", Sequence Number=" + sequenceNumber + ", windowSize="
                + windowSize + ", ackNum=" + ackNumber + ", data=" + data + ", destinationAddress="
                + destinationAddress + ", destinationPort=" + destinationPort + ", sourceAddress="
                + sourceAddress + ", sourcePort=" + sourcePort + "]";
    }
    
    

    
    
    
    
    
    
}
