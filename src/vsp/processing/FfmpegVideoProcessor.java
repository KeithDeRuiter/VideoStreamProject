package vsp.processing;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import vsp.data.FileVideoSource;
import vsp.data.FrameRecording;
import vsp.util.VspProperties;

/**
 *
 * @author adam
 */
public class FfmpegVideoProcessor {

    /**
     * Rips the frames from the supplied source video at the specified FPS (Frames Per Second) and the supplied image
     * quality value and writes them to disk at the specified output directory.  Images will be written out as .jpg
     * format.
     *
     * @param sourceVideo The video to rip the frames from.
     * @param fps The number of frames to rip per second of video.
     * @param quality The image quality to rip to.  Values range from 1 to 31, with 1 being the highest quality and 31
     * being the lowest quality.
     * @param outputDir a directory to write the frames to.
     * @param batchIndex the index of which snippet this image falls under.
     *
     * @return The process started to rip the frames.
     * 
     * @throws IllegalArgumentException if the source video does not exist, FPS is negative, quality value is out
     * of the valid range (1-31).
     * @throws IOException if the process cannot be started.
     */
    public static Process ripFrames(FileVideoSource sourceVideo, int fps, int quality, String outputDir, int batchIndex) throws IOException {
        // Sample command String:
        //      ffmpeg -i toRip.ts -q 3 -r 30 -f image2 ./output/image-%%08d.jpg
        VspProperties props = VspProperties.getInstance();

        List<String> command = new ArrayList<>();
        command.add(props.getFfmpegPath());      // FFMPEG command
        command.add("-i");                       // Input File Flag
        command.add(sourceVideo.getFilepath());  // File Value
        command.add("-q");                       // Quality Flag
        command.add(String.valueOf(quality));    // Quality Value
        command.add("-r");                       // 'Rate' (FPS) Flag
        command.add(String.valueOf(fps));        // FPS value.
        command.add("-f");                       // Output image Format
        command.add("image2");                   // Output image Format
        command.add(outputDir + String.format("%06d", batchIndex) + "-%04d.jpg"); // Output Directory and File Format - (DO WE NEED %% here?  or just %?)

        ProcessBuilder pb = new ProcessBuilder(command);
        Process process = pb.start();
        ProcessHelper.consumeOutput(process);
        return process;
    }

    /** This method is not implemented and will throw an Unsupported Operation Exception. */
    public void buildVideoFromFrames() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
