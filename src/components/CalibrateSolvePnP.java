package components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import javax.swing.Icon;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JTextArea;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import net.miginfocom.swing.MigLayout;

import org.opencv.calib3d.Calib3d;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.MatOfDouble;
import org.opencv.core.MatOfFloat;
import org.opencv.core.MatOfPoint;
import org.opencv.core.MatOfPoint2f;
import org.opencv.core.MatOfPoint3f;
import org.opencv.core.Point;
import org.opencv.core.Point3;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import components.base.Component;

/*
 * This example of camera calibration 
 * 
 * This example is taken directly from http://computervisionandjava.blogspot.com/
 * 
 * reference (opencv java api)
 * http://docs.opencv.org/java/
 * 
 * other references:
 * http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#bool solvePnP(InputArray objectPoints, InputArray imagePoints, InputArray cameraMatrix, InputArray distCoeffs, OutputArray rvec, OutputArray tvec, bool useExtrinsicGuess, int flags)
 * http://dsp.stackexchange.com/questions/16767/is-it-possible-to-convert-from-2d-to-3d-form
 * 
 * http://docs.opencv.org/trunk/index.html (see python)
 * http://docs.opencv.org/trunk/doc/tutorials/calib3d/table_of_content_calib3d/table_of_content_calib3d.html#table-of-content-calib3d
 * http://docs.opencv.org/trunk/doc/py_tutorials/py_calib3d/py_calibration/py_calibration.html
 * http://docs.opencv.org/trunk/doc/py_tutorials/py_calib3d/py_pose/py_pose.html
 * http://computervisionandjava.blogspot.com/
 * http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#
 * http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#calibratecamera
 * http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#findchessboardcorners
 * http://docs.opencv.org/trunk/doc/tutorials/calib3d/real_time_pose/real_time_pose.html
 */
@SuppressWarnings("serial")
public class CalibrateSolvePnP  extends Component implements ChangeListener {


	Mat cameraMatrix = new Mat();
	Mat distCoeffs2 = new MatOfDouble();

	MatOfPoint3f	corners3f = new MatOfPoint3f();
	MatOfPoint2f	corners2f = new MatOfPoint2f();


	Mat rvec;
	Mat tvec;
	Mat rmat;

	Mat perspectiveTransform;


	private JLabel labelObjectMatrix;
	private JTextArea textObjectMatrix;

	private JLabel labelImageMatrix;
	private JTextArea textImageMatrix;

	private JLabel labelCameraMatrix;
	private JTextArea textCameraMatrix;

	private JLabel labelDistCoeff;
	private JTextArea textDistCoeff;

	private JButton buttonUpdateTarget;
	private JTextArea textUpdateTarget;

	private JCheckBox checkBoxShowObjecPoints;

	private JButton buttonRunPnP;
	private JTextArea textRunPnp;

	private JLabel labelRvec;
	private JTextArea textRvec;

	private JLabel labelTvec;
	private JTextArea textTvec;

	private JLabel labelRmat;
	private JTextArea textRmat;

	private JCheckBox checkBoxShowAxis;
	private JCheckBox checkBoxPerspectiveTransform;


	public CalibrateSolvePnP(CalibrateChessBoard intrinsic) {
		super();

		// camera matrix and dist coeffs are presumed to have been
		// computed in a prior stage
		// get their reference here
		cameraMatrix = intrinsic.getCameraMatrix();
		distCoeffs2 = intrinsic.getDistCoeffs();


		this.setLayout(new MigLayout("fillx", "[grow][]", "[][]"));
		this.setTitle("CalibrateSolvePnP");

		buttonUpdateTarget = new JButton ("Update Target");
		buttonUpdateTarget.addActionListener(new ButtonListener());
		buttonUpdateTarget.addChangeListener(this);
		textUpdateTarget = new JTextArea();

		labelObjectMatrix = new JLabel ("Object Points");
		textObjectMatrix = new JTextArea();

		labelImageMatrix = new JLabel ("Image Points");
		textImageMatrix  = new JTextArea();

		labelCameraMatrix  = new JLabel ("Camera Matrix");
		textCameraMatrix = new JTextArea();

		labelDistCoeff = new JLabel ("Distance Coefficients");
		textDistCoeff = new JTextArea();

		checkBoxShowObjecPoints = new JCheckBox("Show Object Points");
		checkBoxShowObjecPoints.addActionListener(new ButtonListener());
		checkBoxShowObjecPoints.addChangeListener(this);

		buttonRunPnP = new JButton ("Run PnP");
		buttonRunPnP.addActionListener(new ButtonListener());
		buttonRunPnP.addChangeListener(this);
		textRunPnp = new JTextArea();

		labelRvec = new JLabel ("Rotation Vector");
		textRvec = new JTextArea();

		labelTvec = new JLabel ("Translation Vector");
		textTvec = new JTextArea();

		labelRmat = new JLabel ("Rotation Matrix");
		textRmat = new JTextArea();

		checkBoxShowAxis = new JCheckBox("Show Axis");
		checkBoxShowAxis.addActionListener(new ButtonListener());
		checkBoxShowAxis.addChangeListener(this);

		checkBoxPerspectiveTransform = new JCheckBox("Perspective Transform");
		checkBoxPerspectiveTransform.addActionListener(new ButtonListener());
		checkBoxPerspectiveTransform.addChangeListener(this);


		this.add(buttonUpdateTarget, "cell 0 0");
		this.add(textUpdateTarget, "cell 1 0");

		this.add(labelObjectMatrix, "cell 0 1");
		this.add(textObjectMatrix, "cell 1 1");

		this.add(labelImageMatrix, "cell 0 2");
		this.add(textImageMatrix, "cell 1 2");

		this.add(labelCameraMatrix, "cell 0 3");
		this.add(textCameraMatrix, "cell 1 3");

		this.add(labelDistCoeff, "cell 0 4");
		this.add(textDistCoeff, "cell 1 4");

		this.add(checkBoxShowObjecPoints, "cell 0 5");

		this.add(buttonRunPnP, "cell 0 6");
		this.add(textRunPnp, "cell 1 6");

		this.add(labelRvec, "cell 0 7");
		this.add(textRvec, "cell 1 7");

		this.add(labelTvec, "cell 0 8");
		this.add(textTvec, "cell 1 8");

		this.add(labelRmat, "cell 0 9");
		this.add(textRmat, "cell 1 9");

		this.add(checkBoxShowAxis, "cell 0 10");

		this.add(checkBoxPerspectiveTransform, "cell 0 11");


	}


	class ButtonListener implements ActionListener {
		ButtonListener() {
		}

		public void actionPerformed(ActionEvent e) {

			if (e.getActionCommand().equals("Update Target")) {

				// status box
				textUpdateTarget.setText("");

				// intrinsic data
				textCameraMatrix.setText(cameraMatrix.dump());
				textDistCoeff.setText(distCoeffs2.dump());


				// 3d and 2d points (text boxes)	
				//zona	corners3f = new MatOfPoint3f();
				//zona	corners2f = new MatOfPoint2f();
				createObjectAndImagePoints(corners3f, corners2f);
				textObjectMatrix.setText(corners3f.dump());
				textImageMatrix.setText(corners2f.dump());


				textUpdateTarget.setText("ok");
			}

			if (e.getActionCommand().equals("Show Object Points")) {
				//checkBoxShowObjecPoints.setSelected(false);
				checkBoxShowAxis.setSelected(false);
				checkBoxPerspectiveTransform.setSelected(false);
			}

			if (e.getActionCommand().equals("Run PnP")) {

				rvec = new Mat();
				tvec = new Mat();
				solvePnpClassSpecific(corners3f, corners2f, rvec, tvec);

				// update ui text fields with results
				textRvec.setText(rvec.dump());
				textTvec.setText(tvec.dump());

				// calculate rmat from rvec
				rmat = new Mat();
				Calib3d.Rodrigues(rvec,rmat);
				textRmat.setText(rmat.dump());

				textRunPnp.setText("ok");
			}

			if (e.getActionCommand().equals("Show Axis")) {
				checkBoxShowObjecPoints.setSelected(false);
				// checkBoxShowAxis.setSelected(false);
				checkBoxPerspectiveTransform.setSelected(false);

				; // do nothing
			}

			if (e.getActionCommand().equals("Perspective Transform")) {

				checkBoxShowObjecPoints.setSelected(false);
				checkBoxShowAxis.setSelected(false);
				// checkBoxPerspectiveTransform.setSelected(false);

				if (corners2f == null) {
					System.err.println("when applying perspective transform corners2f is null");
					return;
				}

				// calculate the warp perspective - we use a temporary point for this
				double d2SpaceX = 1280;
				double d2SpaceY = 740;

				double d2xCtr = d2SpaceX/2;
				double d2xSizBack  = d2SpaceX/2;
				double d2xSizFront = d2SpaceX/2;
				double d2yCtr = d2SpaceY/2;
				double d2ySiz = d2SpaceY/2;

				Point[] points2d = new Point[4];
				points2d[0] = new Point(d2xCtr-d2xSizFront, d2yCtr+d2ySiz);
				points2d[1] = new Point(d2xCtr-d2xSizBack,  d2yCtr-d2ySiz);
				points2d[2] = new Point(d2xCtr+d2xSizBack,  d2yCtr-d2ySiz);
				points2d[3] = new Point(d2xCtr+d2xSizFront, d2yCtr+d2ySiz);
				MatOfPoint2f c2f = new MatOfPoint2f();
				c2f.fromArray(points2d);

				// Finds a perspective transformation between two planes.
				// corners2f is the baseline - used elsewhere
				// c2f is local - can play with that
				perspectiveTransform = Calib3d.findHomography(corners2f, c2f);
			}
		}
	}




	void createObjectAndImagePoints(MatOfPoint3f corners3f,
			MatOfPoint2f corners2f) {

		//		ArrayList objectPoints = new ArrayList();
		//		ArrayList imagePoints = new ArrayList();

		getCorners2f3f(corners3f, corners2f);

		//		objectPoints = new ArrayList();
		//		objectPoints.add(corners3f);

		//		imagePoints = new ArrayList();
		//		imagePoints.add(corners2f);

	}

	protected void getCorners2f3f(MatOfPoint3f c3f, MatOfPoint2f c2f) {

		// our 3-d points
		Point3[] points3d = new Point3[4];

		double d3SpaceZ = 610;

		double xCtr = 0;
		double xSiz = 456/2;
		double zCtr = d3SpaceZ/2;
		double zSiz = 508/2;

		points3d[0] = new Point3(xCtr-xSiz, 0, zCtr-zSiz);
		points3d[1] = new Point3(xCtr-xSiz, 0, zCtr+zSiz);
		points3d[2] = new Point3(xCtr+xSiz, 0, zCtr+zSiz);
		points3d[3] = new Point3(xCtr+xSiz, 0, zCtr-zSiz);
		c3f.fromArray(points3d);

		// our accompanying 2-d points (in pixels)
		double d2SpaceX = 1280;
		double d2SpaceY = 740;

		double d2xCtr = d2SpaceX/2;
		double d2xSizBack  =  d2SpaceX*35/100; // it is this that creates the perspective
		double d2xSizFront = d2SpaceX*80/100;
		double d2yCtr = d2SpaceY/2;
		double d2ySiz = d2SpaceY*45/100;

		Point[] points2d = new Point[4];
		points2d[0] = new Point(d2xCtr-d2xSizFront, d2yCtr+d2ySiz);
		points2d[1] = new Point(d2xCtr-d2xSizBack,  d2yCtr-d2ySiz);
		points2d[2] = new Point(d2xCtr+d2xSizBack,  d2yCtr-d2ySiz);
		points2d[3] = new Point(d2xCtr+d2xSizFront, d2yCtr+d2ySiz);
		c2f.fromArray(points2d);
	}

	/*
	 * solve PnP applying class variables
	 * takes camera matrix from "this" class
	 * and saves rvec,tvec (outputs) to "this" class
	 */
	boolean solvePnpClassSpecific(
			MatOfPoint3f objectPoints,
			MatOfPoint2f imagePoints,
			Mat rvec,
			Mat tvec) {

		// camera matrix and distance coefficients are
		// are presumed to have been calculated in prior phase
		// sanity check...
		if ((cameraMatrix == null) || (distCoeffs2 == null)) {
			System.err.println("in CalibrateSolvePnP - fails sanity check - cameraMatrix or distCoeffs are null - did you calibrate?");
			int foo = 1/0;
		}
		if ((cameraMatrix.size().width != 3)
				|| (cameraMatrix.size().height != 3)
				|| (distCoeffs2.size().width != 1)
				|| (distCoeffs2.size().height != 5)) {
			System.err.println("in CalibrateSolvePnP - fails sanity check - cameraMatrix or distCoeffs are incorrect size"
					+ " " + cameraMatrix.size().width
					+ " " + cameraMatrix.size().height
					+ " " + distCoeffs2.size().width
					+ " " + distCoeffs2.size().height);
			int foo = 1/0;
		}

		// run the PnP
		return solvePnP( objectPoints,
				imagePoints,
				this.cameraMatrix,
				new MatOfDouble(this.distCoeffs2),  // convert to MatOfDouble
				rvec,
				tvec);
	}


	/*
	 * run the solvePnP algorithm, given object and image points
	 * camera matrix and distance coefficients
	 * returns rvec (rotation vector) and tvec (translation vector)
	 */
	boolean solvePnP(
			MatOfPoint3f objectPoints,
			MatOfPoint2f imagePoints,
			Mat cameraMatrix,
			MatOfDouble distCoeffs,
			Mat rvec,
			Mat tvec) {

		boolean useExtrinsicGuess = false;
		int flags = Calib3d.CV_P3P;

		return Calib3d.solvePnP(objectPoints, imagePoints, cameraMatrix, distCoeffs, rvec, tvec, useExtrinsicGuess, flags);
	}



	@Override
	public void applyComponent(Mat inputMat) {

		/////////////////////
		// if the user has set the Show Axis button
		if (checkBoxShowObjecPoints.isSelected()) {

			// sanity check				
			if (corners2f == null) {
				return;
			}

			// we are given MatOfPoint2f corners2f
			// need to convert to MatOfPoint
			List<MatOfPoint> pts = new ArrayList<MatOfPoint>();
			MatOfPoint matOfPoint = new MatOfPoint(corners2f.toArray());
			pts.add(matOfPoint);

			//	Draws the target box
			Core.polylines(
					(Mat)componentManager.getRegistryData("OVERLAY_MAT"),
					pts,
					true,
					new Scalar(0, 0, 255),
					10);
		}

		/////////////////////
		// if the user has set the Show Axis button
		if (checkBoxShowAxis.isSelected()) {

			if ((rvec==null) || (tvec==null) || (cameraMatrix==null) || (distCoeffs2==null)) {
				System.err.println("in applyComponent - one of vectors is null - rvec, tvec, cameraMatrix, distCoeff");
				return;
			}

			// Draw line segments along x,y,z axis
			MatOfPoint2f imagePoints;
			MatOfPoint3f objectPoints;

			// X
			objectPoints = new MatOfPoint3f( new Point3(  0,  0,  0), new Point3(100,  0,  0));
			// Projects 3D points to an image plane.
			// static void projectPoints(MatOfPoint3f objectPoints, Mat rvec, Mat tvec, Mat cameraMatrix, MatOfDouble distCoeffs, MatOfPoint2f imagePoints)

			imagePoints = new MatOfPoint2f();
			Calib3d.projectPoints(objectPoints, rvec, tvec, cameraMatrix, new MatOfDouble(distCoeffs2), imagePoints);
			Core.line(
					(Mat)componentManager.getRegistryData("OVERLAY_MAT"),
					imagePoints.toList().get(0),
					imagePoints.toList().get(1),
					new Scalar(0, 255, 255),
					7);
			Core.putText(
					(Mat)componentManager.getRegistryData("OVERLAY_MAT"),
					"  X",
					imagePoints.toList().get(1),
					Core.FONT_HERSHEY_DUPLEX, 0.5,  new Scalar(255, 255, 255), 1, Core.LINE_AA, false);

			// Y			
			imagePoints = new MatOfPoint2f();
			objectPoints = new MatOfPoint3f( new Point3(  0,  0,  0), new Point3(  0,100,  0));
			Calib3d.projectPoints(objectPoints, rvec, tvec, cameraMatrix, new MatOfDouble(distCoeffs2), imagePoints);
			Core.line(
					(Mat)componentManager.getRegistryData("OVERLAY_MAT"),
					imagePoints.toList().get(0),
					imagePoints.toList().get(1),
					new Scalar(0, 255, 255),
					7);
			Core.putText(
					(Mat)componentManager.getRegistryData("OVERLAY_MAT"),
					"  Y",
					imagePoints.toList().get(1),
					Core.FONT_HERSHEY_DUPLEX, 0.5,  new Scalar(255, 255, 255), 1, Core.LINE_AA, false);

			// Z
			imagePoints = new MatOfPoint2f();
			objectPoints = new MatOfPoint3f( new Point3(  0,  0,  0), new Point3(  0,  0,100));
			Calib3d.projectPoints(objectPoints, rvec, tvec, cameraMatrix, new MatOfDouble(distCoeffs2), imagePoints);
			Core.line(
					(Mat)componentManager.getRegistryData("OVERLAY_MAT"),
					imagePoints.toList().get(0),
					imagePoints.toList().get(1),
					new Scalar(0, 255, 255),
					7);
			Core.putText(
					(Mat)componentManager.getRegistryData("OVERLAY_MAT"),
					"  Z",
					imagePoints.toList().get(1),
					Core.FONT_HERSHEY_DUPLEX, 0.5,  new Scalar(255, 255, 255), 1, Core.LINE_AA, false);


		}

		/////////////////////
		// if the user has set the Perspective Transform button
		if (checkBoxPerspectiveTransform.isSelected()) {
			if (perspectiveTransform == null) {
				System.err.println("trying to apply perspective transform - the transform matrix is null");
				return;
			}

			// Applies a perspective transformation to the image.
			Imgproc.warpPerspective(
					inputMat,
					inputMat,
					perspectiveTransform,
					inputMat.size());
		}
	}


	@Override
	public void stateChanged(ChangeEvent e) {
		notifyChange();
	}
}
