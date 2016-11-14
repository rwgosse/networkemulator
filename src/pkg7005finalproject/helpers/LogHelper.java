/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
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


/**
 *
 * @author Richard
 */
public class LogHelper {
    
    /**
     * Name of the log file.
     */
    public static String logFile = "logfile";
    
    /**
     * Simply logs to the command line and a file.
     * 
     * @param log the message to log.
     */
    public static void write(String log)
    {
        log = LogHelper.getTime() + " " + log;
        
        // print to screen for now
        System.out.println(log);

        PrintWriter printWriter = null;
        try {
            printWriter = new PrintWriter(new BufferedWriter(new FileWriter(LogHelper.logFile, true)));
        } catch (IOException ex) {
            Logger.getLogger(LogHelper.class.getName()).log(Level.SEVERE, null, ex);
        }
        printWriter.println(log);
        printWriter.close();
    }

    /**
     * Returns the current time and date.
     * 
     * @return current time and date
     */
    public static String getTime()
    {
        DateFormat df = new SimpleDateFormat("dd/MM/yy HH:mm:ss");
        Calendar calobj = Calendar.getInstance();

        return (df.format(calobj.getTime()));
    }

}
