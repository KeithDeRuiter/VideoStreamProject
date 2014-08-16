package vsp;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import vsp.display.LiveDisplay;
import vsp.util.VspProperties;

/**
 * Main launcher class for the LiveDisplay.
 *
 * @author Keith
 */
public class RunLiveDisplay {

    /** A handle to VspProperties. */
    private static final VspProperties PROPERTIES = VspProperties.getInstance();

    // Set up some configuration data (logging, properties)
    static {
        // Set up logging to use properties file.
        System.setProperty("java.util.logging.config.file", "./data/config/logging.properties");
    }

    /**
     * Main method.  Creates and launches an instance of LiveDisplay.
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        // VLCJ Native Settings
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), PROPERTIES.getWindowsVlcPath());
        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

        // Create and launch display
        final LiveDisplay liveDisplay = new LiveDisplay();
        liveDisplay.launch();
    }
}