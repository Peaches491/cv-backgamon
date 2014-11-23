package visualization;

import javax.swing.JPanel;

import org.opencv.core.Mat;

import components.base.Component;
import components.base.ComponentManager;
import net.miginfocom.swing.MigLayout;

import javax.swing.JTabbedPane;
import javax.swing.JSlider;
import javax.swing.JLabel;

import java.awt.Font;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;

@SuppressWarnings("serial")
public class EditorRootPanel extends JPanel implements ChangeListener {
	
	private JSlider slider;
	private ImagePanel outputViewPanel;
	private JScrollPane scrollPane;
	private JPanel componentsPanel;
	private Mat imageMat;
	private JTabbedPane viewTabs;
	private ComponentManager compManager;

	public EditorRootPanel(){
		super();
		
		setLayout(new MigLayout("fill", "[grow,fill][fill]", "[grow]"));
		
		viewTabs = new JTabbedPane(JTabbedPane.TOP);
		add(viewTabs, "cell 0 0,grow");
		
		outputViewPanel = new ImagePanel();
		viewTabs.addTab("Output", null, outputViewPanel, null);
		
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane, "cell 1 0,grow");
		
		componentsPanel = new JPanel();
		scrollPane.setViewportView(componentsPanel);
		componentsPanel.setLayout(new MigLayout("fillx, flowy", "[grow, fill]", ""));
		

		JLabel lblThreshold = new JLabel("Components Panel");
		lblThreshold.setFont(new Font("Tahoma", Font.BOLD, 12));
		componentsPanel.add(lblThreshold);
	}
	
	public EditorRootPanel(Mat displayMat, ComponentManager compManager){
		this();
		this.imageMat = displayMat;
		outputViewPanel.setMat(displayMat);
		this.compManager = compManager;
	}

	public JSlider getThresholdSlider() {
		return slider;
	}
	
	public void updateView() {
		outputViewPanel.recalculate();
	}
	
	public void stateChanged(ChangeEvent e) {
		int oldIdx = viewTabs.getSelectedIndex();
		viewTabs.removeAll();
		Mat localMat = imageMat.clone();
		for(Component comp : compManager.getComponents()){
			if(comp.isApplyEnabled()){
				comp.applyComponent(localMat);
				if(comp.shouldVisualize()){
					addVisual(comp.getTitle(), localMat);
				}
			}
		}
		outputViewPanel.setMat(localMat);
		outputViewPanel.recalculate();
		viewTabs.setSelectedIndex(Math.min(viewTabs.getTabCount()-1, oldIdx));
	}

	private void addVisual(String title, Mat localMat) {
		ImagePanel newView = new ImagePanel(localMat);
		viewTabs.addTab(title, newView);
	}

	public void initialize() {
		compManager.initialize();
		
		for(Component c : compManager.getComponents()){
			componentsPanel.add(c.getControlPanel());
			c.addChangeListener(this);
		}
		
		repaint();
		stateChanged(null);
	}
}
