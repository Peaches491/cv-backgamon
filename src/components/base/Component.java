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

@SuppressWarnings("serial")
public abstract class Component  extends JPanel{

	protected JPanel containerPanel = new JPanel(new BorderLayout());
	protected ArrayList<ChangeListener> changeListeners =
			new ArrayList<ChangeListener>();
	protected JLabel titleLabel = new JLabel();
	protected ComponentManager componentManager;


	public Component() {
		this.titleLabel.setText("<Untitled Component>");
		this.titleLabel.setFont(new Font("Tahoma", Font.BOLD, 12));

		containerPanel.add(titleLabel, BorderLayout.NORTH);
		containerPanel.add(this, BorderLayout.CENTER);
		containerPanel.setBorder(new EtchedBorder());

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

	public String getTitle() {
		return titleLabel.getText();
	}

	public void setManager(ComponentManager componentManager) {
		this.componentManager = componentManager;
	}


	public final void applyComponent(Mat inputMat) {
		if (inputMat == null)
			return;
		doApplyComponent(inputMat);
	}
	public abstract void doApplyComponent(Mat inputMat);

	public final Mat processInput(Mat inputMat) {
		if (inputMat == null)
			return null;
		return doProcessInput(inputMat);
	}
	public abstract Mat doProcessInput(Mat inputMat);

	public abstract void setApply(boolean b);
	public abstract boolean isApplyEnabled();
	
	public abstract void setVisualize(boolean b);
	public abstract boolean shouldVisualize();







}
