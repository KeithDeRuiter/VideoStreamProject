package vsp.processing;

import vsp.data.VideoSource;

/**
 * A Utility class that performs various processing actions on video.
 * @author adam
 */
public interface VideoProcessor {

    /**
     * Rips the frames from the supplied source video at the specified FPS (Frames Per Second) and the supplied image
     * quality value and writes them to disk at the specified output directory.  Images will be written out as .jpg
     * format.
     *
     * @param sourceVideo The video to rip the frames from.
     * @param fps The number of frames to rip per second of video.
     * @param quality The image quality to rip to.  The range of values here will be specified by given implementations.
     * @param outputDir a directory to write the frames to.
     *
     * @throws IllegalArgumentException if the source video does not exist, FPS is negative, quality value is out
     * of the valid range
     */
    public void ripFrames(VideoSource sourceVideo, int fps, int quality, String outputDir);


    /**
     * Builds a video from the given frames.
     * @TODO - This method and its signature are not complete.
     */
    public void buildVideoFromFrames();
}