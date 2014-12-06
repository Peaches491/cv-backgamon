package components;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.ml.CvANN_MLP;

import components.base.Component;
import components.base.ProcessInfo;

public class NNComponent extends Component {

	public NNComponent(){
		super();
		setTitle("Neural Net");
	}
	
	@Override
	public void applyComponent(Mat inputMat, ProcessInfo info) {
		
		Mat layers = new Mat(new Size(3, 1), CvType.CV_32SC1);
		layers.put(0, 0, 21);
		layers.put(0, 1, 15);
		layers.put(0, 2, 7);
		
//		System.out.println(layers.dump());
		
		CvANN_MLP x = new CvANN_MLP();
		x.create(layers);
		
//		x.t
	}

}
