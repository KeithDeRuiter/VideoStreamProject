package vsp.util;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Properties file for VideoStreamProject.
 * @author adam
 */
public class VspProperties {

    /** The Singleton instance of VspProperties. */
    private static VspProperties INSTANCE;

    /** A Logger. */
    private static final Logger LOGGER = Logger.getLogger(VspProperties.class.getName());

    /** Properties file location. */
    private static final String PROPERTIES_FILE = "./config/application.properties";

    /** Windows VLC Members. */
    private static final String WIN_VLC_PATH_PROPERTY = "windows.vlc.path";
    private static final String WIN_VLC_PATH_DEFAULT = "C:\\Program Files\\VideoLAN\\VLC";
    private String m_winVlcPath;

    /** Macintosh VLC Members. */
    private static final String MAC_VLC_PATH_PROPERTY = "mac.vlc.path";
    private static final String MAC_VLC_PATH_DEFAULT = "/usr/bin/vlc";
    private String m_macVlcPath;

    /** Linux VLC Members. */
    private static final String NIX_VLC_PATH_PROPERTY = "nix.vlc.path";
    private static final String NIX_VLC_PATH_DEFAULT = "/usr/bin/vlc";
    private String m_nixVlcPath;

    /** FFMPEG Location. */
    private static final String FFMPEG_PATH_PROPERTY = "ffmpeg.path";
    private static final String FFMPEG_PATH_DEFAULT = "./res/ffmpeg.exe";
    private String m_ffmpegPath;

    /** Frame Recording storage directory. */
    private static final String FRAME_RECORDING_DIR_PROPERTY = "frame.recording.dir";
    private static final String FRAME_RECORDING_DIR_DEFAULT = "./recordings/";
    private String m_frameRecordingDir;

    /** Frame Recording meta data file property. */
    private static final String FRAME_RECORDING_FILE_PROPERTY = "frame.recording.filename";
    private static final String FRAME_RECORDING_FILE_DEFAULT = "metadata.fr";
    private String m_frameRecordingFileName;

    /** Scratch / Temp directory. */
    private static final String SCRATCH_DIR_PROPERTY = "scratch.dir";
    private static final String SCRATCH_DIR_DEFAULT = "./scratch/";
    private String m_scratchDir;

    /** Private constructor, enforces Singleton pattern. */
    private VspProperties(){
        // Load Application Properties.
        Properties properties = new Properties();
        try {
            FileInputStream app = new FileInputStream(PROPERTIES_FILE);
            properties.load(app);
        } catch (FileNotFoundException ex) {
            LOGGER.log(Level.CONFIG, "Error loading application properties from file.  File not present.", ex);
        } catch (IOException ioex){
            LOGGER.log(Level.CONFIG, "Error loading application properties from file.", ioex);
        }

        // Windows VLC Path
        String winVlcPath = properties.getProperty(WIN_VLC_PATH_PROPERTY);
        if (winVlcPath == null){
            LOGGER.config("Error reading windows VLC property from file (value was null), using default:  " + WIN_VLC_PATH_DEFAULT);
        } else {
            m_winVlcPath = winVlcPath;
        }

        // Mac VLC Path
        String macVlcPath = properties.getProperty(MAC_VLC_PATH_PROPERTY);
        if (macVlcPath == null){
            LOGGER.config("Error reading Mac VLC property from file (value was null), using default:  " + MAC_VLC_PATH_DEFAULT);
        } else {
            m_macVlcPath = macVlcPath;
        }

        // *Nix VLC Path
        String nixVlcPath = properties.getProperty(NIX_VLC_PATH_PROPERTY);
        if (nixVlcPath == null){
            LOGGER.config("Error reading Linux VLC property from file (value was null), using default:  " + NIX_VLC_PATH_DEFAULT);
        } else {
            m_nixVlcPath = nixVlcPath;
        }

        // FFMPEG Path
        String ffmpegPath = properties.getProperty(FFMPEG_PATH_PROPERTY);
        if (ffmpegPath == null){
            LOGGER.config("Error reading FFMPEG path from file (value was null), using default:  " + FFMPEG_PATH_DEFAULT);
        } else {
            m_ffmpegPath = ffmpegPath;
        }

        // Frame Recording Directory
        String frameRecordingDir = properties.getProperty(FRAME_RECORDING_DIR_PROPERTY);
        if (frameRecordingDir == null){
            LOGGER.config("Error reading frame recording directory property (value was null), using default:  " + FRAME_RECORDING_DIR_DEFAULT);
        } else {
            m_frameRecordingDir = frameRecordingDir;
        }

        // Frame Recording Metadata file
        String frameRecordingFileName = properties.getProperty(FRAME_RECORDING_FILE_PROPERTY);
        if (frameRecordingFileName == null){
            LOGGER.config("Error reading frame recording file name property (value was null), using default:  " + FRAME_RECORDING_FILE_DEFAULT);
        } else {
            m_frameRecordingFileName = frameRecordingFileName;
        }
    }

    public String getWindowsVlcPath() {
        return m_winVlcPath;
    }

    public String getMacVlcPath() {
        return m_macVlcPath;
    }

    public String getNixVlcPath() {
        return m_nixVlcPath;
    }

    /**
     * Returns the FFMPEG executable's path.
     * @return the FFMPEG executable's path.
     */
    public String getFfmpegPath() {
        return m_ffmpegPath;
    }

    public String getFrameRecordingDir() {
        return m_frameRecordingDir;
    }

    public String getFrameRecordingFilename() {
        return m_frameRecordingFileName;
    }

    public String getScratchDirectory() {
        return m_scratchDir;
    }

    /**
     * Returns the Singleton instance of VspProperties.
     * @return the Singleton instance of VspProperties.
     */
    public static VspProperties getInstance() {
        if (INSTANCE == null){
            INSTANCE = new VspProperties();
        }
        return INSTANCE;
    }
}