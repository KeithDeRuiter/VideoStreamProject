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
    private final String m_winVlcPath;

    /** Macintosh VLC Members. */
    private static final String MAC_VLC_PATH_PROPERTY = "mac.vlc.path";
    private static final String MAC_VLC_PATH_DEFAULT = "/usr/bin/vlc";
    private final String m_macVlcPath;

    /** Linux VLC Members. */
    private static final String NIX_VLC_PATH_PROPERTY = "nix.vlc.path";
    private static final String NIX_VLC_PATH_DEFAULT = "/usr/bin/vlc";
    private final String m_nixVlcPath;

    /** FFMPEG Location. */
    private static final String FFMPEG_PATH_PROPERTY = "ffmpeg.path";
    private static final String FFMPEG_PATH_DEFAULT = "./res/ffmpeg.exe";
    private final String m_ffmpegPath;

    /** Frame Recording storage directory. */
    private static final String RECORDING_LIBRARY_DIR_PROPERTY = "recording.library.dir";
    private static final String RECORDING_LIBRARY_DIR_DEFAULT = "./recordings/";
    private final String m_recordingLibraryDir;

    /** Frame Recording meta data file property. */
    private static final String FRAME_RECORDING_FILE_PROPERTY = "frame.recording.filename";
    private static final String FRAME_RECORDING_FILE_DEFAULT = "metadata.fr";
    private final String m_frameRecordingFileName;

    /** Scratch / Temp directory. */
    private static final String SCRATCH_DIR_PROPERTY = "scratch.dir";
    private static final String SCRATCH_DIR_DEFAULT = "./scratch/";
    private final String m_scratchDir;

    /** Recording FPS value. */
    private static final String RECORDING_FPS_PROPERTY = "default.recording.fps";
    private static final int RECORDING_FPS_DEFAULT = 30;
    private final int m_recordingFps;

    /** Recording quality value. */
    private static final String RECORDING_QUALITY_PROPERTY = "default.recording.quality";
    private static final int RECORDING_QUALITY_DEFAULT = 3;
    private final int m_recordingQuality;

    /** IP Address settings. */
    private static final String DEFAULT_IP_PROPERTY = "default.ip";
    private static final String DEFAULT_IP = "226.0.67.1";
    private final String m_defaultIp;

    private static final String PORT_PROPERTY = "default.port";
    private static final int PORT_DEFAULT = 44500;
    private final int m_port;


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
            m_winVlcPath = WIN_VLC_PATH_DEFAULT;
        } else {
            m_winVlcPath = winVlcPath;
        }

        // Mac VLC Path
        String macVlcPath = properties.getProperty(MAC_VLC_PATH_PROPERTY);
        if (macVlcPath == null){
            LOGGER.config("Error reading Mac VLC property from file (value was null), using default:  " + MAC_VLC_PATH_DEFAULT);
            m_macVlcPath = MAC_VLC_PATH_DEFAULT;
        } else {
            m_macVlcPath = macVlcPath;
        }

        // *Nix VLC Path
        String nixVlcPath = properties.getProperty(NIX_VLC_PATH_PROPERTY);
        if (nixVlcPath == null){
            LOGGER.config("Error reading Linux VLC property from file (value was null), using default:  " + NIX_VLC_PATH_DEFAULT);
            m_nixVlcPath = NIX_VLC_PATH_DEFAULT;
        } else {
            m_nixVlcPath = nixVlcPath;
        }

        // FFMPEG Path
        String ffmpegPath = properties.getProperty(FFMPEG_PATH_PROPERTY);
        if (ffmpegPath == null){
            LOGGER.config("Error reading FFMPEG path from file (value was null), using default:  " + FFMPEG_PATH_DEFAULT);
            m_ffmpegPath = FFMPEG_PATH_DEFAULT;
        } else {
            m_ffmpegPath = ffmpegPath;
        }

        // Frame Recording Directory
        String recordingLibraryDir = properties.getProperty(RECORDING_LIBRARY_DIR_PROPERTY);
        if (recordingLibraryDir == null){
            LOGGER.config("Error reading frame recording directory property (value was null), using default:  " + RECORDING_LIBRARY_DIR_DEFAULT);
            m_recordingLibraryDir = RECORDING_LIBRARY_DIR_DEFAULT;
        } else {
            m_recordingLibraryDir = recordingLibraryDir;
        }

        // Frame Recording Metadata file
        String frameRecordingFileName = properties.getProperty(FRAME_RECORDING_FILE_PROPERTY);
        if (frameRecordingFileName == null){
            LOGGER.config("Error reading frame recording file name property (value was null), using default:  " + FRAME_RECORDING_FILE_DEFAULT);
            m_frameRecordingFileName = FRAME_RECORDING_FILE_DEFAULT;
        } else {
            m_frameRecordingFileName = frameRecordingFileName;
        }

        // Scratch work directory
        String scratchDirectory = properties.getProperty(SCRATCH_DIR_PROPERTY);
        if (scratchDirectory == null){
            LOGGER.config("Error reading scratch directory property (value was null), using default:  " + SCRATCH_DIR_DEFAULT);
            m_scratchDir = SCRATCH_DIR_DEFAULT;
        } else {
            m_scratchDir = scratchDirectory;
        }

        // Recording fps value
        String recordingFps = properties.getProperty(RECORDING_FPS_PROPERTY);
        if (recordingFps == null){
            LOGGER.config("Error reading recording fps property (value was null), using default:  " + RECORDING_FPS_DEFAULT);
            m_recordingFps = RECORDING_FPS_DEFAULT;
        } else {
            m_recordingFps = Integer.valueOf(recordingFps);
        }

        // Recording fps value
        String defaultIp = properties.getProperty(DEFAULT_IP_PROPERTY);
        if (defaultIp == null){
            LOGGER.config("Error reading default IP address (value was null), using default:  " + DEFAULT_IP);
            m_defaultIp = DEFAULT_IP;
        } else {
            m_defaultIp = recordingFps;
        }

        // Recording quality value
        String recordingQuality = properties.getProperty(RECORDING_QUALITY_PROPERTY);
        if (recordingQuality == null){
            LOGGER.config("Error reading recording quality property (value was null), using default:  " + RECORDING_QUALITY_DEFAULT);
            m_recordingQuality = RECORDING_QUALITY_DEFAULT;
        } else {
            m_recordingQuality = Integer.valueOf(recordingQuality);
        }

        String port = properties.getProperty(PORT_PROPERTY);
        if (port == null){
            LOGGER.config("Error reading port property (value was null), using default:  " + PORT_DEFAULT);
            m_port = PORT_DEFAULT;
        } else {
            m_port = Integer.valueOf(port);
        }
    }

    /**
     * Returns the Windows VLC path configured in application.properties.
     * @return the Windows VLC path configured in application.properties.
     */
    public String getWindowsVlcPath() {
        return m_winVlcPath;
    }

    /**
     * Returns the Macintosh VLC path configured in application.properties.  This particular VLC installation is broken
     * (as of 10/1/2014), but once bugs in VLC are fixed may work in the future.
     * @return the Macintosh VLC path configured in application.properties.
     */
    public String getMacVlcPath() {
        return m_macVlcPath;
    }

    /**
     * Returns the Linux VLC path configured in application.properties.
     * @return the Linux VLC path configured in application.properties.
     */
    public String getLinuxVlcPath() {
        return m_nixVlcPath;
    }

    /**
     * Returns the FFMPEG executable's path.
     * @return the FFMPEG executable's path.
     */
    public String getFfmpegPath() {
        return m_ffmpegPath;
    }

    /**
     * Returns the directory that has the library of recordings.
     * @return the directory that has the library of recordings.
     */
    public String getRecordingLibraryDirectory() {
        return m_recordingLibraryDir;
    }

    /**
     * Returns the filename that should be used for the frame recording metadata file.
     * @return the filename that should be used for the frame recording metadata file.
     */
    public String getFrameRecordingFilename() {
        return m_frameRecordingFileName;
    }

    /**
     * Returns the name of a directory where temporary files generated during processing may be written.
     * @return the name of a directory where temporary files generated during processing may be written.
     */
    public String getScratchDirectory() {
        return m_scratchDir;
    }

    /**
     * Returns the default IP address that is used to broadcast/receive multicast data on.
     * @return the default IP address that is used to broadcast/receive multicast data on.
     */
    public String getDefaultIpAddress() {
        return m_defaultIp;
    }

    /**
     * Returns the port used to broadcast/receive multicast data on.
     * @return the port used to broadcast/receive multicast data on.
     */
    public int getPort() {
        return m_port;
    }

    /**
     * Returns the FPS to rip frames at.
     * @return the FPS to rip frames at.
     */
    public int getRecordingFps() {
        return m_recordingFps;
    }

    /**
     * Returns the quality value for frames ripped from the video stream.
     * @return the quality value for frames ripped from the video stream.
     */
    public int getRecordingQuality() {
        return m_recordingQuality;
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