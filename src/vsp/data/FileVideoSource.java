package vsp.data;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Objects;

/**
 * a VideoSource that represents a File.
 * @author Keith
 */
public class FileVideoSource extends VideoSource {

    /** The path to the video file. */
    private String m_filepath;


    /**
     * Creates a new FileVideoSource.
     *
     * @param filepath the file path to the video source file.
     */
    public FileVideoSource(String filepath) {
        m_filepath = filepath;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int hash = super.hashCode();
        hash = 79 * hash + Objects.hashCode(this.m_filepath);
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
        final FileVideoSource other = (FileVideoSource) obj;
        if (!Objects.equals(this.m_filepath, other.m_filepath)) {
            return false;
        }

        return true;
    }

    /**
     * Returns the path to the file.
     
     */
    public String getFilepath() {
        return m_filepath;
    }

    /** {@inheritDoc} */
    @Override
    public String getMrl() {
        return m_filepath;
    }
}