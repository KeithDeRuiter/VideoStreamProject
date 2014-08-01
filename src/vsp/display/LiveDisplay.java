/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package vsp.display;

import javax.swing.JFrame;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

/**
 *
 * @author Keith
 */
public class LiveDisplay {

    /** The main frame for this display. */
    private final JFrame m_frame;
    
    /** The embedded VLCJ media player. */
    private EmbeddedMediaPlayerComponent m_mediaPlayerComponent;

    /**
     * Constructs a new instance of {@code LiveDisplay}.
     */
    public LiveDisplay() {
        m_frame = new JFrame("Live Display");
        initComponents();
    }

    /**
     * Initializes all of the components in this display.
     */
    private void initComponents() {
        m_frame.setLocation(100, 100);
        m_frame.setSize(1050, 600);
        m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        m_mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        m_frame.add(m_mediaPlayerComponent);
    }

    /**
     * Packs and sets the main frame visible.
     */
    public void launch() {
        m_frame.pack();
        m_frame.setVisible(true);
    }
    
    /** 
     * Plays the given media file.
     * 
     * @param filename the name of the media file to play
     */
    public void play(String filename) {
        m_mediaPlayerComponent.getMediaPlayer().playMedia(filename);
    }

}
