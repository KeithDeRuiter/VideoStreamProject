/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vsp.processing;

import vsp.data.VideoSource;

/**
 *
 * @author Keith DeRuiter
 */
public interface RecordingCompleteListener {
    
    /**
     * Listener for when a recording is complete.  Passes back the source that was used for that recording.
     * @param source The source for the recording that completed.
     */
    public void recordingComplete(VideoSource source);
    
}
