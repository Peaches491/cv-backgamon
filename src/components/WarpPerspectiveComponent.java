package components;

import java.awt.Dimension;
import java.util.ArrayList;
import java.util.List;

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

import components.BoundingBoxComponent.Region;
import components.base.Component;
import components.base.ProcessInfo;
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
 * http://docs.opencv.org/modules/calib3d/doc/camera_calibration_and_3d_reconstruction.html#
 * http://docs.opencv.org/trunk/doc/tutorials/calib3d/real_time_pose/real_time_pose.html
 * http://docs.opencv.org/modules/imgproc/doc/geometric_transformations.html#Mat getPerspectiveTransform(InputArray src, InputArray dst)
 * http://stackoverflow.com/questions/17637730/android-opencv-getperspectivetransform-and-warpperspective
 *
 */
public class WarpPerspectiveComponent extends Component implements ChangeListener {

	SlideAndTextField slideAndText0x;
	SlideAndTextField slideAndText1x;
	SlideAndTextField slideAndText2x;
	SlideAndTextField slideAndText3x;

	SlideAndTextField slideAndText0y;
	SlideAndTextField slideAndText1y;
	SlideAndTextField slideAndText2y;
	SlideAndTextField slideAndText3y;

	private class SlideAndTextField {
		public JTextField textField;
		public JSlider slider;

		public SlideAndTextField(Integer id, final String name, 
				int initVal, WarpPerspectiveComponent wpc) {

			textField = new JTextField();
			textField.setHorizontalAlignment(SwingConstants.CENTER);
			textField.setEditable(false);
			textField.setColumns(6);

			slider = new JSlider();
			slider.setMinimum(-100);
			slider.setMaximum(100);


			slider.addChangeListener(new ChangeListener() {
				public void stateChanged(ChangeEvent e) {
					textField.setText(name + slider.getValue());
					notifyChange();
				}
			});

			slider.setValue(initVal);

			wpc.add(textField, "cell 0 " + id.toString());
			wpc.add(slider,   "cell 1 " + id.toString());
		}
	}



	/*
	 * PointPair - a pair of points which constitute a box
	 */
	class PointQuad{
		List <Point> point = new ArrayList<Point>();

		public PointQuad(Point point0, Point point1, Point point2, Point point3) {
			set(0,point0);
			set(1,point1);
			set(2,point2);
			set(3,point3);
		}
		public void set(int ptn, Point pt) {
			point.add(ptn, pt);		
		}
		public Point get(int ptn) {
			return point.get(ptn);
		}
	}




	public WarpPerspectiveComponent(){
		super();

		this.setLayout(new MigLayout("fillx", "[][]", "[][]"));
		this.setTitle("WarpPerspective");

		//		slideAndText0x = new SlideAndTextField(0, "p0(x)", 65, this);
		//		slideAndText0y = new SlideAndTextField(1, "p0(y)", 65, this);
		//		slideAndText1x = new SlideAndTextField(2, "p1(x)", 190, this);
		//		slideAndText1y = new SlideAndTextField(3, "p1(y)", 190, this);
		//		slideAndText2x = new SlideAndTextField(4, "p2(x)", 180, this);
		//		slideAndText2y = new SlideAndTextField(5, "p2(y)", 180, this);
		//		slideAndText3x = new SlideAndTextField(6, "p3(x)", 60, this);
		//		slideAndText3y = new SlideAndTextField(7, "p3(y)", 60, this);

		slideAndText0x = new SlideAndTextField(0, "p0(x)", 30, this); slideAndText0y = new SlideAndTextField(1, "p0(y)", 0, this);
		slideAndText1x = new SlideAndTextField(2, "p1(x)", 0, this); slideAndText1y = new SlideAndTextField(3, "p1(y)", 0, this);
		slideAndText2x = new SlideAndTextField(4, "p2(x)", 0, this); slideAndText2y = new SlideAndTextField(5, "p2(y)", 0, this);
		slideAndText3x = new SlideAndTextField(6, "p3(x)", -30, this); slideAndText3y = new SlideAndTextField(7, "p3(y)", 0, this);
	}

	@Override
	public void stateChanged(ChangeEvent arg0) {

	}

	@Override
	public void applyComponent(Mat ioMat, ProcessInfo info) {

		// we need
		// four points each in [src,dst] to
		// define the perspective transform matrix

		// source box we just set to be a square
		// 60% of the src matrix
		//	int colCtr = ioMat.cols()/2;
		//	int colDel = ioMat.cols() * 100 / 30;
		//	int rowCtr = ioMat.rows()/2;
		//	int rowDel = ioMat.rows() * 100 / 30;

		PointQuad ppSrc = new PointQuad(
				new Point(0,           0),
				new Point(0,           ioMat.cols()),
				new Point(ioMat.rows(),ioMat.cols()),
				new Point(ioMat.rows(),0));

		int rs = ioMat.rows();
		int cs = ioMat.cols();

//		PointQuad ppDst = new PointQuad(
//				new Point(0,           0),
//				new Point(0,           ioMat.cols()),
//				new Point(ioMat.rows(),ioMat.cols()),
//				new Point(ioMat.rows(),0));
		
		PointQuad ppDst = new PointQuad(
				new Point(0            + slideAndText0x.slider.getValue() * rs / 100, 0            + slideAndText0y.slider.getValue() * cs / 100),
				new Point(0            + slideAndText1x.slider.getValue() * rs / 100, ioMat.cols() + slideAndText1y.slider.getValue() * cs / 100),
				new Point(ioMat.rows() + slideAndText2x.slider.getValue() * rs / 100, ioMat.cols() + slideAndText2y.slider.getValue() * cs / 100),
				new Point(ioMat.rows() + slideAndText3x.slider.getValue() * rs / 100, 0            + slideAndText3y.slider.getValue() * cs / 100));

		Mat src_mat=new Mat(4,1,CvType.CV_32FC2);
		Mat dst_mat=new Mat(4,1,CvType.CV_32FC2);

		src_mat.put(0,0,
				ppSrc.get(0).x, ppSrc.get(0).y,
				ppSrc.get(1).x, ppSrc.get(1).y,
				ppSrc.get(2).x, ppSrc.get(2).y,
				ppSrc.get(3).x, ppSrc.get(3).y);
		dst_mat.put(0,0,
				ppDst .get(0).x, ppDst .get(0).y,
				ppDst .get(1).x, ppDst .get(1).y,
				ppDst .get(2).x, ppDst .get(2).y,
				ppDst .get(3).x, ppDst .get(3).y);

		// generate the 3x3 perspective transform matrix
		Mat perspectiveTransform=Imgproc.getPerspectiveTransform(src_mat, dst_mat);

		//System.out.println("in WarpPerspectiveComponent...");
		//System.out.println(perspectiveTransform.toString());




		// copy the source image and apply the transform
		Mat srcMat=ioMat.clone();
		warpPerspective(srcMat, ioMat, perspectiveTransform, ioMat.size());

		////////////////
		// draw a box

		Core.rectangle(info.getOverlayMat(),
				ppSrc.get(0), ppSrc.get(3),
				new Scalar(0, 0, 255));

		Core.rectangle(info.getOverlayMat(),
				ppDst.get(0), ppDst.get(3),
				new Scalar(0, 0, 255));

		Core.putText(info.getOverlayMat(),
				"some text!",
				ppSrc.get(3),
				Core.FONT_HERSHEY_DUPLEX, 0.5,
				new Scalar(255, 255, 255), 1,
				Core.LINE_AA, false);



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
