package components;

import java.util.ArrayList;
import java.util.List;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import components.base.Component;

@SuppressWarnings("serial")
public class ContrastAdjustComponent extends Component {

	public ContrastAdjustComponent(){
		this.setTitle("Contrast Adj.");
	}
	
	@Override
	public void applyComponent(Mat inputMat) {

		Imgproc.cvtColor(inputMat, inputMat, Imgproc.COLOR_BGR2YCrCb);
		
		List<Mat> mv = new ArrayList<Mat>(3);
		Core.split(inputMat, mv);
		
		Imgproc.equalizeHist(mv.get(0), mv.get(0));
		Core.merge(mv, inputMat);
		

		Imgproc.cvtColor(inputMat, inputMat, Imgproc.COLOR_YCrCb2BGR);
		
	}

}