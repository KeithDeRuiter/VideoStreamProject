package vsp.display;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.RenderingHints;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 *
 * @author adam
 */
public class FrameViewer extends JComponent {

    /** The image to render. */
    private BufferedImage m_image;

    /** Logger. */
    private static final Logger LOGGER = Logger.getLogger(FrameViewer.class.getName());

    /** Creates a FrameViewer. */
    public FrameViewer() {
    }

    /**
     * Update the image to be rendered.
     * @param pathToImage the fully qualified path to the image.
     */
    public void updateFrame(String pathToImage) {
        try {
            m_image = ImageIO.read(new File(pathToImage));
        } catch (IOException ex) {
            LOGGER.log(Level.INFO, "Unable to read image:  " + pathToImage + ".  Ignoring frame.");
            m_image = null;
        }
        repaint();
    }

    /** Clears the screen of any current image. */
    public void clear() {
        m_image = null;
        repaint();
    }

    /** {@inheritDoc} */
    public void paint(Graphics g) {
        // Setup Graphics 2D
        Graphics2D g2d = (Graphics2D)g;
        Map<RenderingHints.Key, Object> hints = new HashMap<>();
        hints.put(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        hints.put(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        hints.put(RenderingHints.KEY_STROKE_CONTROL, RenderingHints.VALUE_STROKE_PURE);
        RenderingHints renderHints = new RenderingHints(hints);
        g2d.setRenderingHints(renderHints);

        Dimension dim = getSize();
        if (m_image == null) {
            g2d.setColor(Color.BLACK);
            g2d.fillRect(0, 0, dim.width, dim.height);
        } else {
            g2d.drawImage(m_image, 0, 0, this);
        }
    }
}