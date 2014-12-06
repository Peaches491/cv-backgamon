package components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import net.miginfocom.swing.MigLayout;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import components.base.Component;
import components.base.ProcessInfo;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JSlider;
import javax.swing.JLabel;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class ChannelSelectorComponent extends Component implements ChangeListener {
	
	private enum ChannelType {
		Combined("Combined", -1),
		Red("Red", 2),
		Green("Green", 1),
		Blue("Blue", 0);
		private int channelNumber;

		private ChannelType(String name, int channelNumber) {
			this.channelNumber = channelNumber;
		}
	}
	private JTextField redWeightField;
	private JTextField greenWeightField;
	private JTextField blueWeightField;
	private JSlider greenWeightSlider;
	private JSlider blueWeightSlider;
	private JSlider redWeightSlider;
	
	public ChannelSelectorComponent(double initRed, double initGreen, double initBlue){
		super();
		this.setLayout(new MigLayout("fill", "[right][grow,fill][shrink 0]", "[][][]"));
		
		this.setTitle("Channel Select");
		
		JLabel lblRed = new JLabel("Red");
		add(lblRed, "cell 0 0");
		
		redWeightSlider = new JSlider(0, 100, (int) (initRed*100));
		add(redWeightSlider, "cell 1 0");
		redWeightSlider.addChangeListener(this);
		
		redWeightField = new JTextField();
		redWeightField.setText("" + (int) (initRed*100));
		add(redWeightField, "cell 2 0,growx");
		redWeightField.setColumns(4);
		
		JLabel lblGreen = new JLabel("Green");
		add(lblGreen, "cell 0 1");
		
		greenWeightSlider = new JSlider(0, 100, (int) (initGreen*100));
		add(greenWeightSlider, "cell 1 1");
		greenWeightSlider.addChangeListener(this);
		
		greenWeightField = new JTextField();
		greenWeightField.setText("" + (int) (initGreen*100));
		add(greenWeightField, "cell 2 1,growx");
		greenWeightField.setColumns(4);
		
		JLabel lblBlue = new JLabel("Blue");
		add(lblBlue, "cell 0 2");
		
		blueWeightSlider = new JSlider(0, 100, (int) (initBlue*100));
		add(blueWeightSlider, "cell 1 2");
		blueWeightSlider.addChangeListener(this);
		
		blueWeightField = new JTextField();
		blueWeightField.setText("" + (int) (initBlue*100));
		add(blueWeightField, "cell 2 2,growx");
		blueWeightField.setColumns(4);
	}

	@Override
	public void applyComponent(Mat inputMat, ProcessInfo info) {
		
//		ChannelType channelType = (ChannelType)spinner.getModel().getSelectedItem();
		
		Mat weightMat = getWeightMat();
		
		Core.transform(inputMat, inputMat, weightMat);
		
//	    Core.split(inputMat, lRgb);
//	    
//	    Imgproc.cvtColor(lRgb.get(channelType.channelNumber), 
//	    		lRgb.get(channelType.channelNumber), 
//	    		Imgproc.COLOR_GRAY2BGR);
//	    
//	    
//	    lRgb.get(channelType.channelNumber).assignTo(inputMat);
	}

	private Mat getWeightMat() {
		Mat weightMat = new Mat(3, 3, CvType.CV_32F);
		int sum = blueWeightSlider.getValue();
		sum += greenWeightSlider.getValue();
		sum += redWeightSlider.getValue();
		
		float denom = sum * 1.0f;
		
		for(int i = 0; i < 3; i++) {
			weightMat.put(i, 0, new float[]{(float) (blueWeightSlider.getValue()/denom)});
			weightMat.put(i, 1, new float[]{(float) (greenWeightSlider.getValue()/denom)});
			weightMat.put(i, 2, new float[]{(float) (redWeightSlider.getValue()/denom)});
		}
//		System.out.println(weightMat.dump());
		return weightMat;
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		redWeightField.setText("" + redWeightSlider.getValue());
		greenWeightField.setText("" + greenWeightSlider.getValue());
		blueWeightField.setText("" + blueWeightSlider.getValue());
		this.notifyChange();
	}
}
