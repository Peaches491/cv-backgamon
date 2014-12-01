package components;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.Point;
import org.opencv.core.RotatedRect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.features2d.FeatureDetector;
import org.opencv.features2d.Features2d;
import org.opencv.highgui.Highgui;
import org.opencv.highgui.VideoCapture;
import org.opencv.imgproc.Imgproc;

import components.base.Component;
import net.miginfocom.swing.MigLayout;

import javax.swing.JLabel;
import javax.swing.JSlider;
import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

@SuppressWarnings("serial")

/*
 * performs a WarpPerspective transform
 * references:
 * http://docs.opencv.org/modules/imgproc/doc/geometric_transformations.html#Mat getPerspectiveTransform(InputArray src, InputArray dst)
 * http://stackoverflow.com/questions/17637730/android-opencv-getperspectivetransform-and-warpperspective
 */
public class WarpPerspectiveComponent extends Component implements ChangeListener {

	public WarpPerspectiveComponent(){
		this.setLayout(new MigLayout("fillx", "[grow][]", "[][]"));

		this.setTitle("WarpPerspective");

	}

	@Override
	public void stateChanged(ChangeEvent arg0) {

	}

	@Override
	public void applyComponent(Mat ioMat) {

		Mat src_mat=new Mat(4,1,CvType.CV_32FC2);
		Mat dst_mat=new Mat(4,1,CvType.CV_32FC2);


		src_mat.put(0,0,407.0,74.0,1606.0,74.0,420.0,2589.0,1698.0,2589.0);
		dst_mat.put(0,0,0.0,0.0,1600.0,0.0, 0.0,2500.0,1600.0,2500.0);
		Mat perspectiveTransform=Imgproc.getPerspectiveTransform(src_mat, dst_mat);

		Mat srcMat=ioMat.clone();
		warpPerspective(srcMat, ioMat, perspectiveTransform, ioMat.size());
	}



	/*
	 * Calculates a perspective transform from four pairs of the corresponding points.
	 */
	static Mat	getPerspectiveTransform(Mat src, Mat dst) {
		return Imgproc.getPerspectiveTransform( src,  dst);
	}

	/*
	 * Applies a perspective transformation to an image.
	 */
	static void	warpPerspective(Mat src, Mat dst, Mat M, Size dsize) {
		Imgproc.warpPerspective( src,  dst,  M,  dsize);
	}

	/*
	 * Applies a perspective transformation to an image.
	 */
	static void	warpPerspective(Mat src, Mat dst, Mat M, Size dsize, int flags) {
		Imgproc.warpPerspective( src,  dst,  M,  dsize,  flags);
	}

	/*
	 * Applies a perspective transformation to an image.
	 */
	static void	warpPerspective(Mat src, Mat dst, Mat M, Size dsize, int flags, int borderMode, Scalar borderValue) {
		Imgproc.warpPerspective( src,  dst,  M,  dsize,  flags,  borderMode,  borderValue);
	}
}
