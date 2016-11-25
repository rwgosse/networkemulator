/*
 * Sender Module
 * Sends packets to the receiver, via the emulator
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
 * @author Richard Gosse 2016
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
     * Constructor
     */
    public Sender() {
        super();
        this.sequenceNumber = 1;
        this.packetWindow = new ArrayList<Packet>();
    }

    /**
     * start sender operation
     */
    @Override
    public void start() {
        Helper.write("-- SENDER START --");
        this.initializeServer(this.clientSettings.getSenderPort());
        this.handShake();
        int packetsSent = 0;

        while (packetsSent < this.clientSettings.getMaxPackets()) { //remain open as long as there are packets to send
            this.generateWindowAndSend(); // create window, fill it and send packets
            this.waitingForAcks = true;
            this.setTimerForACKs();
            while (!this.packetWindow.isEmpty()) { // empty the window as acks are received & only when empty refill
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
        System.exit(0); //end
    }

    /**
     * Create the connection to receiver
     */
    private void handShake() {

        Packet packet = this.createPacket(SOT); // create start of transmission packet
        this.sendPacket(packet);// send the packet

        Helper.write("SENDER - " + Helper.generateClientPacketLog(packet, true));

        // wait for SOT reply
        try {
            Packet receiverResponse = Network.getPacket(this.listener);

            if (receiverResponse.getType() == SOT) {
                Helper.write("SENDER - " + Helper.generateClientPacketLog(packet, false));
            }

            Thread.sleep(2000); // wait before sending data
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

        Packet packet = this.createPacket(EOT); // create an EOT packet.
        this.sendPacket(packet); // send the packet

        Helper.write("SENDER - " + Helper.generateClientPacketLog(packet, true));
    }

    /**
     * create a packet
     *
     * @param packetType
     * @return
     */
    @Override
    protected Packet createPacket(int packetType) {
        return Helper.makePacket(this.clientSettings.getReceiverAddress().getHostAddress(),
                this.clientSettings.getReceiverPort(), this.clientSettings.getSenderAddress()
                .getHostAddress(), this.clientSettings.getSenderPort(), packetType,
                this.sequenceNumber, this.sequenceNumber, this.clientSettings.getWindowSize());
    }

    /**
     * Create window, fill with packets and send.
     */
    private void generateWindowAndSend() {
        for (int i = 1; i <= this.clientSettings.getWindowSize(); i++) {

            Packet packet = this.createPacket(DATA);// create data packet

            this.packetWindow.add(packet); // add to the window

            this.sendPacket(packet); // send packet

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
     * Wait for ACKs & remove acknowledged packets from window
     */
    private void receiveACKs() {
        try {
            this.listener.setSoTimeout(2000);
            while (this.packetWindow.size() != 0 && this.waitingForAcks) {

                Packet packet = Network.getPacket(Sender.this.listener);

                if (packet.getType() == ACK) {
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
     * Removed packet from window
     *
     * @param ackNum
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
        this.waitingForAcks = false;
    }

}
