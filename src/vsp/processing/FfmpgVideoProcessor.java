package vsp.processing;

import java.util.ArrayList;
import java.util.List;
import vsp.data.FrameRecording;
import vsp.data.VideoSource;

/**
 *
 * @author adam
 */
public class FfmpgVideoProcessor implements VideoProcessor {

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
    @Override
    public void ripFrames(VideoSource sourceVideo, int fps, int quality, String outputDir) {
        // ffmpeg -i BNN-S01E06-720p.mkv -q 3 -r 30 -f image2 ./bnn-output/image-%%08d.jpg

        // Create a FrameRecording and write it to disk.
        FrameRecording recording = new FrameRecording(sourceVideo.getId(),  // Source Video UUID
                                                      fps,                  // Frames Per Second
                                                      sourceVideo.getMrl(), // Source Video
                                                      outputDir,            // Output Dir
                                                      0L,                   // Start Time
                                                      0L,                   // End Time
                                                      0,                    // Width
                                                      0);                   // Height

        List<String> command = new ArrayList<>();
        command.add("ffmpg ");                // FFMPG command
        command.add("-i ");                   // Input File Flag
        command.add(sourceVideo.getMrl());    // File Value
        command.add("-q ");                   // Quality Flag
        command.add(String.valueOf(quality)); // Quality Value
        command.add("-r ");                   // 'Rate' (FPS) Flag
        command.add(String.valueOf(fps));     // FPS value.
        command.add("-f image2");             // Output image Format
        command.add(outputDir);               // Output Directory
        command.add("img-%%08d.jpg");         // Output File Format - (DO WE NEED %% here?  or just %?)

        ProcessBuilder pb = new ProcessBuilder(command);


    }

    /** {@inheritDoc} */
    @Override
    public void buildVideoFromFrames() {
        throw new UnsupportedOperationException("Not supported yet.");
    }
}
