package ann;

import java.io.File;

import javax.swing.JFileChooser;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.CvANN_MLP;

public class DiceClassifier {
	private File dir;
	private int regSize; // the region size to use
	private boolean greyScale;
	private int[] layerSizes;
	private Mat inputs, outputs;
	
	boolean trained;
	private CvANN_MLP ann = new CvANN_MLP();

//	public DiceClassifier() { // may want to make without dir if loading saved ANN
//	}
		
	public DiceClassifier(File dir,int regSize,int[] layerSizes) {
		this.dir = dir;
		this.regSize = regSize;
		this.greyScale = true;
		this.layerSizes = layerSizes;
		trained = false;
	}
		
	/**
	 * Loads images from file and converts them to training samples 
	 * @return 
	 */
	public boolean loadSamples(){
		File sampleDir = null;
		File[] dirListing = null;
		Mat sample;
		
		// no way to tell if a file is already in the matrix so just start over on reloads
		inputs = null; 
		outputs = null;
		
		// Setting up output matrix for each possible die value
		Mat m1 = Mat.zeros(1, 6, CvType.CV_32FC1);
		m1.put(0, 0, 1);
		Mat m2 = Mat.zeros(1, 6, CvType.CV_32FC1);
		m2.put(0, 1, 1);
		Mat m3 = Mat.zeros(1, 6, CvType.CV_32FC1);
		m3.put(0, 2, 1);
		Mat m4 = Mat.zeros(1, 6, CvType.CV_32FC1);
		m4.put(0, 3, 1);
		Mat m5 = Mat.zeros(1, 6, CvType.CV_32FC1);
		m5.put(0, 4, 1);
		Mat m6 = Mat.zeros(1, 6, CvType.CV_32FC1);
		m6.put(0, 5, 1);
		
		// store them for easy access later 
		Mat[] out = new Mat[]{m1, m2, m3, m4, m5, m6};
		
		
		for (int dieValue = 1; dieValue <= 6; dieValue++) {
			sampleDir = new File(dir.getAbsolutePath() + "/" + dieValue
					+ "/regions/" + regSize);
			dirListing = sampleDir.listFiles();
			System.out.println(sampleDir);

			if (dirListing != null) {
				for (File img : dirListing) {
					sample = Highgui.imread(img.getAbsolutePath());
					if (greyScale)
						Imgproc.cvtColor(sample, sample, Imgproc.COLOR_RGB2GRAY);
					sample = sample.reshape(1, 1);
					if (inputs == null){
						inputs = sample; // Start with the very first sample
						outputs = out[dieValue-1].clone();
					}
					else{
						inputs.push_back(sample); // append all the others
						outputs.push_back(out[dieValue-1].clone());
					}
				}
			} else {
				System.err.println("Sample die images missing value = " + dieValue);
				return false;
			}
		}
		//inputs must be Floating point for ANN training 
		inputs.convertTo(inputs, CvType.CV_32FC1);
		return true;
	}
	
	private Mat buildLayerMat(){
		Mat layers = new Mat(1, layerSizes.length+2, 4);
		layers.put(0, 0, inputs.cols());
		for(int i = 0; i < layerSizes.length; i++){
			layers.put(0, i+1, layerSizes[i]);
		}
		layers.put(0, layers.cols()-1, outputs.cols());
		return layers;
	}
	
	public void train(){
		if((inputs == null || outputs == null) && (dir != null)){
			loadSamples();
		}
		ann.create(buildLayerMat());
		ann.train(inputs, outputs, Mat.ones(1, inputs.rows(), inputs.type()));
		
		trained = true;
	}
	
	public int classify(Mat m){
		if(trained){
			m = m.reshape(1,1);
			System.out.println(m);
			Mat classifications = new Mat(m.rows(),m.cols(),CvType.CV_32FC1); 
			System.out.println(classifications);
			System.out.println(inputs);
			ann.predict(m, classifications);
			
			double val = -10;
			int index = -1;
			
			for(int i = 0; i < 6; i++){
				double newVal = classifications.get(0, i)[0];
				if(newVal > val){
					val = newVal;
					index = i;
				}
			}
			return index+1;
		}
		return 0;
	}
	
	public Mat classifyRaw(Mat m){
		Mat classifications = new Mat(0,0,CvType.CV_32FC1);
		if(trained){ 
			ann.predict(m, classifications);
		}
		return classifications;
	}
	
	public void setLayerSizes(int[] layerSizes) {
		this.layerSizes = layerSizes;
		ann.create(buildLayerMat());
	}
	
	public int[] getLayerSizes(){
		return this.layerSizes;
	}
	
	public static void main(String[] args) {
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		File directory = null;
		
		JFileChooser fChoose = new JFileChooser();
		fChoose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

		int fileChooseResult = fChoose.showDialog(null, "Select Directory");

		switch (fileChooseResult) {
		case JFileChooser.APPROVE_OPTION:
			System.out.println("Approved!");
			directory = fChoose.getSelectedFile();
			break;
		case JFileChooser.ERROR_OPTION:
			System.err
					.println("Error occurred opening selected file or folder. Exiting. ");
			System.exit(1);
		case JFileChooser.CANCEL_OPTION:
			System.err.println("File selection cancelled. Exiting. ");
			System.exit(1);
		default:
			break;
		}
		DiceClassifier dc = new DiceClassifier(directory, 16, new int[]{20, 15});
		dc.loadSamples();
		dc.train();
		
		System.out.println(dc.classify(dc.inputs.row(10)));
		System.out.println(dc.classifyRaw(dc.inputs.row(10)).dump());
		
	} 



}
