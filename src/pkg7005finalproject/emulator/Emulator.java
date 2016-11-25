/*
 * This is the emulator module 
 * The Emulator receives packets from the sender, determines if that packet 
 * (data or ack) will be dropped to simulate network packet loss or if 
 * the packet will then be forwarded after a short delay to the receiver. 
 */
package pkg7005finalproject.emulator;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import pkg7005finalproject.helpers.Helper;
import pkg7005finalproject.models.*;

/**
 *
 * @author Richard Gosse
 */
public class Emulator {

    private NetworkSettings networkSettings;
    private static final int SOT = 1;
    private static final int DATA = 2;
    private static final int ACK = 3;
    private static final int EOT = 4;

    public Emulator(NetworkSettings networkSettings) {
        this.networkSettings = networkSettings;
    }

    /**
     * Start the Network Emulator
     */
    public void start() {
        Helper.write("-- EMULATOR START --");
        boolean active = true;

        //statistics for reporting
        int totalPackets = 0;
        int totalDropped = 0;
        int totalforwarded = 0;

        // open a udp socket to be used for receiving packets from sender & receiver
        DatagramSocket udpSocket = null;
        try {
            udpSocket = Network.createServer(networkSettings.getNetworkPort());
        } catch (SocketException ex) {
            Logger.getLogger(Emulator.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }

        while (active) { // emulator remains open until force killed
            try {
                Packet packet = Network.getPacket(udpSocket);
                totalPackets++;
                int type = packet.getType();
                if (type == SOT || type == EOT) { // allow control packets
                    Network.sendPacket(udpSocket, packet);
                    Helper.write("EMULATOR - " + "CONTROL PACKET FORWARDED - SEQ: " + packet.getSequenceNumber() );
                    totalforwarded++;

                } else {
                    if (getRandom() <= networkSettings.getDropRate()) { //determine if packet shall be dropped
                       // drop the packet
                        Helper.write("EMULATOR - " + Helper.generateNetworkPacketLog(packet, false));
                        totalDropped++;
                    } else {
                        // otherwise, wait then forward packet
                        Thread.sleep(this.networkSettings.getDelay());
                        Network.sendPacket(udpSocket, packet);
                        Helper.write("EMULATOR - " + Helper.generateNetworkPacketLog(packet, true));
                        totalforwarded++;
                    }
                }
                //report to terminal & log progress
               Helper.write("EMULATOR - " + "Total packets:           " + totalPackets);
               Helper.write("EMULATOR - " + "Total packets dropped:   " + totalDropped);
               Helper.write("EMULATOR - " + "Total packets forwarded: " + totalforwarded);
            } catch (IOException ex) {
                Logger.getLogger(Emulator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (ClassNotFoundException ex) {
                Logger.getLogger(Emulator.class.getName()).log(Level.SEVERE, null, ex);
            } catch (InterruptedException ex) {
                Logger.getLogger(Emulator.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    /**
     * Generate a random number between 1 and 100. Used in calculating if a
     * packet is dropped or not.
     *
     * @return int
     */
    private int getRandom() {
        Random random = new Random();
        int drop = random.nextInt((100 - 1) + 1) + 1;
        return drop;
    }

    /**
     * Send current settings to the terminal.
     */
    public void reportSettings() {
        System.out.println(networkSettings);
    }

}
