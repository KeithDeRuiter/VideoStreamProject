/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vsp.processing;

/**
 * Interface for a notifier of multicast data detections.
 * 
 * @author Keith DeRuiter
 */
public interface MulticastDataDetectionNotifier {
    
    /** 
     * Adds a new listener to be notified.
     * @param listener The listener to add.
     */
    public void addMulticastDataDetectionListener(MulticastDataDetectionListener listener);
    
    /** 
     * Removes a listener from notifications.
     * @param listener The listener to add.
     */
    public void removeMulticastDataDetectionListener(MulticastDataDetectionListener listener);
}
