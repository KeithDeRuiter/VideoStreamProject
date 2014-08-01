/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vsp;

import com.sun.jna.Native;
import com.sun.jna.NativeLibrary;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.SwingUtilities;
import uk.co.caprica.vlcj.binding.LibVlc;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;
import vsp.display.LiveDisplay;

/**
 *
 * @author Keith
 */
public class Main {

    public static final Properties PROPERTIES = new Properties();

    static {

        try {
            PROPERTIES.load(new FileReader("./config/application.properties"));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Main.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    public static void main(String[] args) {
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), PROPERTIES.getProperty("vlc.path"));
        Native.loadLibrary(RuntimeUtil.getLibVlcLibraryName(), LibVlc.class);

        final LiveDisplay liveDisplay = new LiveDisplay();
        liveDisplay.launch();

        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                liveDisplay.play(PROPERTIES.getProperty("mrl", "rtp://@226.0.67.1:4000"));
            }
        });
    }

}
