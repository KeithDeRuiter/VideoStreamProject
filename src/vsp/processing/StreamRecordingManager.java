package vsp.processing;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;
import vsp.data.FileVideoSource;
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
    
    /** The period at which recording blocks are sent to processing. */
    private final TimeUnit m_RecordingBlockFlushPeriod;
    
    /** The frames per second rate to record at. */
    private final int m_fps;
    
    /** The quality level of the recording, specified by the implementation of {@code VideoProcessor} used. */
    private final int m_quality;

    /** The processor used to record and rip the individual frames. */
    private final static FfmpgVideoProcessor ffmpvp = new FfmpgVideoProcessor();
    
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
    public StreamRecordingManager(VideoSource source, String frameDestinationDirectory, int numSimultaneousRecordings,
            TimeUnit snippetRecordingDuration, int fps, int quality) {
        m_source = source;
        m_frameDestinationDirectory = frameDestinationDirectory;
        m_RecordingBlockFlushPeriod = snippetRecordingDuration;
        m_fps = fps;
        m_quality = quality;
    }
    
    /** Start recording. */
    public void startRecording() {
        
    }
    
    /** Stop recording. */
    public void stopRecording() {
        
    }
    
    //========================================================
    
    private static int cursor = 0;
    
    
    
    /** Test main to print out fields from MPEG2 packets. */
    public static void main(String args[]) {
        
        Runnable r = new Runnable() {
            @Override
            public void run() {
                System.out.println("RUNNING");
                
                //Read In
                File f = new File("pystream.ts");
                FileInputStream fis = null;
                try {
                    fis = new FileInputStream(f);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }

                int length = (int) f.length();
                byte[] bytes = new byte[length - cursor];

                try {
                    System.out.println("Reading file... cursor = " + cursor + " length = " + length + " L-C = " + (length - cursor));
                    fis.skip(cursor);
                    fis.read(bytes, 0, length - cursor);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                
                //Write Out
                final String filename = "slices/slice_" + cursor + ".ts";
                File of = new File(filename);

                if(!of.exists()) {
                    try {
                        System.out.println("Creating new file...");
                        of.createNewFile();
                    } catch (IOException ex) {
                        ex.printStackTrace();
                    }
                }
                FileOutputStream fos = null;
                try {
                    fos = new FileOutputStream(of);
                } catch (FileNotFoundException ex) {
                    ex.printStackTrace();
                }


                try {
                    System.out.println("Writing file...");
                    fos.write(bytes);
                } catch (IOException ex) {
                    ex.printStackTrace();
                }
                
                try {
                    System.out.println("Closing file...");
                    fis.close();
                    fos.close();
                } catch (IOException ex) {
                    Logger.getLogger(StreamRecordingManager.class.getName()).log(Level.SEVERE, null, ex);
                }
                
                
                System.out.println("Wrote cursor: " + cursor);
                cursor = (int) length;
                
                
                //Start FFMPEG
                Runnable ffmpegRunnable = new Runnable() {
                    @Override
                    public void run() {
                        FileVideoSource source = new FileVideoSource(filename);
                        try {
                            File dir = new File("frames/" + filename);
                            dir.mkdirs();
                            
                            Process p = ffmpvp.ripFrames(source, 30, 3, dir.getAbsolutePath(), 0, 0);
                            
                            final InputStream procOutput = p.getInputStream();
                            final InputStream procError = p.getErrorStream();
                            
                            final BufferedInputStream bufProcOutput = new BufferedInputStream(procOutput);
                            final BufferedInputStream bufProcError = new BufferedInputStream(procError);
                            
                            final InputStreamReader procOutputReader = new InputStreamReader(bufProcOutput);
                            final InputStreamReader procErrorReader = new InputStreamReader(bufProcError);
                            
                            final BufferedReader outputBufReader = new BufferedReader(procOutputReader);
                            final BufferedReader errorBufReader = new BufferedReader(procErrorReader);
                            
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    try {
                                        String line;
                                        while((line = outputBufReader.readLine()) != null);
                                        while((line = errorBufReader.readLine()) != null);
                                    } catch (IOException ex) {
                                        ex.printStackTrace();
                                    }
                                }
                            }).start();
                            
                        } catch (IOException ex) {
                            ex.printStackTrace();
                        }
                    }   
                };
                new Thread(ffmpegRunnable).start();
            }   
        };
        
        ScheduledExecutorService ses = Executors.newSingleThreadScheduledExecutor();
        ses.scheduleAtFixedRate(r, 0L, 10L, TimeUnit.SECONDS);
    }
}
