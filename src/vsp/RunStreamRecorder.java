package vsp;

import vsp.display.StreamRecorderDisplay;
import vsp.util.VspUtilities;

/**
 * The Stream Recording application.
 * @author Keith DeRuiter
 */
public class RunStreamRecorder {

    public static void main(String args[]) {
        // Initialize Logging.
        VspUtilities.initLogging();
        // Loads the appropriate VLC Native Library.
        VspUtilities.loadVlcNativeLibs();

        StreamRecorderDisplay srd = new StreamRecorderDisplay();
        srd.launch();
    }

}
