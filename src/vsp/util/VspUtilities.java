package vsp.util;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import java.util.logging.Logger;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

/**
 *
 * @author adam
 */
public class VspUtilities {

    /** Initializes the logging configuration for a VSP application. */
    public static void initLogging() {
        System.setProperty("java.util.logging.config.file", "./config/logging.properties");
    }

    /** Loads the appropriate VLC Native libs for the current operating system. */
    public static void loadVlcNativeLibs() {
        String osName = System.getProperty("os.name");
        VspProperties props = VspProperties.getInstance();

        if (osName.toLowerCase().contains("windows")) {
            NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), props.getWindowsVlcPath());
            Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

        } else if (osName.toLowerCase().contains("linux")) {
            NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), props.getLinuxVlcPath());
            Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

        } else if (osName.toLowerCase().contains("mac")) {
            NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), props.getMacVlcPath());
            Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);
        } else {
            throw new UnsupportedOperationException("Unsupported Operating System:  '" + osName + "'");
        }

        Logger.getLogger(VspUtilities.class.getName()).config("Loaded libraries for:  " + osName);
    }
}