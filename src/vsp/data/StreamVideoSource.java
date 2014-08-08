package vsp.data;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * a VideoSource that represents a Stream.
 * @author adam
 */
public class StreamVideoSource extends VideoSource {

    /** The IP address of the video stream source. */
    private InetAddress m_host;

    /** The port of this video Stream source. */
    private int m_port;

    /**
     * Creates a new StreamSource.
     *
     * @param host the host name or IP address of the Stream Source.
     * @param port the port of the Stream Source.
     *
     * @throws UnknownHostException if the host name / IP address provided in 'host' is not valid.
     */
    public StreamVideoSource(String host, int port) throws UnknownHostException {
        this(host, port, "Unnamed Stream Source");
    }

    /**
     * Creates a new StreamSource.
     *
     * @param host the host name or IP address of the Stream Source.
     * @param port the port of the Stream Source.
     * @param name the name for this source.
     * @throws UnknownHostException  if the host name / IP address provided in 'host' is not valid.
     */
    public StreamVideoSource(String host, int port, String name) throws UnknownHostException {
        super(name);
        if (port < 1 || port > 65535) {
            throw new IllegalArgumentException("Port must be within valid range (1-65535)");
        }
        m_host = InetAddress.getByName(host);
        m_port = port;
    }

    /**
     * Returns the IP address as a String.
     * @return the IP address of the host as a String.
     */
    public String getIp() {
        return m_host.getHostAddress();
    }

    /**
     * Returns the port of the video for this StreamSource.
     * @return the port.
     */
    public int getPort() {
        return m_port;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 79 * hash + Objects.hashCode(this.m_host);
        hash = 79 * hash + this.m_port;
        return hash;
    }

    /** {@inheritDoc} */
    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        if (!super.equals(obj)){
            return false;
        }
        final StreamVideoSource other = (StreamVideoSource) obj;
        if (!Objects.equals(this.m_host, other.m_host)) {
            return false;
        }
        if (this.m_port != other.m_port) {
            return false;
        }
        return true;
    }

    /** {@inheritDoc} */
    @Override
    public String getMrl() {
        String ip = m_host.getHostAddress();
        String port = String.valueOf(m_port);
        return "rtp://@" + ip + ":" + port;
    }
}