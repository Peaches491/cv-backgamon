package visualization;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.UIManager;

import org.opencv.core.Mat;
import org.opencv.core.MatOfByte;
import org.opencv.highgui.Highgui;

/**
 * ImagePanel wraps a standard JPanel, and displays a given Mat object at it's center. 
 * @author Daniel Miller
 */
public class ImagePanel extends JPanel{
	private static final long serialVersionUID = 7238134395013929552L;
	private BufferedImage bufImage;
	private Mat imageMat;

	public ImagePanel(){
		bufImage = null;
	}
	
	public ImagePanel(Mat image_tmp) {
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
	
	@Override
	public void paint(Graphics g1){
		if(bufImage != null){
			setBackground(UIManager.getColor( "Panel.background" ));
			g1.clearRect(0, 0, this.getWidth(), this.getHeight());
			int x = (this.getWidth() - bufImage.getWidth())/2;
			int y = (this.getHeight() - bufImage.getHeight())/2;
			g1.drawImage(bufImage, x, y, null);
		}
	}

	public void recalculate() {
		bufferImage();
		repaint();
	}
}