package components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import org.opencv.core.Mat;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;

import components.base.Component;
import components.base.ProcessInfo;
import net.miginfocom.swing.MigLayout;

import javax.swing.JSpinner;
import javax.swing.JLabel;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.JToggleButton;
import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

@SuppressWarnings("serial")
public class BinaryRegionTransformationComponent extends Component implements ItemListener, ChangeListener {
	
	public enum Shape {
		CIRCLE,
		SQUARE,
		CROSS;
	};
	private JSpinner dilateSizeSpinner;
	private JSpinner erodeSizeSpinner;

	int kernelMax = 15;
	private JToggleButton chckbxNewCheckBox;
	private JComboBox<Shape> comboBox;
	private JLabel lblKernelShape;
	
	public BinaryRegionTransformationComponent(int dilateSize, int erodeSize, Shape kernelShape, boolean setOpening) {
		setTitle("Binary Operations");
		setLayout(new MigLayout("fill", "[][grow,fill]", "[][][][]"));
		
		JLabel lblNewLabel = new JLabel("Dilate Size");
		add(lblNewLabel, "cell 0 0");
		
		dilateSizeSpinner = new JSpinner();
		dilateSizeSpinner.setModel(new SpinnerNumberModel(dilateSize, 1, kernelMax, 2));
		add(dilateSizeSpinner, "cell 1 0");
		

		JLabel lblErode = new JLabel("Erosion Size");
		add(lblErode, "cell 0 1");
		
		erodeSizeSpinner = new JSpinner();
		erodeSizeSpinner.setModel(new SpinnerNumberModel(erodeSize, 1, kernelMax, 2));
		add(erodeSizeSpinner, "cell 1 1");
		
		dilateSizeSpinner.addChangeListener(this);
		erodeSizeSpinner.addChangeListener(this);
		
		chckbxNewCheckBox = new JToggleButton("Opening");
		chckbxNewCheckBox.setSelected(true);
		chckbxNewCheckBox.addItemListener(this);
		
		lblKernelShape = new JLabel("Kernel Shape");
		add(lblKernelShape, "cell 0 2,alignx trailing");
		
		comboBox = new JComboBox<Shape>();
		comboBox.setModel(new DefaultComboBoxModel<Shape>(Shape.values()));
		comboBox.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				notifyChange();
			}
		});
		add(comboBox, "cell 1 2,growx");
		add(chckbxNewCheckBox, "cell 0 3 2 1,growx");
	}

	@Override
	public void applyComponent(Mat inputMat, ProcessInfo info) {

		Integer dilateSize = (Integer) dilateSizeSpinner.getValue();
		Integer erodeSize = (Integer) erodeSizeSpinner.getValue();
		
		int shape = Imgproc.MORPH_ELLIPSE;
		
		switch ((Shape)comboBox.getSelectedItem()) {
		case CIRCLE:
			shape = Imgproc.MORPH_ELLIPSE;
			break;
		case CROSS:
			shape = Imgproc.MORPH_CROSS;
			break;
		case SQUARE:
			shape = Imgproc.MORPH_RECT;
			break;
		default:
			break;
		}

		Mat dilateKernel = Imgproc.getStructuringElement(shape, new Size(dilateSize, dilateSize));
		Mat erodeKernel = Imgproc.getStructuringElement(shape, new Size(erodeSize, erodeSize));
		
		if(!chckbxNewCheckBox.isSelected()){
			Imgproc.dilate(inputMat, inputMat, erodeKernel);
		}
			
		Imgproc.erode(inputMat, inputMat, erodeKernel);
			
		if(chckbxNewCheckBox.isSelected()){
			Imgproc.dilate(inputMat, inputMat, dilateKernel);
		}
	}

	@Override
	public void itemStateChanged(ItemEvent e) {
		if(e.getSource().equals(chckbxNewCheckBox)){
			if(!chckbxNewCheckBox.isSelected()){
				chckbxNewCheckBox.setText("Closing");
			} else {
				chckbxNewCheckBox.setText("Opening");
			}
		}
		this.notifyChange();
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		this.notifyChange();
	}

}
