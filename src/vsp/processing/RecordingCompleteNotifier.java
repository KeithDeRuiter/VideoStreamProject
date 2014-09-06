/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vsp.processing;

/**
 *
 * @author Keith DeRuiter
 */
public interface RecordingCompleteNotifier {
    
    /** 
     * Adds the given listener.
     * @param listener The listener.
     */
    public void addRecordingCompleteListener(RecordingCompleteListener listener);
    
    /** 
     * Removes the given listener. 
     * @param listener The listener.
     */
    public void removeRecordingCompleteListener(RecordingCompleteListener listener);
    
}
