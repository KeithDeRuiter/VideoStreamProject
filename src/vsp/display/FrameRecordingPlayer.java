package vsp.display;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JList;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.ListCellRenderer;
import javax.swing.filechooser.FileNameExtensionFilter;
import vsp.data.FrameRecording;
import vsp.util.VspProperties;

/**
 * A special home-brew player for playing through ripped frame data.
 * @author adam
 */
public class FrameRecordingPlayer {

    /** The top-level window of this display. */
    private JFrame m_frame;

    /** The 'screen' on which the images are rendered.  */
    private FrameViewer m_screen;

    /** The button to play the selected media. */
    private JButton m_playButton;

    /** The frame advance button. */
    private JButton m_frameForwardButton;

    /** The frame reverse button. */
    private JButton m_frameBackwardButton;

    /** The speed selector. */
    private JComboBox<PlaySpeed> m_playSpeedSelector;

    /** The text field for specifying the media. */
    private JTextField m_mediaField;

    /**  */
    private FrameRecording m_recording;

    /** Creates a new Frame Recording Player. */
    public FrameRecordingPlayer() {
        initComponents();
    }

    /** Launches the display. */
    public void launch() {
        m_frame.pack();
        m_frame.setVisible(true);
    }

    /**
     * Calculates the frame rate in milliseconds per frame, unfortunately this currently performs flat integer math and
     * could slowly, over time create playback frame drift.  This method should get smarter.
     * @param fps the FPS (frames per second)
     * @return the number of milliseconds between frames.
     */
    private long calculateRateInMillis(int fps) {
        // This should be smarter
        return 1000 / fps;
    }

    /** Initializes the display components of this display. */
    private void initComponents() {
        m_frame = new JFrame("Frame Recording Player");
        m_frame.setLayout(new BorderLayout());
        m_frame.setPreferredSize(new Dimension(800, 600));
        m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        m_screen = new FrameViewer();

        m_frame.add(buildMediaSelectionPanel(), BorderLayout.PAGE_START);
        m_frame.add(m_screen, BorderLayout.CENTER);
        m_frame.add(createButtonPanel(), BorderLayout.PAGE_END);
    }

    /**
     * Creates the control panel for the player.
     * @return the control panel for the player.
     */
    private JPanel createButtonPanel() {
        JPanel panel = new JPanel();

        m_playButton = new JButton("PLAY");
        m_playButton.setToolTipText("Play/Pause button.");
        m_playButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                String mediaFile = m_mediaField.getText();
                if (!mediaFile.isEmpty() || mediaFile.endsWith(VspProperties.getInstance().getFrameRecordingFilename())) {
                    m_recording = FrameRecording.fromFile(m_mediaField.getText());
                    File frameDir = new File(m_recording.getFrameDirectory());
                    if (frameDir.exists() && frameDir.isDirectory()) {
                        File[] frames = frameDir.listFiles(new FileFilter() {
                            @Override
                            public boolean accept(File file) {
                                if(file.getName().endsWith("jpg") || file.getName().endsWith("jpeg")) {
                                    return true;
                                }
                                return false;
                            }
                        });

                        final List<File> frameList = Arrays.asList(frames);
                        PlayProcessor pp = new PlayProcessor(frameList);
                        ScheduledExecutorService executor = Executors.newSingleThreadScheduledExecutor();
                        long rate = calculateRateInMillis(m_recording.getFps());
                        executor.scheduleAtFixedRate(pp, 0L, rate, TimeUnit.MILLISECONDS);
                    } else {
                        JOptionPane.showMessageDialog(m_frame, "Media directory not found or is not a directory.  :(", "Error", JOptionPane.WARNING_MESSAGE);
                        System.out.println(frameDir.exists());
                        System.out.println(frameDir.isDirectory());
                        System.out.println(frameDir.getAbsoluteFile());
                    }
                } else {
                    JOptionPane.showMessageDialog(m_frame, "File must be a frame-recording.properties file.", "Error", JOptionPane.WARNING_MESSAGE);
                }
            }
        });

        m_frameBackwardButton = new JButton("|<");
        m_frameBackwardButton.setToolTipText("Go backward a single frame.");

        m_frameForwardButton = new JButton(">|");
        m_frameForwardButton.setToolTipText("Go Forward a single frame.");

        m_playSpeedSelector = new JComboBox<>();
        m_playSpeedSelector.setRenderer(new ListCellRenderer<PlaySpeed>() {
            @Override
            public Component getListCellRendererComponent(JList jlist, PlaySpeed speed, int i, boolean bln, boolean bln1) {
                return new JLabel(speed.getDisplayString());
            }
        });
        for (PlaySpeed speed : PlaySpeed.values()) {
            m_playSpeedSelector.addItem(speed);
        }

        // Add the buttons
        panel.add(m_playSpeedSelector);
        panel.add(m_frameBackwardButton);
        panel.add(m_playButton);
        panel.add(m_frameForwardButton);

        return panel;
    }

    /** Assembles the media selection panel. */
    private JPanel buildMediaSelectionPanel() {
        JPanel panel = new JPanel();
        JLabel label = new JLabel("FrameRecording.properties file:");
        m_mediaField = new JTextField(45);
        JButton browse = new JButton("Browse...");
        browse.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                JFileChooser chooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Frame Recordings", ".fr");
                chooser.setFileFilter(filter);
                if (chooser.showOpenDialog(m_frame) == JFileChooser.APPROVE_OPTION) {
                    m_mediaField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        panel.add(label);
        panel.add(m_mediaField);
        panel.add(browse);

        return panel;
    }

    /** A PlayProcessor that handles updating frames on the FramePlayer. */
    private class PlayProcessor implements Runnable {

        /** The List of Frames to Play.  */
        private final List<File> m_frames;

        /** The current frame cursor position. */
        private int m_cursor = 0;

        /**
         * Constructs a new instance of PlayProcessor.
         * @param frames the frames to play.
         */
        public PlayProcessor(List<File> frames) {
            m_frames = new ArrayList<>(frames);
        }

        /**
         * Updates the cursor position to the one specified.
         * @param position the position of the cursor to set.
         */
        public void setCursor(int position) {
            if (position < 0) {
                m_cursor = 0;
            } else if (position > m_frames.size() -1 ) {
                m_cursor = m_frames.size() - 1;
            } else {
                m_cursor = position;
            }
        }

        /** {@inheritDoc} */
        @Override
        public void run() {
            if (m_cursor == m_frames.size() - 1) {
                m_cursor = 0;
            }
            m_screen.updateFrame(m_frames.get(m_cursor).getAbsolutePath());
            m_cursor++;
        }
    }
}