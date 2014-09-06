package vsp.processing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;
import vsp.data.FileVideoSource;
import vsp.data.VideoSource;
import vsp.util.VspProperties;

/**
 * Class to manage the recording and frame extraction of Video streams from
 * different sources.
 *
 * @author Keith
 */
public class StreamRecordingManager implements RecordingCompleteNotifier {

    /** The source for this recording manager. */
    private final VideoSource m_source;

    /** The destination for all recordings. */
    private final String m_videoLibraryDirectory;
    
    /** The directory for all of the files associated with a given recording. */
    private String m_recordingDirectory;

    /** The name of the big .ts recorded file. */
    private String m_recordingFilename;

    /** The period at which recording blocks are sent to processing. */
    private final TimeUnit m_RecordingBlockFlushPeriod;

    /** The frames per second rate to record at. */
    private final int m_fps;

    /** The quality level of the recording, specified by the implementation of {@code VideoProcessor} used. */
    private final int m_quality;
    
    /** The executor service used to run threads in this class. */
    private final ScheduledExecutorService m_scheduledExecutorService;
    
    /** The media player used to receive the stream. */
    private final EmbeddedMediaPlayerComponent m_mediaPlayerComponent;
    
    /** The collection of all listeners to notify when recording ends. */
    private final Set<RecordingCompleteListener> m_listeners;
    
    /** The Future object representing the scheduled periodic task. */
    private Future m_periodicFuture;


    /**
     * The processor used to record and rip the individual frames.
     */
    private final static FfmpegVideoProcessor ffmpvp = new FfmpegVideoProcessor();

    /**
     * Constructs a new instance of {@code StreamRecordingManager}.
     *
     * @param source The source of the video.
     * @param recordingDirectory The directory that the stream will be recorded to, which is created if it does not exist.
     * @param snippetRecordingDuration The length of an individual snippet of
     * recording in the overlapping recordings.
     */
    public StreamRecordingManager(VideoSource source, String recordingDirectory, TimeUnit snippetRecordingDuration) {
        this(source, recordingDirectory, snippetRecordingDuration, VspProperties.getInstance().getRecordingFps(),
                VspProperties.getInstance().getRecordingQuality());
    }

    /**
     * Constructs a new instance of {@code StreamRecordingManager}.
     *
     * @param source The source of the video.
     * @param recordingDirectory The directory that the stream will be recorded to, which is created if it does not exist.
     * @param snippetRecordingDuration The length of an individual snippet of
     * recording in the overlapping recordings.
     * @param fps The frames per second rate to record at.
     * @param quality The quality level of the recording, specified by the
     * implementation of {@code VideoProcessor} used.
     */
    public StreamRecordingManager(VideoSource source, String recordingDirectory,
            TimeUnit snippetRecordingDuration, int fps, int quality) {
        m_source = source;
        m_videoLibraryDirectory = recordingDirectory;
        m_RecordingBlockFlushPeriod = snippetRecordingDuration;
        m_fps = fps;
        m_quality = quality;
        
        m_scheduledExecutorService = Executors.newScheduledThreadPool(4);
        m_mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        m_listeners = new HashSet<>();
        m_periodicFuture = null;
 
        File recordingDir = new File(m_videoLibraryDirectory);
        recordingDir.mkdirs();
    }
    
    
    /**
     * Starts recording. 
     */
    public void startRecording() {
        String mediaUrl = m_source.getMrl();
        m_recordingFilename = m_source.getName() + "-" + System.currentTimeMillis();
        m_recordingDirectory = m_videoLibraryDirectory + "/" + m_recordingFilename;
        String tsFilePath = m_recordingDirectory + "/" + m_recordingFilename + ".ts";
        File recordingDirectoryFile = new File(m_recordingDirectory);
        recordingDirectoryFile.mkdirs();
        String[] options = {":sout=#standard{mux=ts,access=file,dst=" + tsFilePath + "}"};
        m_mediaPlayerComponent.getMediaPlayer().playMedia(mediaUrl, options);
        
        launchPeriodicProcessor(tsFilePath);
    }

    /**
     * Launches the periodic processor to go and rip frames from the stored file with FFMPEG.
     * @param tsFilePath The path to the transport stream file that should be processed.
     */
    private void launchPeriodicProcessor(String tsFilePath) {
        Runnable r = new PeriodicProcessorRunnable(tsFilePath);
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        m_periodicFuture = ses.scheduleAtFixedRate(r, 0L, 10L, TimeUnit.SECONDS);
    }

    /**
     * Stops recording.
     */
    public void stopRecording() {
        m_mediaPlayerComponent.getMediaPlayer().stop();
        for(RecordingCompleteListener l : m_listeners) {
            l.recordingComplete(m_source);
        }
        if(m_periodicFuture != null) {
            m_periodicFuture.cancel(false);
        }
    }

    /** {@inheritDoc} */
    @Override
    public void addRecordingCompleteListener(RecordingCompleteListener listener) {
        m_listeners.add(listener);
    }

    /** {@inheritDoc} */
    @Override
    public void removeRecordingCompleteListener(RecordingCompleteListener listener) {
        m_listeners.remove(listener);
    }
    
    
    
    /**
     * Class launched to periodically read a file and process its data to
     * frames.
     */
    private class PeriodicProcessorRunnable implements Runnable {

        /** The filepath of the big source file we are pulling frames from. */
        private final String m_sourceFilepath;
        
        /** The cursor index into the file we are reading data from. */
        private int cursor = 0;

        /**
         * Constructs a new instance of PeriodicProcessorRunnable.
         *
         * @param sourceFilepath The full path to the file to process and pull frames from (path to .ts).
         */
        public PeriodicProcessorRunnable(String sourceFilepath) {
            m_sourceFilepath = sourceFilepath;
        }
        
        @Override
        public void run() {
            //System.out.println("RUNNING");

            //Read In the file to rip frames from
            File f = new File(m_sourceFilepath);
            FileInputStream fis = null;
            try {
                fis = new FileInputStream(f);
            } catch (FileNotFoundException ex) {
                ex.printStackTrace();
            }

            int length = (int) f.length();
            byte[] bytes = new byte[length - cursor];

            try {
                //System.out.println("Reading file... cursor = " + cursor + " length = " + length + " L-C = " + (length - cursor));
                fis.skip(cursor);
                fis.read(bytes, 0, length - cursor);
            } catch (IOException ex) {
                ex.printStackTrace();
            }

            cursor = (int) length;

            //Start FFMPEG to rip frames
            Runnable ffmpegRunnable = new Runnable() {
                @Override
                public void run() {
                    FileVideoSource source = new FileVideoSource(m_sourceFilepath);
                    try {
                        File recordingDir = new File(m_recordingDirectory);
                        ffmpvp.ripFrames(source, 30, 3, recordingDir.getAbsolutePath(), 0, 0);
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
            };
            new Thread(ffmpegRunnable).start();
        }
    };

}
