package vsp;

import vsp.display.FrameRecordingPlayer;

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
        // Create and launch display
        final FrameRecordingPlayer display = new FrameRecordingPlayer();
        display.launch();
    }
}