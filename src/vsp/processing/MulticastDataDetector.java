/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vsp.processing;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Semaphore;
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

    /** The IP Address to listen on. */
    private String m_ipAddress;
    
    /** The port to listen to. */
    private int m_port;
    
    /** List of listeners for data on the stream. */
    private List<MulticastDataDetectionListener> m_multicastDataDetectionListeners;
    
    
    /** 
     * The semaphore used to control the flow of checking for available data.
     * When data is detected, a recording manager is launched, and this semaphore is acquired.
     * When recording has stopped, then it is released and listening continues.
     * It is a Semaphore instead of a Lock because a different thread (from the recording manager)
     * needs to release it than the one that acquired it.
     */
    private final Semaphore m_listeningSemaphore;

    /** List of MulticastDataListeners to notify when data starts or stops. */
    
    
    /**
     * Creates a new instance of MulticastDataDetector.
     * @param ipAddress The address to listen on.
     * @param port The port to listen on.
     */
    public MulticastDataDetector(String ipAddress, int port) {
        m_listeningSemaphore = new Semaphore(0);
        m_ipAddress = ipAddress;
        m_port = port;
        m_multicastDataDetectionListeners = new ArrayList<>();
    }
    
    
    /**
     * Listens for data.  Once data is detected, it starts a new {@link StreamRecordingManager} to record
     * and then blocks until recording is finished.
     * @throws UnknownHostException
     * @throws IOException 
     */
    private void listenForData() throws UnknownHostException, IOException {
        // join a Multicast group and send the group salutations

        InetAddress group = InetAddress.getByName(m_ipAddress);
        MulticastSocket s = new MulticastSocket(m_port);
        s.joinGroup(group);
        byte[] buf = new byte[1000];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);
        
        boolean listening = true;
        while(listening) {
            //Receive data, so we notice that data is coming over.  We don't actually receive the data here!
            //VLC actually grabs the data for us, this is just a "is data there" detection mechanism.
            s.receive(recv);
            notifyStreamStart();
            
            //Kick off processing
            String frameRecordingDirectory = VspProperties.getInstance().getRecordingLibraryDirectory();
            StreamRecordingManager srm = new StreamRecordingManager(new StreamVideoSource(m_ipAddress, m_port, "Source"), frameRecordingDirectory, 10L);
            srm.addRecordingCompleteListener(this);
            
            srm.startRecording();
            
            try {
                //Wait for stop
                Logger.getLogger(MulticastDataDetector.class.getName()).info("Stream started, waiting...");
                m_listeningSemaphore.acquire();
                Logger.getLogger(MulticastDataDetector.class.getName()).info("Stream stopped, continuing to listen");
            } catch (InterruptedException ex) {
                Logger.getLogger(MulticastDataDetector.class.getName()).log(Level.SEVERE, null, ex);
            }
            
            //Here, we have been told to continue listening, meaning the previous stream has ended
            notifyStreamStop();
        }
        
        s.leaveGroup(group);
    }
        
    /** Tells the detector the current stream ended, so go back to listening for new data streams. */
    synchronized public void continueListening() {
        //Logger.getLogger(MulticastDataDetector.class.getName()).info("Continuing to listen...");
        m_listeningSemaphore.release();
        //Logger.getLogger(MulticastDataDetector.class.getName()).info("Listening semaphore released");
    }
    
    /** {@inheritDoc} */
    @Override
    public void recordingComplete(VideoSource source) {
        continueListening();
    }
    
    /** Run the data detector. */
    @Override
    public void run() {
        try {
            listenForData();
        } catch (IOException ex) {
            Logger.getLogger(MulticastDataDetector.class.getName()).log(Level.SEVERE, null, ex);
        }
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

    @Override
    public void addMulticastDataDetectionListener(MulticastDataDetectionListener listener) {
        m_multicastDataDetectionListeners.add(listener);
    }

    @Override
    public void removeMulticastDataDetectionListener(MulticastDataDetectionListener listener) {
        m_multicastDataDetectionListeners.remove(listener);
    }
}
