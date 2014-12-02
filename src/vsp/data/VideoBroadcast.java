package vsp.data;

/**
 * A simple data-bag that represents a multi-cast video broadcast.
 * @author adam
 */
public class VideoBroadcast {

    private String m_file;
    private String m_ip;
    private int m_port;

    /**
     * Constructs a new VideoBroadcast.
     *
     * @param videoFile the path to the file to be broadcast.
     * @param ipAddress the multicast IP address to broadcast from.
     * @param port the port to broadcast from.
     */
    public VideoBroadcast(String videoFile, String ipAddress, int port) {
        if (videoFile == null) {
            throw new IllegalArgumentException("Param 'tsFile' cannot be null.");
        }
        if (videoFile.isEmpty()) {
            throw new IllegalArgumentException("Param 'tsFile' cannot be empty.");
        }
        if (port < 0 || port > 65535) {
            throw new IllegalArgumentException("Param 'port' must be between 0 and 65535.  Value was:  " + port);
        }
        m_ip = ipAddress;
        m_file = videoFile;
        m_port = port;
    }

    /**
     * Returns the path to the video file that is to be broadcast.
     * @return the path to the video file that is to be broadcast.
     */
    public String getVideoFile() {
        return m_file;
    }

    /**
     * Returns the IP to broadcast on.
     * @return the IP to broadcast on.
     */
    public String getIpAddress() {
        return m_ip;
    }

    /**
     * Returns the port to broadcast on.
     * @return the port to broadcast on.
     */
    public int getPort() {
        return m_port;
    }
}