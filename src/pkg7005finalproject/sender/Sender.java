/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg7005finalproject.sender;

import java.io.IOException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.Timer;
import java.util.TimerTask;
import java.util.logging.Level;
import java.util.logging.Logger;
import pkg7005finalproject.helpers.LogHelper;
import pkg7005finalproject.helpers.PacketHelper;
import pkg7005finalproject.models.Client;
import pkg7005finalproject.models.Network;
import pkg7005finalproject.models.Packet;

/**
 *
 * @author Richard
 */
public class Sender extends Client {

    /**
     * The current sequence number.
     *
     * Initialized to 1 in the constructor.
     */
    private int sequenceNumber;

    /**
     * The current packet window.
     */
    private ArrayList<Packet> packetWindow;

    /**
     * The timer for ACK's.
     */
    private Timer timer;

    /**
     * Boolean switch to wait for Ack's.
     */
    private boolean waitingForAcks;

    /**
     * Create a client, whose sole purpose is to send (transmit) to the
     * receiver.
     *
     * @param clientMode the client mode.
     */
    public Sender() {
        super();
        this.sequenceNumber = 1;
        this.packetWindow = new ArrayList<Packet>();
    }

    @Override
    public void start() {
        // initialize udp server
        this.initializeServer(this.clientSettings.getSenderPort());

        // take control of the channel
        this.sendTakeControlPacket();

        // total packets sent so far
        int packetsSent = 0;

        // once, all ack's arrive, empty window, and move onto the next window
        while (packetsSent < this.clientSettings.getMaxPackets()) {
            // generate packets for a window and send
            this.generateWindowAndSend();

            // we are now waiting for ack's.
            this.waitingForAcks = true;

            // set timer and after it's over, check for ACK's.
            this.setTimerForACKs();

            // wait for ack's for each packet
            while (!this.packetWindow.isEmpty()) {
                // set a timer only if we are not already waiting..no point invoking it again and
                // again
                if (!this.waitingForAcks) {
                    // set timer and after it's over, check for ACK's.
                    this.setTimerForACKs();

                    LogHelper.write("Window Status: " + packetWindow.size()
                            + " packets left in the current window!");
                }
            }

            // windowSize number of more packets have been sent
            packetsSent += this.clientSettings.getWindowSize();

            LogHelper.write("Sent Packets:      " + packetsSent);
            LogHelper.write("Remaining Packets: "
                    + (this.clientSettings.getMaxPackets() - packetsSent));
        }

        // when all window packets sent, send EOT
        this.sendEndOfTransmissionPacket();

        //exit
        System.exit(0);
    }

    /**
     * Send the packet to take control of the communication channel.
     */
    private void sendTakeControlPacket() {
        // create a SOT packet
        Packet packet = this.createPacket(1);

        // send the packet
        this.sendPacket(packet);

        LogHelper.write(PacketHelper.generateClientPacketLog(packet, true));

        // wait for SOT packet from receiver
        try {
            Packet receiverResponse = Network.getPacket(this.listener);

            if (receiverResponse.getType() == 1) {
                LogHelper.write(PacketHelper.generateClientPacketLog(packet, false));
            }

            // wait for 2 seconds before sending data packets.
            Thread.sleep(2000);
        } catch (IOException ex) {
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
        } catch (InterruptedException ex) {
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    /**
     * Send the packet to end the transmission.
     */
    private void sendEndOfTransmissionPacket() {
        // create an EOT packet.
        Packet packet = this.createPacket(4);

        // send the packet
        this.sendPacket(packet);

        LogHelper.write(PacketHelper.generateClientPacketLog(packet, true));
    }

    @Override
    protected Packet createPacket(int packetType) {
        return PacketHelper.makePacket(this.clientSettings.getReceiverAddress().getHostAddress(),
                this.clientSettings.getReceiverPort(), this.clientSettings.getSenderAddress()
                .getHostAddress(), this.clientSettings.getSenderPort(), packetType,
                this.sequenceNumber, this.sequenceNumber, this.clientSettings.getWindowSize());
    }

    /**
     * Generate packets for a full window.
     */
    private void generateWindowAndSend() {
        for (int i = 1; i <= this.clientSettings.getWindowSize(); i++) {
            // craft a data packet
            Packet packet = this.createPacket(2);

            // add it to the window
            this.packetWindow.add(packet);

            // send the packet
            this.sendPacket(packet);

            LogHelper.write(PacketHelper.generateClientPacketLog(packet, true));

            // increment the sequence number
            this.sequenceNumber++;
        }
    }

    /**
     * Wait for ACK's for the packets sent in the window.
     *
     * If an ACK isn't received within the timer, re-send packet.
     */
    private void ackTimeout() {
        this.stopTimerAndAckReceiverThread();

        // if packet window isn't empty, send all those packets again, and wait for ack's.
        if (!this.packetWindow.isEmpty()) {
            this.waitingForAcks = true;

            for (int i = 0; i < this.packetWindow.size(); i++) {
                Packet packet = this.packetWindow.get(i);
                this.sendPacket(packet);
                LogHelper.write("Resending " + PacketHelper.generatePacketDetails(packet));
            }

            this.setTimerForACKs();
        }

    }

    /**
     * Set timer and wait for ACKs.
     */
    private void setTimerForACKs() {
        this.timer = new Timer();

        this.timer.schedule(new TimerTask() {

            @Override
            public void run() {
                // call ackTimeout and check which packets have been ACK'ed.
                Sender.this.ackTimeout();
            }

        }, this.clientSettings.getMaxTimeOut());

        // receive ack's in the meantime
        this.receiveACKs();
    }

    /**
     * Wait for ACKs.
     */
    private void receiveACKs() {
        try {
            // can block for a maximum of 2 seconds
            this.listener.setSoTimeout(2000);

            /**
             * Scan while packet window size isn't 0. If 0, all packets have
             * been ACK'ed. AND Scan while the still waiting for ack's.
             */
            while (this.packetWindow.size() != 0 && this.waitingForAcks) {
                
                
                Packet packet = Network.getPacket(Sender.this.listener);
                
                

                //if an ACK received, log and remove from the window.
                if (packet.getType() == 3) {
                    LogHelper.write(PacketHelper.generateClientPacketLog(packet, false));
                    Sender.this.removePacketFromWindow(packet.getAcknumber());
                }
            }
        } catch (SocketTimeoutException ex) {
            LogHelper.write("Socket Time Out");
        } catch (SocketException ex) {
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);  
        } catch (IOException ex) {
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
        } catch (ClassNotFoundException ex) {
            Logger.getLogger(Sender.class.getName()).log(Level.SEVERE, null, ex);
        } 
    }

    /**
     * Checks all packets in the current window and removes the one whose
     * acknowledgement number is equal to the ACK number of the received packet.
     *
     * @param ackNum the acknowledgement number
     */
    private void removePacketFromWindow(int ackNum) {
        for (int i = 0; i < this.packetWindow.size(); i++) {
            if (this.packetWindow.get(i).getAcknumber() == ackNum) {
                this.packetWindow.remove(i);
            }
        }
    }

    /**
     * Stop the timer.
     */
    private void stopTimerAndAckReceiverThread() {
        this.timer.cancel();
        this.timer.purge();

        this.timer = null;

        // not waiting for ack's now.
        this.waitingForAcks = false;
    }

   

}
