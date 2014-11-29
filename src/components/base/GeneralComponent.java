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
public abstract class GeneralComponent extends Component{

	public GeneralComponent(){

	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see components.base.Component#applyComponent(org.opencv.core.Mat)
	 */
	public void doApplyComponent(Mat inputMat) {
		// do nothing	
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see components.base.Component#processInput(org.opencv.core.Mat)
	 */
	public Mat doProcessInput(Mat inputMat) {
		return inputMat;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see components.base.Component#setApply(boolean)
	 */
	public void setApply(boolean b) {
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see components.base.Component#isApplyEnabled()
	 */
	public boolean isApplyEnabled() {
		return false;
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see components.base.Component#setVisualize(boolean)
	 */
	public void setVisualize(boolean b) {
	}

	@Override
	/*
	 * (non-Javadoc)
	 * @see components.base.Component#shouldVisualize()
	 */
	public boolean shouldVisualize() {
		return false;
	}

}
