package components.base;

import java.awt.BorderLayout;
import java.awt.Font;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
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
		btnEnable.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if(btnEnable.isSelected()){
					btnEnable.setText("Enabled");
				} else {
					btnEnable.setText("Disabled");
				}
				notifyChange();
			}
		});
		btnEnable.setSelected(true);
		enablePanel.add(btnEnable, "cell 0 0,alignx left,aligny top");
		
		chckBoxVisualize = new JCheckBox("Visualize");
		chckBoxVisualize.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				notifyChange();
			}
		});
		enablePanel.add(chckBoxVisualize, "cell 1 0,alignx left,aligny top");
		
	}

	public abstract void applyComponent(Mat inputMat, ProcessInfo info);

	public Mat processInput(Mat inputMat, ProcessInfo info) {
		Mat output = inputMat.clone();
		applyComponent(output, info);
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
