/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg7005finalproject.emulator;

import java.util.Random;
import pkg7005finalproject.models.NetworkSettings;

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
        throw new UnsupportedOperationException("emulator start - Not supported yet.");
    }
    
    /**
     * Generate a random number between 1 and 100.
     * Used in calculating if a packet is dropped or not.
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
