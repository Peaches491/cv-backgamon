package components;

import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;

import components.base.Component;
import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class BoundingBoxComponent extends Component implements ChangeListener {
	
	class Region{
		public int regionNumber = 0;
		public int minX = Integer.MAX_VALUE;
		public int minY = Integer.MAX_VALUE;
		public int maxX = 0;
		public int maxY = 0;
		public int pixelCount = 0;
		public void updateValues(int x, int y) {
			minX = Math.min(minX, x);
			minY = Math.min(minY, y);
			maxX = Math.max(maxX, x);
			maxY = Math.max(maxY, y);
		}
		public org.opencv.core.Point getMinPoint(){
			return new org.opencv.core.Point(minX, minY);
		}
		public org.opencv.core.Point getMaxPoint(){
			return new org.opencv.core.Point(maxX, maxY);
		}
		public Dimension getDimension(){
			return new Dimension(maxX-minX, maxY-minY);
		}
		public String toString(){
			return "Region: (" + minX + ", " + minY + ") - (" + maxX + ", " + maxY + ")";
		}
		public void increment() {
			pixelCount ++;
		}
	}

	private JSpinner spinnerMinPixels;
	private JSpinner spinnerMinAspect;
	private JSpinner spinnerMaxAspect;
	private JSpinner spinnerMaxPixels;
	private JLabel lblRegions;
	private JTextField regionsField;
	private JLabel lblBoundingArea;
	private JSpinner spinnerMinBox;
	private JSpinner spinnerMaxBox;
	private JLabel lblMinimum;
	private JLabel lblMaximum;
	private JLabel lblFillRatio;
	private JSpinner spinnerMinDensity;
	private JSpinner spinnerMaxDensity;
	
	public BoundingBoxComponent(){
		this(0, 1, 0, Integer.MAX_VALUE, 0, Integer.MAX_VALUE, 0.001, 5000);
	}
	
	public BoundingBoxComponent(double minDensity, double maxDensity, int minArea, int maxArea, int minPixels, int maxPixels, double minAspect, double maxAspect){
		super();
		
		this.setTitle("Bounding Boxes");
		setLayout(new MigLayout("fill", "[right][grow,fill][grow,fill]", "[][][][][][]"));
		
		lblMinimum = new JLabel("Minimum");
		lblMinimum.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblMinimum, "cell 1 0");
		
		lblMaximum = new JLabel("Maximum");
		lblMaximum.setHorizontalAlignment(SwingConstants.CENTER);
		add(lblMaximum, "cell 2 0");
		
		lblFillRatio = new JLabel("Fill Ratio");
		add(lblFillRatio, "cell 0 1");
		
		spinnerMinDensity = new JSpinner();
		spinnerMinDensity.setModel(new SpinnerNumberModel(new Double(minDensity), new Double(0), new Double(1), new Double(0.1)));
		add(spinnerMinDensity, "cell 1 1");
		spinnerMinDensity.addChangeListener(this);
		
		spinnerMaxDensity = new JSpinner();
		spinnerMaxDensity.setModel(new SpinnerNumberModel(new Double(maxDensity), new Double(0), new Double(1), new Double(0.1)));
		add(spinnerMaxDensity, "cell 2 1");
		spinnerMaxDensity.addChangeListener(this);
		
		lblBoundingArea = new JLabel("Bounding Area");
		add(lblBoundingArea, "cell 0 2");
		
		spinnerMinBox = new JSpinner();
		spinnerMinBox.setModel(new SpinnerNumberModel(new Integer(minArea), new Integer(0), null, new Integer(1)));
		add(spinnerMinBox, "cell 1 2");
		spinnerMinBox.addChangeListener(this);
		
		spinnerMaxBox = new JSpinner();
		spinnerMaxBox.setModel(new SpinnerNumberModel(new Integer(maxArea), new Integer(0), null, new Integer(1)));
		add(spinnerMaxBox, "cell 2 2");
		spinnerMaxBox.addChangeListener(this);
		
		JLabel lblPixels = new JLabel("Pixels");
		add(lblPixels, "cell 0 3");
		
		spinnerMinPixels = new JSpinner();
		spinnerMinPixels.setModel(new SpinnerNumberModel(new Integer(minPixels), new Integer(0), null, new Integer(1)));
		add(spinnerMinPixels, "cell 1 3");
		spinnerMinPixels.addChangeListener(this);
		
		spinnerMaxPixels = new JSpinner();
		spinnerMaxPixels.setModel(new SpinnerNumberModel(new Integer(maxPixels), new Integer(0), null, new Integer(1)));
		add(spinnerMaxPixels, "cell 2 3");
		spinnerMaxPixels.addChangeListener(this);
		
		JLabel lblAspect = new JLabel("Aspect");
		add(lblAspect, "cell 0 4");
		
		spinnerMinAspect = new JSpinner();
		spinnerMinAspect.setModel(new SpinnerNumberModel(new Double(minAspect), new Double(0.001), null, new Double(0.1)));
		add(spinnerMinAspect, "cell 1 4");
		spinnerMinAspect.addChangeListener(this);
		
		spinnerMaxAspect = new JSpinner();
		spinnerMaxAspect.setModel(new SpinnerNumberModel(new Double(maxAspect), new Double(0.001), null, new Double(0.1)));
		add(spinnerMaxAspect, "cell 2 4");
		spinnerMaxAspect.addChangeListener(this);
		
		lblRegions = new JLabel("Regions");
		add(lblRegions, "cell 0 5,alignx trailing");
		
		regionsField = new JTextField();
		regionsField.setHorizontalAlignment(SwingConstants.TRAILING);
		regionsField.setText("1");
		regionsField.setEditable(false);
		add(regionsField, "cell 1 5 2 1,growx");
		regionsField.setColumns(10);
	}

	@Override
	@SuppressWarnings("unchecked")
	public void applyComponent(Mat inputMat) {
		
//		Mat newMat = new Mat(inputMat.size(), CvType.CV_64FC3);
		
//		System.out.println(CvType.typeToString(inputMat.type()));
//		Imgproc.cvtColor(inputMat, newMat, Imgproc.COLOR_GRAY2BGR);
//		System.out.println(CvType.typeToString(inputMat.type()));

		TreeMap<Double, ArrayList<Point>> regions = (TreeMap<Double, ArrayList<Point>>) componentManager.getRegistryData("REGION_SET");
		
		ArrayList<Region> regionBounds = new ArrayList<Region>(regions.size());
		for(Entry<Double, ArrayList<Point>> e : regions.entrySet()){
			if(e.getValue().size() < (int)spinnerMinPixels.getValue()) continue;
			if(e.getValue().size() > (int)spinnerMaxPixels.getValue()) continue;
			
			Region r = new Region();
			r.regionNumber = e.getKey().intValue();
			for(Point p : e.getValue()){
				r.updateValues(p.x, p.y);
				r.increment();
			}
			Dimension dim = r.getDimension();
			double aspect;
			if(dim.height == 0){
				aspect = Double.MAX_VALUE;
			} else {
				aspect = (double)dim.width / (double)dim.height;
			}

			if(aspect < (double)spinnerMinAspect.getValue()) continue;
			if(aspect > (double)spinnerMaxAspect.getValue()) continue;
			
			if(dim.width*dim.height < (int)spinnerMinBox.getValue()) continue;
			if(dim.width*dim.height > (int)spinnerMaxBox.getValue()) continue;

			if(e.getValue().size() / ((double)dim.width*dim.height) < (double)spinnerMinDensity.getValue()) continue;
			if(e.getValue().size() / ((double)dim.width*dim.height) > (double)spinnerMaxDensity.getValue()) continue;
			
			regionBounds.add(r);
		}
		
		for(Region r : regionBounds){
			Core.rectangle((Mat)componentManager.getRegistryData("OVERLAY_MAT"), r.getMinPoint(), r.getMaxPoint(), new Scalar(0, 0, 255));
			Core.putText((Mat)componentManager.getRegistryData("OVERLAY_MAT"), ""+r.pixelCount, r.getMaxPoint(), Core.FONT_HERSHEY_DUPLEX, 0.5,  new Scalar(255, 255, 255), 1, Core.LINE_AA, false);
		}
		
		regionsField.setText("" + regionBounds.size());
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		notifyChange();
	}

}
