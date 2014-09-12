package vsp.display;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.Insets;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.BorderFactory;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import vsp.processing.MulticastDataDetectionListener;
import vsp.processing.MulticastDataDetector;

/**
 * A utility display for listening to record video.
 * @author Keith DeRuiter
 */
public class StreamRecorderDisplay implements MulticastDataDetectionListener {

    /** LOGGER */
    private static final Logger LOGGER = Logger.getLogger(StreamRecorderDisplay.class.getName());

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
    
    /** Lamp for if the stream is active. */
    private JLabel m_connectionLabel;
    
    /** The data detector for the recording. */
    private MulticastDataDetector m_dataDetector;

    /** Constructs a new SRD. */
    public StreamRecorderDisplay() {
        initialize();
    }
    
    /** Begins listening for streams. */
    private void listen() {
        //Grap port/ip from UI
        String ip = m_ipField.getText().trim();
        try {
            ip = InetAddress.getByName(ip).getHostAddress();    //Validate IP Address
        } catch (UnknownHostException ex) {
            Logger.getLogger(StreamRecorderDisplay.class.getName()).log(Level.SEVERE, "Poorly formatted IP Address:  " + m_ipField.getText(), ex);
        }
        int port = Integer.valueOf(m_portField.getText().trim());
        
        //Start detector
        m_dataDetector = new MulticastDataDetector(ip, port);
        m_dataDetector.addMulticastDataDetectionListener(this);
        new Thread(m_dataDetector).start();
    }

    /** Launches and sets the display visible. */
    public void launch() {
        m_frame.pack();
        m_frame.setVisible(true);
    }

    /** Initializes the display components for this class. */
    private void initialize() {
        m_frame = new JFrame("Video Recorder");
        m_frame.setResizable(false);
        m_frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        m_frame.setLayout(new BorderLayout());
        m_frame.add(buildContentPanel(), BorderLayout.CENTER);

        JPanel buttonPanel = new JPanel(new FlowLayout(FlowLayout.CENTER, 5, 5));
        JButton startButton = new JButton("Start");
        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent ae) {
                listen();
            }
        });
        buttonPanel.add(startButton);
        
        m_connectionLabel = new JLabel("Disconnected");
        m_connectionLabel.setOpaque(true);
        m_connectionLabel.setBackground(Color.RED);
        m_connectionLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK, 3));
        m_connectionLabel.setPreferredSize(new Dimension(100, 26));
        m_connectionLabel.setHorizontalAlignment(SwingConstants.CENTER);
        buttonPanel.add(m_connectionLabel);
        
        m_frame.add(buttonPanel, BorderLayout.PAGE_END);
    }

    /** Changes to disconnected status. */
    private void disconnected() {
        m_connectionLabel.setBackground(Color.RED);
        m_connectionLabel.setText("Disconnected");
    }
    
    /** Changes to connected status. */
    private void connected() {
        m_connectionLabel.setBackground(Color.GREEN);
        m_connectionLabel.setText("Connected");
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

        // First Column
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = 0;
        gbc.gridy = 0;
        gbc.insets = new Insets(2, 2, 2, 2);
        content.add(new JLabel("IP:"), gbc);


        // Second Column
        gbc.gridx = 1;
        gbc.gridy = 0;
        content.add(m_ipField, gbc);


        // Third Column
        gbc.gridx = 2;
        gbc.gridy = 0;
        gbc.gridwidth = 1;
        content.add(new JLabel("Port:"), gbc);

        // Fourth Column
        gbc.gridx = 3;
        content.add(m_portField, gbc);


        // Separator
        gbc.gridx = 0;
        gbc.gridy = 2;
        gbc.gridwidth = 5;
        gbc.fill = GridBagConstraints.HORIZONTAL;
        content.add(new JSeparator(), gbc);

        return content;
    }

    @Override
    public void streamStarted() {
        connected();
    }

    @Override
    public void streamEnded() {
        disconnected();
    }
}