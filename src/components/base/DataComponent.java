package components.base;

import java.awt.BorderLayout;
import java.awt.Font;
import java.util.ArrayList;

import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EtchedBorder;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Mat;

import javax.swing.JCheckBox;
import javax.swing.JToggleButton;

import net.miginfocom.swing.MigLayout;

@SuppressWarnings("serial")
public abstract class DataComponent extends Component{
		
	private JPanel enablePanel;
	private JToggleButton btnEnable;
	private JCheckBox chckBoxVisualize;

	
	public DataComponent(){
	
		enablePanel = new JPanel();
		containerPanel.add(enablePanel, BorderLayout.SOUTH);
		enablePanel.setLayout(new MigLayout("fill", "[fill, grow][]", "[]"));
		
		btnEnable = new JToggleButton("Enabled");
		btnEnable.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(btnEnable.isSelected()){
					btnEnable.setText("Enabled");
				} else {
					btnEnable.setText("Disabled");
				}
			}
		});
		btnEnable.setSelected(true);
		btnEnable.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				notifyChange();
			}
		});
		enablePanel.add(btnEnable, "cell 0 0,alignx left,aligny top");
		
		chckBoxVisualize = new JCheckBox("Visualize");
		chckBoxVisualize.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				notifyChange();
			}
		});
		enablePanel.add(chckBoxVisualize, "cell 1 0,alignx left,aligny top");
		
	}

	public abstract void doApplyComponent(Mat inputMat);

	public Mat doProcessInput(Mat inputMat) {
		Mat output = inputMat.clone();
		applyComponent(output);
		return output;
	}
	
	
	public void setApply(boolean b) {
		btnEnable.setSelected(b);
	}
	public boolean isApplyEnabled() {
		return btnEnable.isSelected();
	}
	
	public void setVisualize(boolean b) {
		chckBoxVisualize.setSelected(b);
	}
	public boolean shouldVisualize() {
		return chckBoxVisualize.isSelected();
	}

}
