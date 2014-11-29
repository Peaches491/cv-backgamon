package components;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import components.base.DataComponent;

@SuppressWarnings("serial")
public class ContrastAdjustComponent extends DataComponent {

	public ContrastAdjustComponent(){
		this.setTitle("Contrast Adj.");
	}
	
	@Override
	public void doApplyComponent(Mat inputMat) {

		Imgproc.cvtColor(inputMat, inputMat, Imgproc.COLOR_BGR2GRAY);
		Imgproc.equalizeHist(inputMat, inputMat);
	}

}
