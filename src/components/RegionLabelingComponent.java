package components;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;
import java.util.TreeMap;
import java.util.TreeSet;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import components.base.Component;
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
	public void applyComponent(Mat inputMat) {
		reset(inputMat);
		
		Imgproc.cvtColor(inputMat, inputMat, CvType.CV_8UC1);
		Imgproc.threshold(inputMat, inputMat, 0, 1, Imgproc.THRESH_BINARY);
		
		double width = labelMat.size().width;
		double height = labelMat.size().height;
		
		for(int y = 0; y < height; y++){
			for(int x = 0; x < width; x++){
				double[] intVec= inputMat.get(y, x);
				if(intVec[0] == unlabledLabel ||
				   intVec[1] == unlabledLabel ||
				   intVec[2] == unlabledLabel) {
					double l = updateCurrentLabel(inputMat, labelMat, x, y);
					labelMat.put(y, x, new double[] {l, l, l});
				}
			}
		}
		
		TreeMap<Double, Double> collisionRepair = collapseCollisions(labelMat);
		TreeMap<Double, ArrayList<Point>> regionSet = new TreeMap<Double, ArrayList<Point>>();
		
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

	private void reset(Mat srcMat) {
		currentLabel = 1;
		labelMat = Mat.zeros(srcMat.rows(), srcMat.cols(), srcMat.type());
		collisionMap.clear();
		orderedReplacementMap.clear();
	}

}
