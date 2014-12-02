package vsp;

import vsp.display.LiveDisplay;
import vsp.util.VspUtilities;

/**
 * Main launcher class for the LiveDisplay.
 * @author Keith
 */
public class RunLiveDisplay {

    /**
     * Main method.  Creates and launches an instance of LiveDisplay.
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        // Initializes logging.
        VspUtilities.initLogging();

        // Load Native Libs
        VspUtilities.loadVlcNativeLibs();

        // Create and launch display
        final LiveDisplay liveDisplay = new LiveDisplay();
        liveDisplay.launch();
    }
}