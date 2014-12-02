package vsp.processing;

import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import vsp.data.StreamVideoSource;
import vsp.data.VideoSource;
import vsp.util.VspProperties;

/**
 *
 * @author Keith DeRuiter
 */
public class MulticastDataDetector implements Runnable, RecordingCompleteListener, MulticastDataDetectionNotifier {

    /** A logger. */
    private static final Logger LOGGER = Logger.getAnonymousLogger(MulticastDataDetector.class.getName());

    /** List of MulticastDataListeners to notify when data starts or stops. */
    private List<MulticastDataDetectionListener> m_multicastDataDetectionListeners;

    /** The IP Address to listen on. */
    private final String m_ipAddress;

    /** The port to listen to. */
    private final int m_port;

    /** A monitor to check the multicast socket for data. */
    private MulticastDataMonitor m_monitor;

    /** The number of milliseconds to wait before alerting that the data stream has stopped. */
    private static final long TIMEOUT = 12000;

    /** A boolean indicating if the stream has already been reported as started. */
    private boolean m_streamActive = false;


    /**
     * Creates a new instance of MulticastDataDetector.
     * @param ipAddress The address to listen on.
     * @param port The port to listen on.
     */
    public MulticastDataDetector(String ipAddress, int port) {
        m_ipAddress = ipAddress;
        m_port = port;
        m_multicastDataDetectionListeners = new ArrayList<>();
        m_monitor = new MulticastDataMonitor(ipAddress, port);
    }

    /** Run the data detector. */
    @Override
    public void run() {
        m_monitor.run();
        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
        executor.schedule(new Runnable() {
                @Override
                public void run() {
                    long now = System.currentTimeMillis();
                    long lastPacket = m_monitor.getLastPacketReceiptTime();
                    if ((now - lastPacket) > TIMEOUT) {
                        if (!m_streamActive) {
                            try {
                                //Kick off processing
                                String frameRecordingDirectory = VspProperties.getInstance().getRecordingLibraryDirectory();
                                StreamRecordingManager srm = new StreamRecordingManager(new StreamVideoSource(m_ipAddress, m_port, "Source"), frameRecordingDirectory, 10L);
                                srm.startRecording();

                                // Notify listeners of stream start.
                                notifyStreamStart();
                                m_streamActive = true;
                            } catch (UnknownHostException ex) {
                                LOGGER.log(Level.SEVERE, "Error connecting stream recording manager.", ex);
                            }


                        }
                    } else {
                        m_streamActive = false;
                        notifyStreamStop();
                    }
                }
            },
            0L,
            TimeUnit.MILLISECONDS);
    }

    /** {@inherit} */
    @Override
    public void recordingComplete(VideoSource source) {
        m_streamActive = false;
        notifyStreamStop();
    }

    /** Notifies all multicast data detection listeners that the stream has started. */
    private void notifyStreamStart() {
        Logger.getLogger(MulticastDataDetector.class.getName()).info("Notifying Stream Start");

        for(MulticastDataDetectionListener l : m_multicastDataDetectionListeners) {
            l.streamStarted();
        }
    }

    /** Notifies all multicast data detection listeners that the stream has stopped. */
    private void notifyStreamStop() {
        Logger.getLogger(MulticastDataDetector.class.getName()).info("Notifying Stream Stop");
        
        for(MulticastDataDetectionListener l : m_multicastDataDetectionListeners) {
            l.streamEnded();
        }
    }

    /** {@inheritDoc} */
    @Override
    public void addMulticastDataDetectionListener(MulticastDataDetectionListener listener) {
        m_multicastDataDetectionListeners.add(listener);
    }

    /** {@inheritDoc} */
    @Override
    public void removeMulticastDataDetectionListener(MulticastDataDetectionListener listener) {
        m_multicastDataDetectionListeners.remove(listener);
    }
}
