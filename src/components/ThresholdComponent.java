package components;

import javax.swing.JSlider;
import javax.swing.JTextField;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import components.base.Component;
import components.base.ProcessInfo;
import net.miginfocom.swing.MigLayout;

import javax.swing.SwingConstants;

@SuppressWarnings("serial")
public class ThresholdComponent extends Component {
	private JSlider slider;
	private JTextField textField;

	public ThresholdComponent(int sliderVal){
		super();

		this.setLayout(new MigLayout("fillx", "[grow][]", "[][]"));
		this.setTitle("Threshold");

		textField = new JTextField();
		textField.setHorizontalAlignment(SwingConstants.CENTER);
		textField.setEditable(false);
		textField.setText("" + sliderVal);
		this.add(textField, "cell 1 0,growx");
		textField.setColumns(3);

		slider = new JSlider();
		slider.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				textField.setText("" + slider.getValue());
				notifyChange();
			}
		});
		slider.setMaximum(255);
		slider.setValue(sliderVal);
		this.add(slider, "cell 0 0,grow");
	}

	@Override
	public void applyComponent(Mat inputMat, ProcessInfo info) {
		Imgproc.threshold(inputMat, inputMat, slider.getValue() , 0xFFFFFF, Imgproc.THRESH_BINARY);
	}
}
