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
import pkg7005finalproject.helpers.LogHelper;
import pkg7005finalproject.helpers.PacketHelper;
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

    public Receiver() {
        super();
        this.currentSequenceNumber = 0;
        this.ackedPacketList = new ArrayList<Packet>();
    }

    @Override
    public void start() {
        this.initializeServer(clientSettings.getReceiverPort());
        this.awaitHandshake();
        boolean connectionOpen = true;
        int totalPackets = 0;
        int totalDuplicates = 0;

        while (connectionOpen) {

            Packet incommingPacket;
            try {
                incommingPacket = Network.getPacket(this.listener);

                switch (incommingPacket.getType()) {

                    case 2: // data packet

                        // update current sequence number
                        this.currentSequenceNumber = incommingPacket.getSequenceNumber();

                        // craft and send ACK packet
                        Packet ackPacket = this.createPacket(3); // ack packet
                        this.sendPacket(ackPacket);

                        // if packet hasn't been ACK'ed before.
                        if (!this.isAcked(incommingPacket.getSequenceNumber())) {
                            totalPackets++;
                            LogHelper.write(PacketHelper.generateClientPacketLog(incommingPacket, false));
                             LogHelper.write(PacketHelper.generateClientPacketLog(ackPacket, true));
                        } else {
                            // ACKing again - earlier ACK probably got lost
                            totalDuplicates++;
                            LogHelper.write(PacketHelper.generateClientResendLog(incommingPacket, false));
                        }

                        // add to list of ack'ed packets
                        this.ackedPacketList.add(incommingPacket);
                        break;

                    case 4: // end of transmission packet
                        // listen for EOT

                       LogHelper.write(PacketHelper.generateClientPacketLog(incommingPacket, false));
                       LogHelper.write("Total Packets Received: " + totalPackets);
                       LogHelper.write("Total Duplicate ACK's:  " + totalDuplicates);
                        connectionOpen = false;
                        break;

                }
            } catch (IOException ex) {
                Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
            }

        }

    }

    @Override
    protected Packet createPacket(int packetType) {
        //throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.

        return PacketHelper.makePacket(this.clientSettings.getSenderAddress()
                .getHostAddress(), this.clientSettings.getSenderPort(), this.clientSettings
                .getReceiverAddress().getHostAddress(), this.clientSettings.getReceiverPort(),
                packetType, this.currentSequenceNumber, this.currentSequenceNumber,
                this.clientSettings.getWindowSize());
    }

    public void awaitHandshake() {
        try {

            Packet incommingPacket = Network.getPacket(this.listener);

            if (incommingPacket.getType() == 1) // start of transmission
            {
                LogHelper.write(PacketHelper.generateClientPacketLog(incommingPacket, false));

                // send SOT back to signify receive.
                Packet outGoingPacket = this.createPacket(1);
                this.sendPacket(outGoingPacket);

                LogHelper.write(PacketHelper.generateClientPacketLog(incommingPacket, true));
            } else {
               LogHelper.write(PacketHelper.generateClientPacketLog(incommingPacket, false));

                // ummm, not a start of transmission packet
                this.awaitHandshake();
            }
        } catch (ClassNotFoundException e) {
              LogHelper.write(e.getMessage());
        } catch (IOException ex) {
            Logger.getLogger(Receiver.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private boolean isAcked(int seqNum) {
        for (int i = 0; i < this.ackedPacketList.size(); i++) {
            if (this.ackedPacketList.get(i).getSequenceNumber() == seqNum) {
                return true;
            }
        }

        return false;
    }

}
