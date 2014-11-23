package visualization;

import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import java.awt.BorderLayout;
import java.util.ArrayList;
import java.util.List;

public class ImageChannelPanel extends JPanel {

	private ImagePanel RGBPanel;
	private ImagePanel rPanel;
	private ImagePanel gPanel;
	private ImagePanel bPanel;
	private Mat imageMat;

	public ImageChannelPanel() {
		setLayout(new BorderLayout(0, 0));
		
		JTabbedPane tabbedPane = new JTabbedPane(JTabbedPane.TOP);
		add(tabbedPane);
		
		RGBPanel = new ImagePanel();
		tabbedPane.addTab("Combined", null, RGBPanel, null);
		
		rPanel = new ImagePanel();
		tabbedPane.addTab("Red", null, rPanel, null);
		
		gPanel = new ImagePanel();
		tabbedPane.addTab("Green", null, gPanel, null);
		
		bPanel = new ImagePanel();
		tabbedPane.addTab("Blue", null, bPanel, null);
	}
	
	public void setMat(Mat newMat) {
		this.imageMat = newMat;
	}

	public void recalculate() {
		populateChannels();
		repaint();
	}

	private void populateChannels() {
	    List<Mat> lRgb = new ArrayList<Mat>(3);
	    Core.split(imageMat, lRgb);
	    
	    Imgproc.cvtColor(lRgb.get(2), lRgb.get(2), Imgproc.COLOR_GRAY2BGR);
	    Imgproc.cvtColor(lRgb.get(1), lRgb.get(1), Imgproc.COLOR_GRAY2BGR);
	    Imgproc.cvtColor(lRgb.get(0), lRgb.get(0), Imgproc.COLOR_GRAY2BGR);
	    
	    RGBPanel.setMat(imageMat);
	    rPanel.setMat(lRgb.get(2));
	    gPanel.setMat(lRgb.get(1));
	    bPanel.setMat(lRgb.get(0));
	}

}
