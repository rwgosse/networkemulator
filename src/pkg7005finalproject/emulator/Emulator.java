/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg7005finalproject.emulator;

import java.io.IOException;
import java.net.DatagramSocket;
import java.net.SocketException;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import pkg7005finalproject.helpers.LogHelper;
import pkg7005finalproject.helpers.PacketHelper;
import pkg7005finalproject.models.*;

/**
 *
 * @author Richard Gosse
 */
public class Emulator {

    private NetworkSettings networkSettings;

    public Emulator(NetworkSettings networkSettings) {
        this.networkSettings = networkSettings;
    }

    /**
     * Start the Network Emulator
     */
    public void start() {
        boolean active = true;

        //statistics for reporting
        int totalPackets = 0;
        int totalDropped = 0;
        int totalforwarded = 0;

        DatagramSocket udpSocket = null;
        try {
            udpSocket = Network.createServer(networkSettings.getNetworkPort());
        } catch (SocketException ex) {
            Logger.getLogger(Emulator.class.getName()).log(Level.SEVERE, null, ex);
            System.exit(0);
        }

        while (active) {
            try {
                Packet packet = Network.getPacket(udpSocket);
                totalPackets++;
                int type = packet.getType();

                if (type == 1 || type == 4) { // allow control packets
                    Network.sendPacket(udpSocket, packet);
                    LogHelper.write("CONTROL PACKET FORWARDED - SEQ: " + packet.getSequenceNumber() );
                    totalforwarded++;

                } else {
                    if (getRandom() <= networkSettings.getDropRate()) {
                       // LogHelper.write("PACKET DROPPED - SEQ: " + packet.getSequenceNumber() );
                        LogHelper.write(PacketHelper.generateNetworkPacketLog(packet, false));
                        totalDropped++;
                    } else {
                        Thread.sleep(this.networkSettings.getDelay());

                        Network.sendPacket(udpSocket, packet);

                        LogHelper.write(PacketHelper.generateNetworkPacketLog(packet, true));
                        totalforwarded++;
                    }
                }

                LogHelper.write("Total packets:           " + totalPackets);
                LogHelper.write("Total packets dropped:   " + totalDropped);
                LogHelper.write("Total packets forwarded: " + totalforwarded);
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
     * @return
     */
    private int getRandom() {
        Random random = new Random();
        int drop = random.nextInt((100 - 1) + 1) + 1;
        return drop;
    }

    /**
     *
     */
    public void reportSettings() {
        System.out.println(networkSettings);
    }

}