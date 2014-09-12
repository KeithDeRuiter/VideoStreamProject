/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package vsp.processing;

/**
 * Interface for a multicast data stream start and end listener.
 * 
 * @author Keith DeRuiter
 */
public interface MulticastDataDetectionListener {
    
    public void streamStarted();
    
    public void streamEnded();
}
