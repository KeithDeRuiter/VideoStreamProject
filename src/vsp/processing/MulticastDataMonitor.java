package vsp.processing;

import java.io.IOException;
import java.net.DatagramPacket;
import java.net.InetAddress;
import java.net.MulticastSocket;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The multicast data monitor that watches a socket for data.  This is sort of a hack, it drops a lot
 * of a data on the floor and requires polling.  Better ideas would be appreciated.
 * @author adam
 */
public class MulticastDataMonitor implements Runnable {

    /** A logger. */
    private static final Logger LOGGER = Logger.getAnonymousLogger(MulticastDataMonitor.class.getName());

    /** The IP Address to listen on. */
    private final String m_ipAddress;

    /** The port to listen to. */
    private final int m_port;

    /** The system time the last packet was received. */
    private long m_timeOfLastPacketReceipt;


    /**
     * Creates a new instance of MulticastDataDetector.
     * @param ipAddress The address to listen on.
     * @param port The port to listen on.
     */
    public MulticastDataMonitor(String ipAddress, int port) {
        m_ipAddress = ipAddress;
        m_port = port;
        m_timeOfLastPacketReceipt = -1L;
    }


    /**
     * Listens for data and updates the last updated time when a packet is received.
     * @throws UnknownHostException if a connection is attempted to an unknown host.
     * @throws IOException if an error occurs reading from the socket.
     */
    private void listenForData() throws UnknownHostException, IOException {
        InetAddress group = InetAddress.getByName(m_ipAddress);
        MulticastSocket socket = new MulticastSocket(m_port);
        socket.joinGroup(group);
        byte[] buf = new byte[1500];
        DatagramPacket recv = new DatagramPacket(buf, buf.length);

        boolean listening = true;
        while(listening) {
            //Receive data, so we notice that data is coming over.  We don't actually receive the data here!
            //VLC actually grabs the data for us, this is just a "is data there" detection mechanism.
            socket.receive(recv);
            updateTime();
        }

        socket.leaveGroup(group);
    }

    /** Run the data detector. */
    @Override
    public void run() {
        try {
            listenForData();
        } catch (IOException ex) {
            LOGGER.log(Level.SEVERE, "Error listening for data.", ex);
        }
    }

    /**
     * Returns the last time a packet was updated.
     * @return the last time a packet was updated.
     */
    public synchronized long getLastPacketReceiptTime() {
        return m_timeOfLastPacketReceipt;
    }

    /** Updates the last updated time. */
    private synchronized void updateTime() {
        m_timeOfLastPacketReceipt = System.currentTimeMillis();
    }
}
