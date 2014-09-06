package vsp;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import vsp.display.VideoBroadcastDisplay;
import vsp.util.VspProperties;

/**
 * Main class for running the broadcaster.
 * @author adam
 */
public class VideoBroadcaster {

    /** A handle to VspProperties. */
    private static final VspProperties PROPERTIES = VspProperties.getInstance();

    /**
     * Main method.
     * @param blargs command line arguments.
     */
    public static void main(String blargs[]) {
        // VLCJ Native Settings
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), PROPERTIES.getWindowsVlcPath());
        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

        VideoBroadcastDisplay display = new VideoBroadcastDisplay();
        display.launch();
    }
}