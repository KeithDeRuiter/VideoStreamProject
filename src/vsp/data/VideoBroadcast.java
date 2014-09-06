package vsp.data;

import java.net.UnknownHostException;

/**
 * A class that represents a multi-cast video broadcast.
 * @author adam
 */
public class VideoBroadcast {

    private String m_file;
    private String m_ip;
    private int m_port;

    /**
     * Constructs a new VideoBroadcast.
     *
     * @param tsFile the file to broadcast.
     * @param ipAddress the multicast IP address to broadcast from.
     * @param port the port to broadcast from.
     */
    public VideoBroadcast(String tsFile, String ipAddress, int port) {
        if (tsFile == null) {
            throw new IllegalArgumentException("Param 'tsFile' cannot be null.");
        }
        if (tsFile.isEmpty()) {
            throw new IllegalArgumentException("Param 'tsFile' cannot be empty.");
        }
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Param 'port' must be between 0 and 65535.  Value was:  " + port);
        }
        m_ip = ipAddress;
        m_file = tsFile;
        m_port = port;
    }

    public String getTsFile() {
        return m_file;
    }

    public String getIpAddress() {
        return m_ip;
    }

    public int getPort() {
        return m_port;
    }
}