package components;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import components.base.Component;

@SuppressWarnings("serial")
public class BoundingBoxComponent extends Component {
	
	class Region{
		public int regionNumber = 0;
		public int minX = Integer.MAX_VALUE;
		public int minY = Integer.MAX_VALUE;
		public int maxX = 0;
		public int maxY = 0;
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
		public String toString(){
			return "Region: (" + minX + ", " + minY + ") - (" + maxX + ", " + maxY + ")";
		}
	}
	
	
	public BoundingBoxComponent(){
		this.setTitle("Bounding Boxes");
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
			Region r = new Region();
			r.regionNumber = e.getKey().intValue();
			for(Point p : e.getValue()){
				r.updateValues(p.x, p.y);
			}
			regionBounds.add(r);
		}
		
		for(Region r : regionBounds){
			Core.rectangle(inputMat, r.getMinPoint(), r.getMaxPoint(), new Scalar(0, 0, 255));
		}
	}

}
