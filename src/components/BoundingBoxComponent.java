package components;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Map.Entry;
import java.util.TreeMap;

import org.opencv.core.Mat;

public class BoundingBoxComponent extends Component {
	
	public BoundingBoxComponent(){
		this.setTitle("Bounding Boxes");
	}

	@Override
	@SuppressWarnings("unchecked")
	public void applyComponent(Mat inputMat) {
		

		TreeMap<Double, ArrayList<Point>> regions = (TreeMap<Double, ArrayList<Point> >)componentManager.getRegistryData("REGION_SET");
		
		System.out.println("~~~~~~~~~~~~~~~~~~~~~~~~~~");
		for(Entry<Double, ArrayList<Point>> e : regions.entrySet()) {
			System.out.println("" + e.getKey() + ": " + e.getValue().size());
		}
	}

}
