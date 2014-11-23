package components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;

import net.miginfocom.swing.MigLayout;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

import components.base.Component;

import javax.swing.JComboBox;
import javax.swing.DefaultComboBoxModel;

@SuppressWarnings("serial")
public class ChannelSelectorComponent extends Component implements ActionListener {
	
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

	private JComboBox<ChannelType> spinner;
	
	public ChannelSelectorComponent(){
		this.setLayout(new MigLayout("fillx", "[grow]", "[][]"));
		
		this.setTitle("Channel Select");
		
		spinner = new JComboBox<ChannelType>();
		spinner.setModel(new DefaultComboBoxModel<ChannelType>(ChannelType.values()));
		spinner.setSelectedIndex(1);
		spinner.addActionListener(this);
		add(spinner, "cell 0 1, grow");
	}

	@Override
	public void applyComponent(Mat inputMat) {
		
		ChannelType channelType = (ChannelType)spinner.getModel().getSelectedItem();
		
		if(channelType.channelNumber >= 0){
			List<Mat> lRgb = new ArrayList<Mat>(3);
		    Core.split(inputMat, lRgb);
		    
		    Imgproc.cvtColor(lRgb.get(channelType.channelNumber), 
		    		lRgb.get(channelType.channelNumber), 
		    		Imgproc.COLOR_GRAY2BGR);
		    
		    
		    lRgb.get(channelType.channelNumber).assignTo(inputMat);
		}
	}

	public void actionPerformed(ActionEvent e) {
		this.notifyChange();
	}
}
