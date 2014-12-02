package vsp.processing;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.headless.HeadlessMediaPlayer;
import vsp.data.FileVideoSource;
import vsp.data.FrameRecording;
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
    private final long m_recordingBlockFlushPeriod;

    /** The frames per second rate to record at. */
    private final int m_fps;

    /** The quality level of the recording, specified by the implementation of {@code VideoProcessor} used. */
    private final int m_quality;
    
    /** The media player used to receive the stream. */
    private final HeadlessMediaPlayer m_mediaPlayer;
    
    /** The collection of all listeners to notify when recording ends. */
    private final Set<RecordingCompleteListener> m_listeners;
    
    /** The Future object representing the scheduled periodic task. */
    private Future m_periodicFuture;

    /** The batch index for snippets as processing advances. */
    private int m_batchIndex;

    /** The executor used to start new threads. */
    ScheduledExecutorService m_ses;
    
    /**
     * Constructs a new instance of {@code StreamRecordingManager}.
     *
     * @param source The source of the video.
     * @param recordingLibraryDirectory The directory that the stream will be recorded to, which is created if it does not exist.
     * @param snippetRecordingDuration The length of an individual snippet of
     * recording in the overlapping recordings.
     */
    public StreamRecordingManager(VideoSource source, String recordingLibraryDirectory, long snippetRecordingDuration) {
        this(source, recordingLibraryDirectory, snippetRecordingDuration, VspProperties.getInstance().getRecordingFps(),
                VspProperties.getInstance().getRecordingQuality());
    }

    /**
     * Constructs a new instance of {@code StreamRecordingManager}.
     *
     * @param source The source of the video.
     * @param recordingLibraryDirectory The directory that the stream will be recorded to, which is created if it does not exist.
     * @param snippetRecordingDuration The length of an individual snippet of
     * recording in the overlapping recordings.
     * @param fps The frames per second rate to record at.
     * @param quality The quality level of the recording, specified by the
     * implementation of {@code VideoProcessor} used.
     */
    public StreamRecordingManager(VideoSource source, String recordingLibraryDirectory,
            long snippetRecordingDuration, int fps, int quality) {
        m_source = source;
        m_videoLibraryDirectory = recordingLibraryDirectory;
        m_recordingBlockFlushPeriod = snippetRecordingDuration;
        m_fps = fps;
        m_quality = quality;
        m_batchIndex = 0;
        m_ses = Executors.newSingleThreadScheduledExecutor();
        
        MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory();
        m_mediaPlayer = mediaPlayerFactory.newHeadlessMediaPlayer();
        m_listeners = new HashSet<>();
        m_periodicFuture = null;
 
        File recordingDir = new File(m_videoLibraryDirectory);
        recordingDir.mkdirs();
    }
    
    
    /**
     * Starts recording. 
     */
    public void startRecording() {
        Logger.getLogger(StreamRecordingManager.class.getName()).info("Starting Recording");
        
        String mediaUrl = m_source.getMrl();
        Logger.getLogger(StreamRecordingManager.class.getName()).info("Recording MRL: " + mediaUrl);
        long startTime = System.currentTimeMillis();
        m_recordingFilename = m_source.getName() + "-" + startTime;
        m_recordingDirectory = m_videoLibraryDirectory + "/" + m_recordingFilename + "/";
        String tsFilePath = m_recordingDirectory + m_recordingFilename + ".ts";
        File recordingDirectoryFile = new File(m_recordingDirectory);
        recordingDirectoryFile.mkdirs();
        String[] options = {":sout=#standard{mux=ts,access=file,dst=" + tsFilePath + "}"};
        boolean everythingIsOk = true;
        everythingIsOk = m_mediaPlayer.startMedia(mediaUrl, options);
        
        FrameRecording frameRecording = new FrameRecording(m_source.getId(), m_fps, tsFilePath,
                m_recordingDirectory, startTime, -1, "NAME");
        frameRecording.saveToFile(m_recordingDirectory + "/" + VspProperties.getInstance().getFrameRecordingFilename());
        
        launchPeriodicProcessor(tsFilePath);
    }

    /**
     * Launches the periodic processor to go and rip frames from the stored file with FFMPEG.
     * @param tsFilePath The path to the transport stream file that should be processed.
     */
    private void launchPeriodicProcessor(String tsFilePath) {
        Runnable r = new PeriodicProcessorRunnable(tsFilePath);
        m_periodicFuture = m_ses.scheduleAtFixedRate(r, 5L, m_recordingBlockFlushPeriod, TimeUnit.SECONDS);
    }

    /**
     * Stops recording.
     */
    public void stopRecording() {
        Logger.getLogger(StreamRecordingManager.class.getName()).info("Stopping Recording");

        m_mediaPlayer.stop();
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
        private int m_cursor = 0;

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
            Logger.getLogger(StreamRecordingManager.class.getName()).info("Running periodic processor");

            //Read In the file to rip frames from
            File f = new File(m_sourceFilepath);
            int length = 0;
            
            try(FileInputStream fis = new FileInputStream(f)) {
                length = (int) f.length();
                int numNewBytes = length - m_cursor;
                Logger.getLogger(StreamRecordingManager.class.getName()).info("Number of new bytes found in source file: " + numNewBytes);
                if(numNewBytes == 0) {
                    stopRecording();
                    return;
                }

                //Copy bytes into memory
                byte [] bytes = new byte[numNewBytes];

                //Skip bytes behind the cursor and read the new ones
                //System.out.println("Reading file... cursor = " + cursor + " length = " + length + " L-C = " + (length - cursor));
                fis.skip(m_cursor);
                fis.read(bytes, 0, numNewBytes);
                
                //Reset cursor to end of file
                m_cursor = (int) length;

                //Write scratch recording snippet for ffmpeg to pull from
                String snippetPath = VspProperties.getInstance().getScratchDirectory() + "/" + System.currentTimeMillis();
                File scratchRecordingSnippet = new File(snippetPath);
                try (FileOutputStream fos = new FileOutputStream(scratchRecordingSnippet)) {
                    fos.write(bytes);
                }
                
                launchFrameRipperForFile(snippetPath);
                
            } catch (FileNotFoundException ex) {
                Logger.getLogger(StreamRecordingManager.class.getName()).log(Level.SEVERE, "Source or Destination file not found", ex);
            } catch (IOException ex) {
                Logger.getLogger(StreamRecordingManager.class.getName()).log(Level.SEVERE, "IOException in Periodic Process", ex);
            }
        }
        
        private void launchFrameRipperForFile(final String snippetPath) {
            //Start FFMPEG to rip frames
            Runnable ffmpegRunnable = new Runnable() {
                @Override
                public void run() {
                    FileVideoSource source = new FileVideoSource(snippetPath);
                    try {
                        Logger.getLogger(StreamRecordingManager.class.getName()).info("Launching FFMPEG Process");
                        Process frameProcess = FfmpegVideoProcessor.ripFrames(source, 30, 3, m_recordingDirectory, m_batchIndex);
                        m_batchIndex++;
                        Logger.getLogger(StreamRecordingManager.class.getName()).info("Waiting for FFMPEG Process...");
                        frameProcess.waitFor(); //Wait for the processing to complete
                        //remove scratch material
                        File snippetFile = new File(snippetPath);
                        snippetFile.delete();
                        Logger.getLogger(StreamRecordingManager.class.getName()).info("Deleted Snippet File");
                    } catch (IOException ex) {
                        Logger.getLogger(StreamRecordingManager.class.getName()).log(Level.SEVERE, "IO Exception while processing frames", ex);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(StreamRecordingManager.class.getName()).log(Level.SEVERE, "Frame Processing thread interrupted", ex);
                    }
                }
            };
            new Thread(ffmpegRunnable).start();
        }
    };

}
