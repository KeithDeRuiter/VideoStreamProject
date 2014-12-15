package vsp;

import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import vsp.data.StreamVideoSource;
import vsp.util.VspProperties;

/**
 * This class provides a static method to get binary data (images) from a video stream input.
 * An ffmpeg process is launched for the video processing, which outputs the image data to
 * standard out.  This is then read in as a input stream, and the jpeg files are identified
 * and written to disk.
 * 
 * @author Keith
 */
public class FfmpegStreamCapture {

    /** Properties. */
    private static final VspProperties PROPERTIES = VspProperties.getInstance();
    
    /** Private constructor. */
    private FfmpegStreamCapture(){
    }

    /** Image file format spec. Last two bytes should have the following values: FF D9 */
    public static final int JPEG_FILE_EOF_NEXT_TO_LAST = 255; //FF

    /** Image file format spec. Last two bytes should have the following values: FF D9 */
    public static final int JPEG_FILE_EOF_LAST = 217;//D9

    /**
     * Grab frames from the source stream and write them to the given output directory.
     * @param sourceVideo source video stream to read from
     * @param fps the fps to capture frames at
     * @param quality the quality to capture frames at
     * @param outputDir the directory to write to
     * @throws IOException
     * @throws InterruptedException 
     */
    public static void runProcessing(StreamVideoSource sourceVideo, int fps, int quality, String outputDir) throws IOException, InterruptedException
    {
        //Build FFMPEG commandline string
        // Sample command String:
        //      ffmpeg -i toRip.ts -q 3 -r 30 -f image2 ./output/image-%%08d.jpg
        VspProperties props = VspProperties.getInstance();

        List<String> command = new ArrayList<>();
        command.add(props.getFfmpegPath());      // FFMPEG command
        command.add("-i");                       // Input File Flag
        command.add(sourceVideo.getMrl());       // File/Stream Value
        command.add("-q");                       // Quality Flag
        command.add(String.valueOf(quality));    // Quality Value
        command.add("-r");                       // 'Rate' (FPS) Flag
        command.add(String.valueOf(fps));        // FPS value.
        command.add("-f");                       // Output image Format
        command.add("image2pipe");               // Output image Format
        command.add("pipe:1");                   // Output image Format

        ProcessBuilder pb = new ProcessBuilder(command);
        Process ffmpegProcess = pb.start();

        //Get the input stream of bytes coming out of ffmpeg
        InputStream is = new BufferedInputStream(ffmpegProcess.getInputStream());

        int currentByte;
        int index = 0;
        int prelastByte = -2;
        ByteArrayOutputStream output =   new ByteArrayOutputStream();
        //Grab all of the bytes until we see the end of a jpeg file
        while ((currentByte = is.read()) != -1){
            output.write(currentByte);

            if (prelastByte == JPEG_FILE_EOF_NEXT_TO_LAST && currentByte == JPEG_FILE_EOF_LAST){
                // no in output you get the image as a binary array. You can efficiently save it in the database, memcached or file
                InputStream inputStream = new ByteArrayInputStream(output.toByteArray());
                BufferedImage bImageFromConvert = ImageIO.read(inputStream);
                
                ImageIO.write(bImageFromConvert, "jpg", new File(outputDir, "img" + index + ".jpg"));
                index++;
                output.reset();
            }
            prelastByte = currentByte;
        }

    }
  
    /**
     * Main method to run
     * @param args commandline arguments
     */
    public static void main(String[] args) {        
        try {
            StreamVideoSource streamVideoSource = new StreamVideoSource(PROPERTIES.getDefaultIpAddress(), PROPERTIES.getPort());
            runProcessing(streamVideoSource, PROPERTIES.getRecordingFps(), PROPERTIES.getRecordingQuality(), PROPERTIES.getScratchDirectory());
        } catch (UnknownHostException ex) {
            Logger.getLogger(FfmpegStreamCapture.class.getName()).log(Level.SEVERE, "Unknown Host IP: " + PROPERTIES.getDefaultIpAddress(), ex);
        } catch (IOException | InterruptedException ex) {
            Logger.getLogger(FfmpegStreamCapture.class.getName()).log(Level.SEVERE, "Error processing stream", ex);
        }
    }
} 
