/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vsp;

import vsp.display.StreamRecorderDisplay;
import vsp.util.VspUtilities;

/**
 *
 * @author Keith DeRuiter
 */
public class RunStreamRecorder {
    
    public static void main(String args[]) {
        // VLCJ Native Settings
        VspUtilities.initLogging();
        VspUtilities.loadVlcNativeLibs();
        
        StreamRecorderDisplay srd = new StreamRecorderDisplay();
        srd.launch();
    }
    
}
