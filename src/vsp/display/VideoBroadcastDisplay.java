package vsp.display;

import java.awt.BorderLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import uk.co.caprica.vlcj.player.MediaPlayerFactory;
import uk.co.caprica.vlcj.player.headless.HeadlessMediaPlayer;
import vsp.data.VideoBroadcast;

/**
 * A utility display for broadcasting a video.
 * See:  https://github.com/caprica/vlcj/blob/master/src/test/java/uk/co/caprica/vlcj/test/streaming/StreamRtp.java
 * @author adam
 */
public class VideoBroadcastDisplay {

    /** LOGGER */
    private static final Logger LOGGER = Logger.getLogger(VideoBroadcastDisplay.class.getName());

    /** The default IP address. */
    private final String m_defaultIp = "226.0.67.1";

    /** The default port value. */
    private final String m_defaultPort = "44500";

    /** The top level window for this display. */
    private JFrame m_frame;

    /** A text field for the ip address. */
    private JTextField m_ipField;

    /** A text field for the port value. */
    private JTextField m_portField;

    /** A text file for the file. */
    private JTextField m_fileField;

    /** A button for launching the file chooser. */
    private JButton m_browseButton;

    /** Constructs a new VBD. */
    public VideoBroadcastDisplay() {
        initialize();
    }

    /**
     * Starts streaming the given VideoBroadcast in a new thread.
     * @param toStream a VideoBroadcast to be streamed.
     * @throws IOException if an error occurs when streaming.
     */
    public void stream(VideoBroadcast toStream) throws IOException {
        String options = formatRtpStream(toStream.getIpAddress(), toStream.getPort());

        MediaPlayerFactory mediaPlayerFactory = new MediaPlayerFactory(toStream.getTsFile());
        HeadlessMediaPlayer mediaPlayer = mediaPlayerFactory.newHeadlessMediaPlayer();
        mediaPlayer.playMedia(toStream.getTsFile(),
            options,
            ":no-sout-rtp-sap",
            ":no-sout-standard-sap",
            ":sout-all",
            ":sout-keep"
        );
    }

    /**
     * Formats the RTP stream command, including the TS transcoding.
     * @param ip the multicast IP address to broadcast this video on.
     * @param port the port to broadcast on.
     *
     * @return returns the formatted RTP stream command.
     */
    private String formatRtpStream(String ip, int port) {
        StringBuilder sb = new StringBuilder();
        sb.append(":sout=#transcode{vcodec=mp2v,vb=800,acodec=mpga,ab=128,channels=2,samplerate=44100}");
        sb.append(":rtp{dst=");
        sb.append(ip);
        sb.append(",port=");
        sb.append(port);
        sb.append(",mux=ts}");
        return sb.toString();
    }

    /** Launches and sets the display visible. */
    public void launch() {
        m_frame.pack();
        m_frame.setVisible(true);
    }

    /** Initializes the display components for this class. */
    private void initialize() {
        m_frame = new JFrame("Video Broadcaster");
        m_frame.setResizable(false);
        m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        m_frame.setLayout(new BorderLayout());
        m_frame.add(buildContentPanel(), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel();
        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                try {
                    stream(getVideoBroadcast());
                } catch (IOException ex) {
                    LOGGER.log(Level.SEVERE, "Error starting stream.", ex);
                }
            }
        });
        buttonPanel.add(startButton);

        m_frame.add(buttonPanel, BorderLayout.PAGE_END);
    }

    private VideoBroadcast getVideoBroadcast() {
        return new VideoBroadcast(m_fileField.getText().trim(),
                                    m_ipField.getText().trim(),
                                    Integer.valueOf(m_portField.getText().trim()));

    }

    /**
     * Assembles and returns the JPanel with the main controls and content on it.
     * @return the main content panel.
     */
    private JPanel buildContentPanel() {
        JPanel content = new JPanel();
        content.setLayout(new GridBagLayout());

        m_ipField = new JTextField(10);
        m_ipField.setText(m_defaultIp);

        m_portField = new JTextField(5);
        m_portField.setText(m_defaultPort);

        m_fileField = new JTextField(18);
        m_browseButton = new JButton("Browse...");
        m_browseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                JFileChooser chooser = new JFileChooser();
                if (chooser.showOpenDialog(m_frame) == JFileChooser.APPROVE_OPTION) {
                    m_fileField.setText(chooser.getSelectedFile().getAbsolutePath());
                }
            }
        });

        // First Column
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(2, 2, 2, 2);
        content.add(new JLabel("IP:"), gbc);

        gbc.gridy = 1;
        content.add(new JLabel("File:"), gbc);

        // Second Column
        gbc.gridx = 1;
        gbc.gridy = 0;
        content.add(m_ipField, gbc);

        gbc.gridy = 1;
        gbc.gridwidth = 3;
        content.add(m_fileField, gbc);

        // Third Column
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        content.add(new JLabel("Port:"), gbc);

        // Fourth Column
        gbc.gridx = 3;
        content.add(m_portField, gbc);

        gbc.gridx = 4;
        gbc.gridy = 1;
        content.add(m_browseButton, gbc);

        // Separator
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        content.add(new JSeparator(), gbc);

        return content;
    }
}