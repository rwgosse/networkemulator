/*
 * The objective of this project is to design and implement a basic 
 * Send-And-Wait protocol simulator. The protocol will be half-duplex 
 * and use sliding windows to send multiple packets between to hosts 
 * on a LAN with an “unreliable network” between the two hosts.
 */
package pkg7005finalproject;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.InetAddress;
import java.util.Properties;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;
import pkg7005finalproject.emulator.Emulator;
import pkg7005finalproject.models.ClientSettings;
import pkg7005finalproject.models.NetworkSettings;
import pkg7005finalproject.receiver.Receiver;
import pkg7005finalproject.sender.Sender;

/**
 *
 * @author Richard Gosse 2016
 */
public class Main {

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        // Determine through user input which module to run

        System.out.println("Working Directory = "
                + System.getProperty("user.dir"));

        System.out.println("COMP 7005 - Final Project");
        System.out.println("UDP Network Emulator & Clients");
        System.out.println("By Richard Gosse - A00246425");
        System.out.println("------------------------------");
        System.out.println("Please select which module to run: 1, 2 or 3");
        System.out.println("(1) - Network Emulator");
        System.out.println("(2) - Sender");
        System.out.println("(3) - Receiver");

        Scanner scanner = new Scanner(System.in);
        int module = scanner.nextInt();
        scanner.close();

        switch (module) {
            case 1: //emulator
                runEmulator();
                break;
            case 2: //sender
                runSender();
                break;
            case 3: //receiver
                runReceiver();
                break;
            default: // invalid selection
                System.out.println("Not a valid selection, please run program again.");
                System.exit(0);

        }
    } // end of main method

    /**
     * Creates an instance of the network emulator and starts it.
     */
    public static void runEmulator() {

        NetworkSettings networkSettings = loadEmulatorSettings();
        Emulator emulator = new Emulator(networkSettings);

        emulator.reportSettings();
        emulator.start();

    }

    /**
     * Creates & runs an instance of the sender client.
     */
    public static void runSender() {
        Sender sender = new Sender();
            sender.setClientSettings(loadClientSettings());
            sender.reportSettings();
            sender.start();
    }
        
        /**
         * Creates and runs an instance of the receiver client.
         */
    public static void runReceiver() {
        Receiver receiver = new Receiver();
        receiver.setClientSettings(loadClientSettings());
        receiver.reportSettings();
        receiver.start();

    }

    /**
     * load the network settings from the configuration file
     *
     * @return
     */
    private static NetworkSettings loadEmulatorSettings() {
        Properties incommingProperties = new Properties();
        InputStream inputStream = null;
        NetworkSettings networkSettings = new NetworkSettings();
        try {

            inputStream = new FileInputStream("networksettings.cfg");
            incommingProperties.load(inputStream);
            networkSettings.setDelay(Integer.parseInt(incommingProperties.getProperty("delay")));
            networkSettings.setDropRate(Integer.parseInt(incommingProperties.getProperty("dropRate")));
            networkSettings.setNetworkPort(Integer.parseInt(incommingProperties.getProperty("networkPort")));
            networkSettings.setReceiverAddress(InetAddress.getByName(incommingProperties.getProperty("receiverAddress")));
            networkSettings.setReceiverPort(Integer.parseInt(incommingProperties.getProperty("receiverPort")));
            networkSettings.setSenderAddress(InetAddress.getByName(incommingProperties.getProperty("senderAddress")));
            networkSettings.setSenderPort(Integer.parseInt(incommingProperties.getProperty("senderPort")));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }
            }
        }
        return networkSettings;
    }
    /**
     * 
     * @return 
     */
    private static ClientSettings loadClientSettings() {
        Properties incommingProperties = new Properties();
        InputStream inputStream = null;
        ClientSettings clientSettings = new ClientSettings();

        try {
            
            inputStream = new FileInputStream("ClientSettings.cfg");

            // load the configuration file
            incommingProperties.load(inputStream);

            clientSettings.setEmulatorAddress(InetAddress.getByName(incommingProperties.getProperty("emulatorAddress")));
            clientSettings.setEmulatorPort(Integer.parseInt(incommingProperties.getProperty("emulatorPort")));
            clientSettings.setSenderAddress(InetAddress.getByName(incommingProperties.getProperty("senderAddress")));
            clientSettings.setSenderPort(Integer.parseInt(incommingProperties.getProperty("senderPort")));
            clientSettings.setReceiverAddress(InetAddress.getByName(incommingProperties.getProperty("receiverAddress")));
            clientSettings.setReceiverPort(Integer.parseInt(incommingProperties.getProperty("receiverPort")));
            clientSettings.setWindowSize(Integer.parseInt(incommingProperties.getProperty("windowSize")));
            clientSettings.setMaxPackets(Integer.parseInt(incommingProperties.getProperty("maxPackets")));
            clientSettings.setMaxTimeout(Integer.parseInt(incommingProperties.getProperty("maxTimeout")));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } finally {
            if (inputStream != null) {

                try {
                    inputStream.close();
                } catch (IOException ex) {
                    Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
                }

            }
        }

        return clientSettings;
    }

}
