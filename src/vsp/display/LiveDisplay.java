package vsp.display;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import uk.co.caprica.vlcj.component.EmbeddedMediaPlayerComponent;

/**
 * The main display class of the LiveDisplay application for displaying streams.  Includes the media player, source 
 * entry, and controls.
 * 
 * @author Keith
 */
public class LiveDisplay {

    /** The main frame for this display. */
    private final JFrame m_frame;
    
    /** The embedded VLCJ media player. */
    private EmbeddedMediaPlayerComponent m_mediaPlayerComponent;

    /** The Play Button for the stream. */
    JButton m_playButton;

    /** The Stop Button for the stream. */
    JButton m_stopButton;
    
    /** The volume slider. */
    JSlider m_volumeSlider;
    
    /** The stream source MRL field. */
    JTextField m_mrlSourceTextFied;
    
    /** The enter button for the MRL link. */
    JButton m_mrlConfirmButton;
    
    /** Constructs a new instance of {@code LiveDisplay}. */
    public LiveDisplay() {
        m_frame = new JFrame("Live Display");
        initComponents();
    }

    /** Initializes all of the components in this display. */
    private void initComponents() {
        //Frame init
        m_frame.setLayout(new BorderLayout());
        m_frame.setLocation(100, 100);
        m_frame.setPreferredSize(new Dimension(1050, 600));
        m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        
        //Media Player init
        m_mediaPlayerComponent = new EmbeddedMediaPlayerComponent();
        
        //UI init
        m_playButton = new JButton("Play");
        m_stopButton = new JButton("Stop");
        m_volumeSlider = new JSlider(JSlider.HORIZONTAL, 0, 100, 35);
        m_mediaPlayerComponent.getMediaPlayer().setVolume(35);
        m_volumeSlider.setMajorTickSpacing(20);
        m_volumeSlider.setMinorTickSpacing(5);
        m_volumeSlider.setPaintTicks(true);
        m_volumeSlider.setPaintLabels(true);
        m_mrlSourceTextFied = new JTextField(40);
        m_mrlConfirmButton = new JButton("Enter");
        
        m_playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                m_mediaPlayerComponent.getMediaPlayer().play();
            }
        });

        m_stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                m_mediaPlayerComponent.getMediaPlayer().stop();
            }
        });
        
        m_volumeSlider.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent ce) {
                int vol = m_volumeSlider.getValue();
                m_mediaPlayerComponent.getMediaPlayer().setVolume(vol);
            }
        });
        
        m_mrlConfirmButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String mrl = m_mrlSourceTextFied.getText();
                m_mediaPlayerComponent.getMediaPlayer().playMedia(mrl);
            }
        });
        
        //Component Assembly
        m_frame.add(m_mediaPlayerComponent, BorderLayout.CENTER);
        
        JPanel controlPanel = new JPanel();
        controlPanel.add(m_playButton);
        controlPanel.add(m_stopButton);
        controlPanel.add(new JLabel("Volume:"));
        controlPanel.add(m_volumeSlider);
        m_frame.add(controlPanel, BorderLayout.SOUTH);
        
        JPanel mrlPanel = new JPanel();
        mrlPanel.add(new JLabel("MRL Source:"));
        mrlPanel.add(m_mrlSourceTextFied);
        mrlPanel.add(m_mrlConfirmButton);
        m_frame.add(mrlPanel, BorderLayout.NORTH);
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
