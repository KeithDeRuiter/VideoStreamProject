package vsp.data;

import java.util.Objects;
import java.util.UUID;

/**
 * A class that represents a Video Source.
 * @author adam
 */
public class VideoSource {

    /** The ID of this VideoSource. */
    private UUID m_id;

    /** The Name of this Video Source. */
    private String m_name;

    /** Creates a new instance of VideoSource. */
    public VideoSource() {
        this ("Unnamed Video Source");
    }

    /**
     * Creates a new instance of VideoSource.
     * @param name the name of this video source.  If null or empty, 'Unnamed Video Source' will be used as the name.
     */
    public VideoSource(String name) {
        m_id = UUID.randomUUID();
        if (name == null || name.isEmpty()) {
            name = "Unnamed Video Source";
        }
        m_name = name;
    }

    /**
     * Returns the Stream Source's name.
     * @return the name of this StreamSource.
     */
    public String getName() {
        return m_name;
    }

    /**
     * Returns the ID of this VideoSource.
     * @return the ID of this VideoSource.
     */
    public UUID getId() {
        return m_id;
    }

    /** {@inheritDoc} */
    @Override
    public int hashCode() {
        int hash = 3;
        hash = 47 * hash + Objects.hashCode(this.m_id);
        hash = 47 * hash + Objects.hashCode(this.m_name);
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
        final VideoSource other = (VideoSource) obj;
        if (!Objects.equals(this.m_id, other.m_id)) {
            return false;
        }
        if (!Objects.equals(this.m_name, other.m_name)) {
            return false;
        }
        return true;
    }
}