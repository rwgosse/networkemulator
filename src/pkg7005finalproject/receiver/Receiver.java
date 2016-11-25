/*
 * Class to represent the receiver client
 * 
 * 
 */
package pkg7005finalproject.receiver;

import java.io.IOException;
import java.util.ArrayList;
import java.util.logging.Level;
import java.util.logging.Logger;
import pkg7005finalproject.helpers.Helper;
import pkg7005finalproject.models.Client;
import pkg7005finalproject.models.Network;
import pkg7005finalproject.models.Packet;

/**
 *
 * @author Richard Gosse
 */
public class Receiver extends Client {

    private int currentSequenceNumber;
    private ArrayList<Packet> ackedPacketList;
    private static final int SOT = 1;
    private static final int DATA = 2;
    private static final int ACK = 3;
    private static final int EOT = 4;

    /**
     * Constructor
     */
    public Receiver() {
        super();
        this.currentSequenceNumber = 0;
        this.ackedPacketList = new ArrayList<Packet>();
    }

    /**
     * Begins the receiver operation
     */
    @Override
    public void start() {
        Helper.write("-- RECEIVER START --");
        this.initializeServer(clientSettings.getReceiverPort());
        this.awaitHandshake();
        boolean connectionOpen = true;
        int totalPackets = 0;
        int totalDuplicates = 0;

        while (connectionOpen) { // stay open until EOT packet received

            Packet incommingPacket;
            try {
                incommingPacket = Network.getPacket(this.listener);

                switch (incommingPacket.getType()) {

                    case DATA: // data packet
                        // update current sequence number
                        this.currentSequenceNumber = incommingPacket.getSequenceNumber();
                        // craft and send ACK packet
                        Packet ackPacket = this.createPacket(ACK); // ack packet
                        this.sendPacket(ackPacket);

                        // if packet hasn't been ACK'ed before.
                        if (!this.isAcked(incommingPacket.getSequenceNumber())) {
                            totalPackets++;
                            Helper.write("RECEIVER - " + Helper.generateClientPacketLog(incommingPacket, false));
                            Helper.write("RECEIVER - " + Helper.generateClientPacketLog(ackPacket, true));
                        } else {
                            // ACK again - earlier ACK probably got lost
                            totalDuplicates++;
                            Helper.write("RECEIVER - " + Helper.generateClientResendLog(incommingPacket, false));
                        }

                        // add to list of ack'd packets
                        this.ackedPacketList.add(incommingPacket);
                        break;

                    case EOT: // end of transmission packet
                        Helper.write("RECEIVER - " + Helper.generateClientPacketLog(incommingPacket, false));
                        Helper.write("RECEIVER - " + "Total Packets Received: " + totalPackets);
                        Helper.write("RECEIVER - " + "Total Duplicate ACK's:  " + totalDuplicates);
                        connectionOpen = false;
                        break;

                }
            } catch (IOException ex) {
                Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
            }

        }
        Helper.write("RECEIVER - Transmission Complete");
        System.exit(0); // end 
    }

    /**
     * Create a packet
     * @param packetType
     * @return Packet 
     */
    @Override
    protected Packet createPacket(int packetType) {
        return Helper.makePacket(this.clientSettings.getSenderAddress()
                .getHostAddress(), this.clientSettings.getSenderPort(), this.clientSettings
                .getReceiverAddress().getHostAddress(), this.clientSettings.getReceiverPort(),
                packetType, this.currentSequenceNumber, this.currentSequenceNumber,
                this.clientSettings.getWindowSize());
    }

    /**
     * Await SOT packet, and establish connection 
     */
    public void awaitHandshake() {
        try {

            Packet incommingPacket = Network.getPacket(this.listener);

            if (incommingPacket.getType() == SOT) // start of transmission
            {
                Helper.write("RECEIVER - " + Helper.generateClientPacketLog(incommingPacket, false));

                Packet outGoingPacket = this.createPacket(1);
                this.sendPacket(outGoingPacket);

                Helper.write("RECEIVER - " + Helper.generateClientPacketLog(incommingPacket, true));
            } else {
                Helper.write("RECEIVER - " + Helper.generateClientPacketLog(incommingPacket, false));

                // not a start of transmission packet, this would be really odd
                this.awaitHandshake();
            }
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Has the packet been acknowledged already?
     * @param seqNum
     * @return boolean
     */
    private boolean isAcked(int seqNum) {
        for (int i = 0; i < this.ackedPacketList.size(); i++) {
            if (this.ackedPacketList.get(i).getSequenceNumber() == seqNum) {
                return true;
            }
        }

        return false;
    }

}
