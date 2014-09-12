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
import java.util.concurrent.Semaphore;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;
import java.util.logging.Level;
import java.util.logging.Logger;
import vsp.data.StreamVideoSource;
import vsp.data.VideoSource;
import vsp.util.VspProperties;

/**
 *
 * @author Keith DeRuiter
 */
public class MulticastDataDetector implements Runnable, RecordingCompleteListener {

    /** The IP Address to listen on. */
    private String m_ipAddress;
    
    /** The port to listen to. */
    private int m_port;
    
    /** 
     * The semaphore used to control the flow of checking for available data.
     * When data is detected, a recording manager is launched, and this semaphore is acquired.
     * When recording has stopped, then it is released and listening continues.
     * It is a Semaphore instead of a Lock because a different thread (from the recording manager)
     * needs to release it than the one that acquired it.
     */
    private final Semaphore m_listeningSemaphore;

    
    /**
     * Creates a new instance of MulticastDataDetector.
     * @param ipAddress The address to listen on.
     * @param port The port to listen on.
     */
    public MulticastDataDetector(String ipAddress, int port) {
        m_listeningSemaphore = new Semaphore(0);
        m_ipAddress = ipAddress;
        m_port = port;
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
            //Receive data, so we notice that data is coming over    
            s.receive(recv);
            
            //Kick off processing
<<<<<<< HEAD
            String frameRecordingDirectory = VspProperties.getInstance().getFrameRecordingDir();
            StreamRecordingManager srm = new StreamRecordingManager(new StreamVideoSource(m_ipAddress, m_port, "Source"),
                    frameRecordingDirectory, 10L);
=======
            String frameRecordingDirectory = VspProperties.getInstance().getRecordingLibraryDirectory();
            StreamRecordingManager srm = new StreamRecordingManager(new StreamVideoSource(m_ipAddress, m_port, "Source"), frameRecordingDirectory, TimeUnit.MICROSECONDS);
>>>>>>> 212e9a348fe80de2ef4d18c63fd3bc1db8a15a91
            srm.startRecording();
            
            try {
                //Wait for stop
                m_listeningSemaphore.acquire();
            } catch (InterruptedException ex) {
                Logger.getLogger(MulticastDataDetector.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        
        s.leaveGroup(group);
    }
    
    /** Tells the detector to go back to listening for new data streams. */
    synchronized public void continueListening() {
        m_listeningSemaphore.release();
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

}
