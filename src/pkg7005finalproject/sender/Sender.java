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
import pkg7005finalproject.helpers.Helper;
import pkg7005finalproject.models.Client;
import pkg7005finalproject.models.Network;
import pkg7005finalproject.models.Packet;

/**
 *
 * @author Richard Gosse
 */
public class Sender extends Client {

    private int sequenceNumber;
    private ArrayList<Packet> packetWindow;
    private Timer timer;
    private boolean waitingForAcks;
    
    private static final int SOT = 1;
    private static final int DATA = 2;
    private static final int ACK = 3;
    private static final int EOT = 4;

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
        Helper.write("-- SENDER START --");
        this.initializeServer(this.clientSettings.getSenderPort());
        this.handShake();
        int packetsSent = 0;

        while (packetsSent < this.clientSettings.getMaxPackets()) {
            this.generateWindowAndSend();
            this.waitingForAcks = true;
            this.setTimerForACKs();
            while (!this.packetWindow.isEmpty()) {
                if (!this.waitingForAcks) {
                    this.setTimerForACKs();
                    Helper.write("SENDER - Window Status: " + packetWindow.size()
                            + " packets remaining.");
                }
            }
            packetsSent += this.clientSettings.getWindowSize();

            Helper.write("SENDER - Packets Sent:      " + packetsSent);
            Helper.write("SENDER - Packets Remaining: "
                    + (this.clientSettings.getMaxPackets() - packetsSent));
        }

        this.sendEndOfTransmissionPacket();
        Helper.write("SENDER - Transmission Complete");
        System.exit(0);
    }

    /**
     * Create the connection to receiver
     */
    private void handShake() {

        Packet packet = this.createPacket(1);

        // send the packet
        this.sendPacket(packet);

        Helper.write("SENDER - " + Helper.generateClientPacketLog(packet, true));

        // wait for SOT packet from receiver
        try {
            Packet receiverResponse = Network.getPacket(this.listener);

            if (receiverResponse.getType() == 1) {
                Helper.write("SENDER - " + Helper.generateClientPacketLog(packet, false));
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

        Helper.write("SENDER - " + Helper.generateClientPacketLog(packet, true));
    }

    @Override
    protected Packet createPacket(int packetType) {
        return Helper.makePacket(this.clientSettings.getReceiverAddress().getHostAddress(),
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

            Helper.write("SENDER - " + Helper.generateClientPacketLog(packet, true));

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
                Helper.write("SENDER - " + "Resending " + Helper.generatePacketDetails(packet));
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
                    Helper.write("SENDER - " + Helper.generateClientPacketLog(packet, false));
                    Sender.this.removePacketFromWindow(packet.getAcknumber());
                }
            }
        } catch (SocketTimeoutException ex) {
            Helper.write("SENDER - " + "Socket Time Out");
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
