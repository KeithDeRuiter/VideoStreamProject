package vsp;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import vsp.display.LiveDisplay;

/**
 * Main launcher class for the LiveDisplay.
 *
 * @author Keith
 */
public class RunLiveDisplay {

    /** The properties for this application. */
    public static final Properties PROPERTIES = new Properties();

    /** The Properties file location. */
    private static final String PROPERTIES_FILE = "./config/application.properties";

    /** Logger for this file. */
    private static final Logger LOGGER = Logger.getLogger(RunLiveDisplay.class.getName());

    // Set up some configuration data (logging, properties)
    static {
        // Set up logging to use properties file.
        System.setProperty("java.util.logging.config.file", "./data/config/logging.properties");

        // Load Properties Object.
        try {
            PROPERTIES.load(new FileReader(PROPERTIES_FILE));
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.SEVERE, "Failed to load properties file:  " + PROPERTIES_FILE + ".  Does file exist?", ex);
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error reading properties from file:  " + PROPERTIES_FILE + ".", ex);
        }
    }

    /**
     * Main method.  Creates and launches an instance of LiveDisplay.
     * @param args command line arguments.
     */
    public static void main(String[] args) {
        // VLCJ Native Settings
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), PROPERTIES.getProperty("windows.vlc.path"));
        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

        // Create and launch display
        final LiveDisplay liveDisplay = new LiveDisplay();
        liveDisplay.launch();
    }
}