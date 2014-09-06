package vsp;

import vsp.display.FrameRecordingPlayer;
import vsp.util.VspUtilities;

/**
 *
 * @author adam
 */
public class RunFrameRecordingPlayer {

    /**
     * Main method.  Creates and launches an instance of FrameRecordingPlayer.
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        // Initializes logging.
        VspUtilities.initLogging();

        // Create and launch display
        final FrameRecordingPlayer display = new FrameRecordingPlayer();
        display.launch();
    }
}