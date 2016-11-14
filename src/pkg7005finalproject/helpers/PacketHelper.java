/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg7005finalproject.helpers;

import pkg7005finalproject.models.Packet;

/**
 *
 * @author Richard
 */
public class PacketHelper {
   

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
            int acknowledgementNumber, int windowSize)
    {
        Packet packet = new Packet();

        switch (packetType)
        {
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
     * Generates a generic packet log for network module. This can be put on the screen or in the
     * log files.
     * 
     * @param packet the packet to generate the logs for
     * @param forwarded if the packet was forwarded or dropped.
     * 
     * @return a generic packet log
     */
    public static String generateNetworkPacketLog(Packet packet, boolean forwarded)
    {
        StringBuilder log = new StringBuilder();

        if (forwarded)
        {
            log.append("PACKET FORWARDED ");
        }
        else
        {
            log.append("PACKET DROPPED ");
        }

        log.append(PacketHelper.generatePacketDetails(packet));

        return log.toString();

    }

    /**
     * Generates a generic packet log for clients. This can be put on the screen or in the log
     * files.
     * 
     * @param packet the packet to generate the logs for
     * @param sending true if sending packet, false if received it.
     * 
     * @return a generic packet log
     */
    public static String generateClientPacketLog(Packet packet, boolean sending)
    {
        StringBuilder log = new StringBuilder();

        if (sending)
        {
            log.append("Sending   ");
        }
        else
        {
            log.append("Received  ");
        }

        log.append(PacketHelper.generatePacketDetails(packet));

        return log.toString();

    }

    /**
     * Generate client log for when resending (data or ACK) stuff.
     * 
     * @param packet the packet to generate the log for
     * @param resendingData true if resending data, false if resending ACK
     * 
     * @return log
     */
    public static String generateClientResendLog(Packet packet, boolean resendingData)
    {
        StringBuilder log = new StringBuilder();

        if (resendingData)
        {
            log.append("Sending       ");
        }
        else
        {
            log.append("Resending ACK ");
        }

        log.append(PacketHelper.generatePacketDetails(packet));

        return log.toString();
    }

    /**
     * Generate details for a packet.
     * 
     * @param packet the packet to generate details for
     * @return packet log
     */
    public static String generatePacketDetails(Packet packet)
    {
        StringBuilder log = new StringBuilder();

        log.append("Packet Type: ");

        switch (packet.getType())
        {
            case 1:
                log.append("SOT \t");

                break;

            case 2:
                log.append("DATA\t");
                log.append("Packet Number: " + packet.getSequenceNumber());

                break;

            case 3:
                log.append("ACK \t");
                log.append("ACK Number:    " + packet.getAcknumber());

                break;

            case 4:
                log.append("EOT \t");

                break;
        }

        return log.toString();

    }
    
}
