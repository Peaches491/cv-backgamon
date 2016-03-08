package components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.JTextField;

import net.miginfocom.swing.MigLayout;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.core.TermCriteria;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import components.base.Component;
import components.base.ProcessInfo;

/*
 * This example of camera calibration 
 * 
 * This example is taken directly from http://computervisionandjava.blogspot.com/
 * 
 * reference (opencv java api)
 * http://docs.opencv.org/java/
 * 
 * other references:
 * http://docs.opencv.org/trunk/index.html (see python)
 * http://docs.opencv.org/trunk/doc/tutorials/calib3d/table_of_content_calib3d/table_of_content_calib3d.html#table-of-content-calib3d
 * http://docs.opencv.org/trunk/doc/py_tutorials/py_calib3d/py_calibration/py_calibration.html
 * http://docs.opencv.org/trunk/doc/py_tutorials/py_calib3d/py_pose/py_pose.html
 * http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#
 * http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#calibratecamera
 * http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#findchessboardcorners
 * http://docs.opencv.org/trunk/doc/tutorials/calib3d/real_time_pose/real_time_pose.html
 */
@SuppressWarnings("serial")
public class CalibrateChessBoard  extends Component {

	private JLabel labelSquareSize;
	private JTextField textFieldSquareSize;

	private JButton buttonCalibrate;

	private JLabel labelErrorReproj;
	private JTextField textFieldErrorReproj;

	private JLabel labelCameraMatrix;
	private JTextArea textFieldCameraMatrix;

	private JLabel labelDistCoeff;
	private JTextField textDistCoeff;


	// CV_CALIB_CB_ADAPTIVE_THRESH Use adaptive thresholding to convert the image to black and white, rather than a fixed threshold level (computed from the average image brightness).
	// CV_CALIB_CB_NORMALIZE_IMAGE Normalize the image gamma with "equalizeHist" before applying fixed or adaptive thresholding.
	// CV_CALIB_CB_FILTER_QUADS Use additional criteria (like contour area, perimeter, square-like shape) to filter out false quads extracted at the contour retrieval stage.
	// CALIB_CB_FAST_CHECK Run a fast check on the image that looks for chessboard corners, and shortcut the call if none is found. This can drastically speed up the call in the degenerate condition when no chessboard is observed.
	int flagsCorner = Calib3d.CALIB_CB_FAST_CHECK;
	//	int flagsCorner = Calib3d.CALIB_CB_ADAPTIVE_THRESH
	//			| Calib3d.CALIB_CB_FAST_CHECK 
	//			| Calib3d.CALIB_CB_NORMALIZE_IMAGE;
	int flagsCalib = Calib3d.CALIB_ZERO_TANGENT_DIST
			| Calib3d.CALIB_FIX_PRINCIPAL_POINT 
			| Calib3d.CALIB_FIX_K4
			| Calib3d.CALIB_FIX_K5;
	TermCriteria criteria = new TermCriteria(TermCriteria.EPS
			+ TermCriteria.MAX_ITER, 40, 0.001);
	Size winSize = new Size(5, 5), zoneSize = new Size(-1, -1);
	Size patternSize = new Size(9, 6);
	ArrayList objectPoints, imagePoints = new ArrayList();
	ArrayList vCorners;
	ArrayList<Mat> vImg;
	Mat cameraMatrix = Mat.eye(3, 3, CvType.CV_64F);
	Mat distCoeffs = Mat.zeros(8, 1, CvType.CV_64F);
	ArrayList rvecs = new ArrayList();
	ArrayList tvecs = new ArrayList();
	Double errReproj;


	public CalibrateChessBoard() {
		super();

		this.setLayout(new MigLayout("fillx", "[grow][]", "[][]"));
		this.setTitle("Calibrate Camera");

		labelSquareSize = new JLabel ("square size (mm)");
		textFieldSquareSize = new JTextField("50");

		buttonCalibrate = new JButton ("Calibrate");
		buttonCalibrate.addActionListener(new ButtonListener());

		labelErrorReproj = new JLabel("reprojection error");
		textFieldErrorReproj = new JTextField("-");

		labelCameraMatrix = new JLabel("camera matrix");
		textFieldCameraMatrix = new JTextArea("-");

		labelDistCoeff = new JLabel("distance coefficients");
		textDistCoeff = new JTextField("-");

		this.add(labelSquareSize, "cell 0 0");
		this.add(textFieldSquareSize, "cell 1 0");

		this.add(buttonCalibrate, "cell 0 1");

		this.add(labelErrorReproj, "cell 0 2");
		this.add(textFieldErrorReproj, "cell 1 2");

		this.add(labelCameraMatrix, "cell 0 3");
		this.add(textFieldCameraMatrix, "cell 1 3");

		this.add(labelDistCoeff, "cell 0 4");
		this.add(textDistCoeff, "cell 1 4");
	}


	class ButtonListener implements ActionListener {
		ButtonListener() {
		}

		public void actionPerformed(ActionEvent e) {
			if (e.getActionCommand().equals("Calibrate")) {

				System.out.println("Calibrate has been clicked");

				getAllCornors("C:/Users/cwinsor/Documents/DanielsHardCodedPath");
				calibrate();

				// update ui text fields				
				textFieldErrorReproj.setText(errReproj.toString());
				textFieldCameraMatrix.setText(cameraMatrix.dump());
				textDistCoeff.setText(distCoeffs.dump());
			}
		}
	}

	void getAllCornors(String path) {
		vImg = new ArrayList<Mat>();
		objectPoints = new ArrayList();
		imagePoints = new ArrayList();
		MatOfPoint3f corners3f = getCorner3f();
		for (File f : new File(path).listFiles()) {
			Mat mat = Highgui.imread(f.getPath(), 
					Highgui.CV_LOAD_IMAGE_COLOR);
			if (mat == null || mat.channels() != 3)
				continue;
			System.out.println("fn = " + f.getPath());
			System.out.println("mat.channels() = " + mat.channels() 
					+ ", " + mat.cols() + ", " + mat.rows());
			Mat gray = new Mat();
			Imgproc.cvtColor(mat, gray, Imgproc.COLOR_BGR2GRAY);

			MatOfPoint2f corners = new MatOfPoint2f();
			if (!getCorners(gray, corners)) {
				System.out.println("no corners found");
				continue;
			}
			System.out.println("adding to list");
			objectPoints.add(corners3f);
			imagePoints.add(corners);
			vImg.add(mat);
		}
	}



	void calibrate() {
		System.out.println("attempting to calibrate - vImg (image list) size = " + vImg.size());
		if (vImg.size() <= 0) {
			System.err.println("------>  cannot calibrate (no image points were found) <------- ");
			return;	
		}
		errReproj = Calib3d.calibrateCamera(objectPoints, 
				imagePoints,vImg.get(0).size(), cameraMatrix, 
				distCoeffs, rvecs, tvecs,flagsCalib);
		System.out.println("done, \nerrReproj = " + errReproj);
		System.out.println("cameraMatrix = \n" + cameraMatrix.dump());
		System.out.println("distCoeffs = \n" + distCoeffs.dump());
	}


	boolean getCorners(Mat gray, MatOfPoint2f corners) {
		if (!Calib3d.findChessboardCorners(gray, patternSize,
				corners, flagsCorner))
			return false;
		Imgproc.cornerSubPix(gray, corners, winSize, zoneSize,
				criteria);
		return true;
	}

	MatOfPoint3f getCorner3f() {
		MatOfPoint3f corners3f = new MatOfPoint3f();
		Double squareSize = Double.parseDouble(textFieldSquareSize.getText());
		Point3[] vp = new Point3[(int) (patternSize.height * 
				patternSize.width)];
		int cnt = 0;
		for (int i = 0; i < patternSize.height; ++i)
			for (int j = 0; j < patternSize.width; ++j, cnt++)
				vp[cnt] = new Point3(j * squareSize, 
						i * squareSize, 0.0d);
		corners3f.fromArray(vp);
		return corners3f;
	}


	@Override
	public void applyComponent(Mat inputMat, ProcessInfo info) {
	

		// draw line showing 2-d pixel points
		MatOfPoint2f matOfPoint2f;

		// X
		matOfPoint2f = new MatOfPoint2f( new Point(0,0), new Point(100,0));
		Core.line(
				(Mat)componentManager.getRegistryData("OVERLAY_MAT"),
				matOfPoint2f.toList().get(0),
				matOfPoint2f.toList().get(1),
				new Scalar(0, 255, 255),
				7);
		Core.putText(
				(Mat)componentManager.getRegistryData("OVERLAY_MAT"),
				"  X",
				new Point(100,10), // offset down by 10 so we see it
				Core.FONT_HERSHEY_DUPLEX, 0.5,  new Scalar(255, 255, 255), 1, Core.LINE_AA, false);

		// Y
		matOfPoint2f = new MatOfPoint2f( new Point(0,0), new Point(0,100));
		Core.line(
				(Mat)componentManager.getRegistryData("OVERLAY_MAT"),
				matOfPoint2f.toList().get(0),
				matOfPoint2f.toList().get(1),
				new Scalar(0, 255, 255),
				7);
		Core.putText(
				(Mat)componentManager.getRegistryData("OVERLAY_MAT"),
				"  Y",
				matOfPoint2f.toList().get(1),
				Core.FONT_HERSHEY_DUPLEX, 0.5,  new Scalar(255, 255, 255), 1, Core.LINE_AA, false);

	
	
	
	
	}



	public Mat getCameraMatrix() {
		return cameraMatrix;
	}


	public Mat getDistCoeffs() {
		return distCoeffs;
	}



}
