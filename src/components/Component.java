package components;

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

public abstract class Component extends JPanel{
	
	private JPanel containerPanel = new JPanel(new BorderLayout());
	private ArrayList<ChangeListener> changeListeners =
			new ArrayList<ChangeListener>();
	private JLabel titleLabel = new JLabel();
	private JPanel enablePanel;
	private JToggleButton btnEnable;
	private JCheckBox chckBoxVisualize;
	protected ComponentManager componentManager;
	
	public Component(){
		this.titleLabel.setText("<Untitled Component>");
		this.titleLabel.setFont(new Font("Tahoma", Font.BOLD, 12));

		containerPanel.add(titleLabel, BorderLayout.NORTH);
		containerPanel.add(this, BorderLayout.CENTER);
		containerPanel.setBorder(new EtchedBorder());

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

	public abstract void applyComponent(Mat inputMat);

	public Mat processInput(Mat inputMat) {
		Mat output = inputMat.clone();
		applyComponent(output);
		return output;
	}
	
	public JPanel getControlPanel(){
		return containerPanel;
	}
	
	public void addChangeListener(ChangeListener listener){
		this.changeListeners.add(listener);
	}
	
	public void notifyChange(){
		for(ChangeListener e : changeListeners){
			e.stateChanged(new ChangeEvent(this));
		}
	}
	
	protected void setTitle(String string) {
		this.titleLabel.setText(string);
	}

	public boolean isApplyEnabled() {
		return btnEnable.isSelected();
	}
	
	public boolean shouldVisualize() {
		return chckBoxVisualize.isSelected();
	}

	public String getTitle() {
		return titleLabel.getText();
	}

	public void setApply(boolean b) {
		btnEnable.setSelected(b);
	}

	public void setVisualize(boolean b) {
		chckBoxVisualize.setSelected(b);
	}

	public void setManager(ComponentManager componentManager) {
		this.componentManager = componentManager;
	}
}
