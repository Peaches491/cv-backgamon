package components;

import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class ContrastAdjustComponent extends Component {

	public ContrastAdjustComponent(){
		this.setTitle("Contrast Adj.");
	}
	
	@Override
	public void applyComponent(Mat inputMat) {

		Imgproc.cvtColor(inputMat, inputMat, Imgproc.COLOR_BGR2GRAY);
		Imgproc.equalizeHist(inputMat, inputMat);
	}

}
