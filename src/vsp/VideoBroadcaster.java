package vsp;

import vsp.display.VideoBroadcastDisplay;
import vsp.util.VspUtilities;

/**
 * Main class for running the broadcaster.
 * @author adam
 */
public class VideoBroadcaster {

    /**
     * Main method.
     * @param blargs command line arguments.
     */
    public static void main(String blargs[]) {
        // Initializes logging.
        VspUtilities.initLogging();
        
        // Load Native Libs
        VspUtilities.loadVlcNativeLibs();

        VideoBroadcastDisplay display = new VideoBroadcastDisplay();
        display.launch();
    }
}