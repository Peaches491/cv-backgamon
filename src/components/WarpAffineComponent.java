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
public class WarpAffineComponent extends Component implements ChangeListener {

	private JTextField tfcx;
	private JTextField tfcy;
	private JTextField tfa;
	private JTextField tfs;

	private JSlider centerSliderX;
	private JSlider centerSliderY;
	private JSlider angleSlider;
	private JSlider scaleSlider;

	public WarpAffineComponent(){
		super();

		this.setLayout(new MigLayout("fillx", "[grow][]", "[][]"));
		this.setTitle("WarpAffine");

		//	JTextField tf = new JTextField();
		//	tf.setText("[center(x), center(y), angle(degrees), scale(%)]");
		//	this.add(tf, "cell 0 0,growx");

		// centerX
		tfcx = new JTextField();
		tfcx.setHorizontalAlignment(SwingConstants.CENTER);
		tfcx.setEditable(false);
		//tfcx.setText("CenterX " + centerSliderX.getValue() + "%");
		this.add(tfcx, "cell 0 0,growx");
		tfcx.setColumns(6);

		centerSliderX = new JSlider();
		centerSliderX.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				tfcx.setText("CntrX " + centerSliderX.getValue() + "%");
				notifyChange();
			}
		});
		centerSliderX.setMinimum(0);
		centerSliderX.setMaximum(100);
		centerSliderX.setValue(30);
		this.add(centerSliderX, "cell 1 0,grow");


		// centerY
		tfcy = new JTextField();
		tfcy.setHorizontalAlignment(SwingConstants.CENTER);
		tfcy.setEditable(false);
		//tfcy.setText("CenterY " + centerSliderY.getValue() + "%");
		this.add(tfcy, "cell 0 1,growx");
		tfcy.setColumns(6);

		centerSliderY = new JSlider();
		centerSliderY.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				tfcy.setText("CntrY " + centerSliderY.getValue() + "%");
				notifyChange();
			}
		});
		centerSliderY.setMinimum(0);
		centerSliderY.setMaximum(100);
		centerSliderY.setValue(0);
		this.add(centerSliderY, "cell 1 1,grow");


		// angle
		tfa = new JTextField();
		tfa.setHorizontalAlignment(SwingConstants.CENTER);
		tfa.setEditable(false);
		//tfa.setText("Angle " + angleSlider.getValue());
		this.add(tfa, "cell 0 2,growx");
		tfa.setColumns(6);

		angleSlider = new JSlider();
		angleSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				tfa.setText("Angle " + angleSlider.getValue());
				notifyChange();
			}
		});
		angleSlider.setMaximum(-180);
		angleSlider.setMaximum(180);
		angleSlider.setValue(-10);
		this.add(angleSlider, "cell 1 2,grow");

		// scale
		tfs = new JTextField();
		tfs.setHorizontalAlignment(SwingConstants.CENTER);
		tfs.setEditable(false);
		//tfs.setText("Scale " + scaleSlider.getValue() + "x");
		this.add(tfs, "cell 0 3,growx");
		tfs.setColumns(6);

		scaleSlider = new JSlider();
		scaleSlider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				tfs.setText("Scale " + scaleSlider.getValue() + "%");
				notifyChange();
			}
		});
		scaleSlider.setMinimum(50);
		scaleSlider.setMaximum(200);
		scaleSlider.setValue(75);
		this.add(scaleSlider, "cell 1 3,grow");
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {

	}

	@Override
	public void applyComponent(Mat inputMat) {

		//		Mat inMat = inputMat.clone();

		Point theCenter = new Point();
		theCenter.x = centerSliderX.getValue() * inputMat.cols() / 100;
		theCenter.y = centerSliderY.getValue() * inputMat.rows() / 100;

		Mat rotationMat = getRotationMatrix2D(
				theCenter,
				(double)angleSlider.getValue(),
				(double)scaleSlider.getValue()/100.0);

		warpAffine(inputMat, inputMat, rotationMat, inputMat.size());
		//	warpPerspective(inMat, inputMat, rotationMat, inputMat.size());

	}


	/*
	 * Calculates an affine matrix of 2D rotation.
	 */
	static Mat getRotationMatrix2D(Point center, double angle, double scale) {
		return Imgproc.getRotationMatrix2D( center,  angle,  scale);
	}

	/*
	 * Inverts an affine transformation.
	 */
	static void invertAffineTransform(Mat M, Mat iM) {
		Imgproc.invertAffineTransform( M, iM);
	}

	/*
	 * Applies an affine transformation to an image.
	 */
	static void warpAffine(Mat src, Mat dst, Mat M, Size dsize) {
		Imgproc.warpAffine( src,  dst,  M,  dsize);
	}

	/*
	 * Applies an affine transformation to an image.
	 */
	static void	warpAffine(Mat src, Mat dst, Mat M, Size dsize, int flags) {
		Imgproc.warpAffine( src,  dst,  M,  dsize,  flags);
	}

	/*
	 * Applies an affine transformation to an image.
	 */
	static void	warpAffine(Mat src, Mat dst, Mat M, Size dsize, int flags, int borderMode, Scalar borderValue) {
		Imgproc.warpAffine( src,  dst,  M,  dsize,  flags,  borderMode,  borderValue);
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
