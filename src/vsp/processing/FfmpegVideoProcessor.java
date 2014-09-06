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
     *
     * @throws IllegalArgumentException if the source video does not exist, FPS is negative, quality value is out
     * of the valid range (1-31).
     */
    public Process ripFrames(FileVideoSource sourceVideo, int fps, int quality, String outputDir, long startTime, long endTime) throws IOException {
        // Sample command String:
        //      ffmpeg -i toRip.ts -q 3 -r 30 -f image2 ./output/image-%%08d.jpg

        VspProperties props = VspProperties.getInstance();

        // Create a FrameRecording and write it to disk.
        FrameRecording recording = new FrameRecording(sourceVideo.getId(),  // Source Video UUID
                fps,                  // Frames Per Second
                sourceVideo.getMrl(), // Source Video
                outputDir,            // Output Dir
                startTime,            // Start Time
                endTime);             // End Time
        recording.saveToFile(outputDir + props.getFrameRecordingFilename());

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
        command.add(outputDir + "img-%08d.jpg"); // Output Directory and File Format - (DO WE NEED %% here?  or just %?)

        ProcessBuilder pb = new ProcessBuilder(command);
        Process process = pb.start();
        return process;
    }

    /** This method is not implemented and will throw an Unsupported Operation Exception. */
    public void buildVideoFromFrames() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
