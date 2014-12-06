package components;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.imgproc.Imgproc;

import components.base.Component;
import components.base.ProcessInfo;
import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;
import javax.swing.JTextField;

@SuppressWarnings("serial")
public class RegionLabelingComponent extends Component {

	private int unlabledLabel = 1;
	private int currentLabel = 2;
	
	private Map<Double, TreeSet<Double> > collisionMap = new HashMap<Double, TreeSet<Double>>();
	private HashMap<Double, Double> orderedReplacementMap = new HashMap<Double, Double>();
	
	private Mat labelMat = new Mat();
	private JTextField textField;
	
	public RegionLabelingComponent() {
		this.setTitle("Region Labeling");
		setLayout(new MigLayout("fill", "[][grow]", "[]"));
		
		JLabel lblRegions = new JLabel("Regions:");
		add(lblRegions, "cell 0 0,alignx trailing");
		
		textField = new JTextField();
		textField.setEditable(false);
		textField.setText("0");
		add(textField, "cell 1 0,growx");
		textField.setColumns(10);
	}
	
	@Override
	public void applyComponent(Mat inputMat, ProcessInfo info) {
		boolean newMethod = true;
		reset(inputMat, newMethod);
		
//		Imgproc.cvtColor(inputMat, inputMat, Imgproc.COLOR_BGR2GRAY);
		Imgproc.threshold(inputMat, inputMat, 0, 1, Imgproc.THRESH_BINARY);
		
		double width = labelMat.size().width;
		double height = labelMat.size().height;
		
		TreeMap<Double, ArrayList<Point>> regionSet = new TreeMap<Double, ArrayList<Point>>();
	
//		newMethod = false;
		if(!newMethod){
			for(int y = 0; y < height; y++){
				for(int x = 0; x < width; x++){
					double[] intVec= inputMat.get(y, x);
					if(intVec[0] == unlabledLabel ||
					   intVec[1] == unlabledLabel ||
					   intVec[2] == unlabledLabel) {
	//				if(intVec[0] == unlabledLabel) {
						double l = updateCurrentLabel(inputMat, labelMat, x, y);
						labelMat.put(y, x, new double[] {l, l, l});
					}
				}
			}
			
			TreeMap<Double, Double> collisionRepair = collapseCollisions(labelMat);
			
			for(int y = 0; y < height; y++){
				for(int x = 0; x < width; x++){
					double[] intVec= labelMat.get(y, x);
					if(intVec[0] == 0.0) continue;
					
					Double newVal = collisionRepair.get(intVec[0]);
					
					if(newVal == null) newVal = intVec[0];
					
	//				if(orderedReplacementMap.containsKey(newVal)){
	//					newVal = orderedReplacementMap.get(newVal);
	//				}
					
					labelMat.put(y, x, new double[] {newVal, newVal, newVal});
					if(!regionSet.containsKey(newVal)) 
						regionSet.put(newVal, new ArrayList<Point>());
					regionSet.get(newVal).add(new Point(x, y));
				}
			}
			
			int idx = 1;
			Map<Double, Double> finalRegionMap = new HashMap<Double, Double>(regionSet.size());
			for(Double e : regionSet.keySet()){
				finalRegionMap.put(e, (double) idx);
				idx++;
			}
			
			
			for(int y = 0; y < height; y++){
				for(int x = 0; x < width; x++){
					double[] intVec= labelMat.get(y, x);
					if(intVec[0] == 0.0) continue;
					
					Double newVal = finalRegionMap.get(intVec[0]);
	
	//				labelMat.put(y, x, new double[] {newVal, newVal, newVal});
	//				if(regionSet.containsKey(intVec[0])) {
	//					if(intVec[0] != newVal){
	//						regionSet.put(newVal, regionSet.get(intVec[0]));
	//						regionSet.remove(intVec[0]);
	//					}
	//				}
				}
			}
			
			labelMat.assignTo(inputMat);
		} else {
			
//			inputMat.assignTo(labelMat);

			Mat mask = Mat.zeros(labelMat.rows()+2, labelMat.cols()+2, CvType.CV_8UC(1));
			
			int label_count = 2; // starts at 2 because 0,1 are used already
            
		    for(int y = 0; y < labelMat.height(); y++) {
		        for(int x=0; x < labelMat.width(); x++) {
//		        	System.out.println(labelMat.get(y, x)[0]);
		            if(inputMat.get(y, x)[0] != 1) {
		                continue;
		            }
		            

		            ArrayList<Point> blob = new ArrayList<Point>();

		            Rect rect = new Rect();
		            Imgproc.floodFill(inputMat, mask, new org.opencv.core.Point(x, y), new Scalar(label_count), rect, new Scalar(0), new Scalar(0), Imgproc.FLOODFILL_FIXED_RANGE);

		            for(int i=rect.y; i < (rect.y+rect.height); i++) {
		                for(int j=rect.x; j < (rect.x+rect.width); j++) {
		                    if(inputMat.get(i, j)[0] != label_count) {
		                        continue;
		                    }

		                    blob.add(new Point(j,i));
		                }
		            }

		            regionSet.put(label_count + 0.0, blob);

		            label_count++;
		            
		            
		        }
		    }
		    List<Mat> channel = new ArrayList<Mat>(3);
            Core.split(inputMat, channel);
            channel.set(1, channel.get(0));
            channel.set(2, channel.get(0));
            
            Core.merge(channel, inputMat);
		}
			
		System.out.println("Setting Regions: " + regionSet.size());
		componentManager.setRegistryData("REGION_SET", regionSet);
		textField.setText("" + regionSet.size());
	}

	private TreeMap<Double, Double> collapseCollisions(Mat labelMat) {
		
		TreeMap<Double, Double> newMap = new TreeMap<Double, Double>();
		for(Entry<Double, TreeSet<Double>> e : collisionMap.entrySet()){
			Double nextVal = e.getValue().first();
			while(collisionMap.containsKey(nextVal)) {
				nextVal = collisionMap.get(nextVal).first();
			}
			newMap.put(e.getKey(), nextVal);
		}
		
		TreeSet<Double> unique = new TreeSet<Double>(newMap.values());
		orderedReplacementMap = new HashMap<Double, Double>();
		double idx = 1;
		for(Double e : unique){
			orderedReplacementMap.put(e, idx);
			idx++;
		}
		
		return newMap;
	}

	private double updateCurrentLabel(Mat imageMat, Mat labelMat, int x, int y) {
		double newLabel = currentLabel; 
		
		double[] intVec = new double[] {0, 0, 0, 0};
		
		if(x>0){
			intVec[0] = labelMat.get(y  , x-1)[0];
			if(y>0){
				intVec[1] = labelMat.get(y-1, x-1)[0];
			}
		}
		if(y>0){
			intVec[2] = labelMat.get(y-1, x  )[0];
			if(x < labelMat.width()-1){
				intVec[3] = labelMat.get(y-1, x+1)[0];
			}
		}
		
		if( Arrays.equals(intVec, new double[] {0, 0, 0, 0}) ){
			newLabel = ++currentLabel;
			return newLabel;
		}
		
		Set<Double> neighborSet = new HashSet<Double>(4);
		for(int i = 0; i < 4; i++){
			if(intVec[i] != 0){
				if(!neighborSet.contains(intVec[i])){
					neighborSet.add(intVec[i]);
				}
			}
		}
		
		ArrayList<Double> neighbors = new ArrayList<Double>(neighborSet);
		Collections.sort(neighbors);
		if(neighbors.size() > 1){
			for(int i = 1; i < neighbors.size(); i++){
				if(!collisionMap.containsKey(neighbors.get(i))) collisionMap.put(neighbors.get(i), new TreeSet<Double>());
				collisionMap.get(neighbors.get(i)).add(neighbors.get(0));
			}
		}
		
		return neighbors.get(0);
	}

	private void reset(Mat srcMat, boolean newMethod) {
		int offset = 0;
//		if(newMethod) offset  = 2;
		currentLabel = 1;
		labelMat = Mat.zeros(srcMat.rows()+offset, srcMat.cols()+offset, srcMat.type());
		collisionMap.clear();
		orderedReplacementMap.clear();
	}

}
