package vsp.processing;

import java.util.concurrent.TimeUnit;
import vsp.data.VideoSource;

/**
 * Class to manage the recording and frame extraction of Video streams from different sources.
 * 
 * @author Keith
 */
public class StreamRecordingManager {

    /** The source for this recording manager. */
    private final VideoSource m_source;
    
    /** The destination for frames recorded and ripped from the stream. */
    private final String m_frameDestinationDirectory;
    
    /** The number of recordings running simultaneously in the multiple overlapping recording scheme. */
    private final int m_numSimultaneousRecordings;
    
    /** The length of an individual snippet recording in the overlapping recording scheme. */
    private final TimeUnit m_snippetRecordingDuration;
    
    /** The frames per second rate to record at. */
    private final int m_fps;
    
    /** The quality level of the recording, specified by the implementation of {@code VideoProcessor} used. */
    private final int m_quality;

    /** The processor used to record and rip the individual frames. */
    private final VideoProcessor m_videoProcessor;
    
    /**
     * Constructs a new instance of {@code StreamRecordingManager}.
     * 
     * @param source The source of the video.
     * @param frameDestinationDirectory The directory that the ripped frames will be written to, which is created if it does not exist.
     * @param numSimultaneousRecordings The number of recordings running simultaneously for the overlapping recording.
     * @param snippetRecordingDuration The length of an individual snippet of recording in the overlapping recordings.
     */
    public StreamRecordingManager(VideoSource source, String frameDestinationDirectory, int numSimultaneousRecordings, TimeUnit snippetRecordingDuration) {
        //TODO- read default fps and quality from properties file
        this(source, frameDestinationDirectory, numSimultaneousRecordings, snippetRecordingDuration, 30, 3);
    }
    
    /**
     * Constructs a new instance of {@code StreamRecordingManager}.
     * 
     * @param source The source of the video.
     * @param frameDestinationDirectory The directory that the ripped frames will be written to, which is created if it does not exist.
     * @param numSimultaneousRecordings The number of recordings running simultaneously for the overlapping recording.
     * @param snippetRecordingDuration The length of an individual snippet of recording in the overlapping recordings.
     * @param fps The frames per second rate to record at.
     * @param quality The quality level of the recording, specified by the implementation of {@code VideoProcessor} used.
     */
    public StreamRecordingManager(VideoSource source, String frameDestinationDirectory, int numSimultaneousRecordings, TimeUnit snippetRecordingDuration, int fps, int quality) {
        this(source, frameDestinationDirectory, numSimultaneousRecordings, snippetRecordingDuration, fps, quality, null);
    }
    
    /**
     * Constructs a new instance of {@code StreamRecordingManager}.
     * 
     * @param source The source of the video.
     * @param frameDestinationDirectory The directory that the ripped frames will be written to, which is created if it does not exist.
     * @param numSimultaneousRecordings The number of recordings running simultaneously for the overlapping recording.
     * @param snippetRecordingDuration The length of an individual snippet of recording in the overlapping recordings.
     * @param fps The frames per second rate to record at.
     * @param quality The quality level of the recording, specified by the implementation of {@code VideoProcessor} used.
     * @param videoProcessor The video processor to use when recording and ripping frames.
     */
    public StreamRecordingManager(VideoSource source, String frameDestinationDirectory, int numSimultaneousRecordings,
            TimeUnit snippetRecordingDuration, int fps, int quality, VideoProcessor videoProcessor) {
        m_source = source;
        m_frameDestinationDirectory = frameDestinationDirectory;
        m_numSimultaneousRecordings = numSimultaneousRecordings;
        m_snippetRecordingDuration = snippetRecordingDuration;
        m_fps = fps;
        m_quality = quality;
        m_videoProcessor = videoProcessor;
        
        //TODO setup FFMPEG PROCESSOR
    }
    
    /** Start recording. */
    public void startRecording() {
        
    }
    
    /** Stop recording. */
    public void stopRecording() {
        
    }
    
}
