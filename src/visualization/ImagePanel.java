package visualization;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionListener;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JViewport;
import javax.swing.UIManager;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;

import javax.swing.JTextField;
import javax.swing.JLabel;

/**
 * ImagePanel wraps a standard JPanel, and displays a given Mat object at it's center. 
 * @author Daniel Miller
 */
public class ImagePanel extends JPanel implements MouseMotionListener{
	private static final long serialVersionUID = 7238134395013929552L;
	private BufferedImage bufImage;
	private Mat imageMat;
	private JPanel imageDrawPanel = new ImageDrawingPanel();
	private JScrollPane scrollPane = new JScrollPane();
	private JTextField pixelRField;
	private JTextField pixelGField;
	private JTextField pixelBField;

	public ImagePanel(){
		bufImage = null;
		setLayout(new BorderLayout(0, 0));
		scrollPane.setViewportView(imageDrawPanel);
		HandScrollListener drag = new HandScrollListener(imageDrawPanel, scrollPane.getViewport());
		imageDrawPanel.addMouseMotionListener(drag);
		imageDrawPanel.addMouseListener(drag);
		this.add(scrollPane);
		
		JPanel panel = new JPanel();
		add(panel, BorderLayout.NORTH);
		
		JLabel lblPixVal = new JLabel("Pixel Value");
		panel.add(lblPixVal);
		
		pixelRField = new JTextField();
		panel.add(pixelRField);
		pixelRField.setColumns(3);
		pixelRField.setEditable(false);
		
		pixelGField = new JTextField();
		panel.add(pixelGField);
		pixelGField.setColumns(3);
		pixelGField.setEditable(false);
		
		pixelBField = new JTextField();
		panel.add(pixelBField);
		pixelBField.setColumns(3);
		pixelBField.setEditable(false);
	}
	
	public ImagePanel(Mat image_tmp) {
		this();
		this.setMat(image_tmp);
		this.bufferImage();
	}
	
	private void bufferImage() {

	    MatOfByte matOfByte = new MatOfByte();
	    Highgui.imencode(".bmp", imageMat, matOfByte); 
	    
	    byte[] byteArray = matOfByte.toArray();

	    this.bufImage = null;
	    
	    try {
	        InputStream in = new ByteArrayInputStream(byteArray);
	        bufImage = ImageIO.read(in);
	    } catch (Exception e) {
	        e.printStackTrace();
	    }
	}

	public void setMat(Mat newMat) {
		this.imageMat = newMat;
		recalculate();
	}
	
	public void recalculate() {
		bufferImage();
		imageDrawPanel.repaint();
	}
	
	private class ImageDrawingPanel extends JPanel {
		public ImageDrawingPanel(){
			this.addMouseMotionListener(ImagePanel.this);
		}
		
//		@Override
//		public void scrollRectToVisible(){
//			
//		}
		
		@Override
		public Dimension getPreferredSize(){
			if(bufImage != null){
				return new Dimension(bufImage.getWidth(), bufImage.getHeight());
			} else {
				return new Dimension(100, 100);
			}
		}
		@Override
		public void paint(Graphics g1){
			if(bufImage != null){
				setBackground(UIManager.getColor( "Panel.background" ));
				g1.clearRect(0, 0, this.getWidth(), this.getHeight());
				Point p = ImagePanel.this.getImageLoc();
				g1.drawImage(bufImage, p.x, p.y, null);
			}
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		
	}

	public Point getImageLoc() {
		int x;
		int y;
		if(bufImage != null) {
			x = (imageDrawPanel.getWidth() - bufImage.getWidth())/2;
			y = (imageDrawPanel.getHeight() - bufImage.getHeight())/2;
		} else {
			x = 0;
			y = 0;
		}
		return new Point(x, y);
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		Point p = e.getPoint();
		Point imageOrigin = getImageLoc();
		
		if(p != null){
			Point imagePixel = new Point(p);
			imagePixel.x -= imageOrigin.x;
			imagePixel.y -= imageOrigin.y;
			
			if(imagePixel.x >= bufImage.getWidth() ||
					imagePixel.x < 0) return;
			if(imagePixel.y >= bufImage.getHeight() ||
					imagePixel.y < 0) return;
			
			int val = bufImage.getRGB((int)imagePixel.getX(), (int)imagePixel.getY());
			pixelRField.setText("" + (val & 0x000000FF));
			pixelGField.setText("" + ((val & 0x0000FF00)>>8) );
			pixelBField.setText("" + ((val & 0x00FF0000)>>16) );
		}
	}
}

class HandScrollListener extends MouseAdapter
{
    private final Cursor defCursor = Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR);
    private final Cursor hndCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR);
    private final Point pp = new Point();
    private JComponent image;
	private JViewport vport;

    public HandScrollListener(JComponent image, JViewport vport)
    {
        this.image = image;
        this.vport = vport;
    }

    public void mouseDragged(final MouseEvent e)
    {
        Point cp = e.getPoint();
        if(cp != null){
	        Point vp = vport.getViewPosition();
	        vp.translate(pp.x-cp.x, pp.y-cp.y);
	        image.scrollRectToVisible(new Rectangle(vp, vport.getSize()));
	        pp.setLocation(cp);
//	        System.out.println("" + pp);
        }
    }

    public void mousePressed(MouseEvent e)
    {
        image.setCursor(hndCursor);
        pp.setLocation(e.getPoint());
    }

    public void mouseReleased(MouseEvent e)
    {
        image.setCursor(defCursor);
        image.repaint();
    }
}