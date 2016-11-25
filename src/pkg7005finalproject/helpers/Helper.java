/*
 * This module helps in the creation of packets & log file functions.
 */
package pkg7005finalproject.helpers;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import pkg7005finalproject.models.Packet;

/**
 *
 * @author Richard Gosse 2016
 */
public class Helper {

    public static String logFile = "logfile";

    /**
     * Generates a packet with the details provided.
     *
     * @param destinationAddress the destination address
     * @param destinationPort the destination port
     * @param sourceAddress the source address
     * @param sourcePort the source port
     * @param packetType the type of packet
     * @param sequenceNumber the sequence number of packet
     * @param acknowledgementNumber the ack number
     * @param windowSize the window size
     *
     * @return the generated packet
     */
    public static Packet makePacket(String destinationAddress, int destinationPort,
            String sourceAddress, int sourcePort, int packetType, int sequenceNumber,
            int acknowledgementNumber, int windowSize) {
        Packet packet = new Packet();

        switch (packetType) {
            case 1:
                // SOT
                packet.setData("Handshake");
                break;

            case 2:
                // DATA
                packet.setData("Packet Number: " + sequenceNumber);
                break;

            case 3:
                // ACK
                packet.setData("Acknowledgement Number: " + acknowledgementNumber);
                break;

            case 4:
                // EOT
                packet.setData("End of Transmission");
                break;
        }

        packet.setDestinationAddress(destinationAddress);
        packet.setDestinationPort(destinationPort);
        packet.setSourceAddress(sourceAddress);
        packet.setSourcePort(sourcePort);
        packet.setType(packetType);
        packet.setAcknumber(acknowledgementNumber);
        packet.setSequenceNumber(sequenceNumber);
        packet.setWindowSize(windowSize);

        return packet;
    }

    /**
     * Generates a packet log for the emulator
     * 
     *
     * @param packet the packet to generate the logs for
     * @param forwarded if the packet was forwarded or dropped.
     *
     * @return a generic packet log
     */
    public static String generateNetworkPacketLog(Packet packet, boolean forwarded) {
        StringBuilder log = new StringBuilder();

        if (forwarded) {
            log.append("PACKET FORWARDED ");
        } else {
            log.append("PACKET DROPPED ");
        }

        log.append(Helper.generatePacketDetails(packet));

        return log.toString();

    }

    /**
     * Generates a generic packet log for sender & receiver
     *
     * @param packet the packet to generate the logs for
     * @param sending true if sending packet, false if received
     *
     * @return a generic packet log
     */
    public static String generateClientPacketLog(Packet packet, boolean sending) {
        StringBuilder log = new StringBuilder();

        if (sending) {
            log.append("Sending   ");
        } else {
            log.append("Received  ");
        }

        log.append(Helper.generatePacketDetails(packet));

        return log.toString();

    }

    /**
     * Generate client log for when resending (data or ACK)
     *
     * @param packet 
     * @param resendingData 
     *
     * @return log
     */
    public static String generateClientResendLog(Packet packet, boolean resendingData) {
        StringBuilder log = new StringBuilder();

        if (resendingData) {
            log.append("Sending       ");
        } else {
            log.append("Resending  ACK ");
        }

        log.append(Helper.generatePacketDetails(packet));

        return log.toString();
    }

    /**
     * Generate details for a packet.
     *
     * @param packet the packet to generate details for
     * @return packet log
     */
    public static String generatePacketDetails(Packet packet) {
        StringBuilder log = new StringBuilder();

        log.append(" ");

        switch (packet.getType()) {
            case 1:
                log.append("SOT \t");
                break;

            case 2:
                log.append("DATA\t");
                log.append("SEQ #: " + packet.getSequenceNumber());
                break;

            case 3:
                log.append("ACK \t");
                log.append("ACK #: " + packet.getAcknumber());
                break;

            case 4:
                log.append("EOT \t");
                break;
        }

        return log.toString();

    }

    /**
     * Reports to the console and log file.
     *
     * @param log the message to log.
     */
    public static void write(String log) {
        log = Helper.getTime() + " " + log;
        System.out.println(log);
        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new BufferedWriter(new FileWriter(Helper.logFile, true)));
        } catch (IOException ex) {
            Logger.getLogger(Helper.class.getName()).log(Level.SEVERE, null, ex);
        }
        printWriter.println(log);
        printWriter.close();
    }

    /**
     * Returns the current time and date.
     *
     * @return current time and date
     */
    public static String getTime() {
        DateFormat dateFormat = new SimpleDateFormat("dd/MM/yy @ HH:mm:ss");
        Calendar calender = Calendar.getInstance();

        return (dateFormat.format(calender.getTime()));
    }

}
