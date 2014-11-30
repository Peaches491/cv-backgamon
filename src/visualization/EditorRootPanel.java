package visualization;

import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import components.base.Component;
import components.base.ComponentManager;
import files.FileTreeModel;
import net.miginfocom.swing.MigLayout;

import javax.swing.JTabbedPane;
import javax.swing.JSlider;
import javax.swing.JLabel;

import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.TreeModel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTree;

@SuppressWarnings("serial")
public class EditorRootPanel extends JPanel implements ChangeListener {
	
	private JSlider slider;
	private ImagePanel outputViewPanel;
	private JScrollPane scrollPane;
	private JPanel componentsPanel;
	private Mat imageMat;
	private JTabbedPane viewTabs;
	private ComponentManager compManager;
	private JTree tree;
	private JScrollPane scrollPane_1;
	private ImagePanel overlayViewPanel;

	
	/**
	 * @wbp.parser.constructor
	 */
	public EditorRootPanel(String fileDirectory){
		super();
		
		setLayout(new MigLayout("fill", "[400px!,fill][][300px!,fill]", "[grow]"));
		
		TreeModel model = new FileTreeModel(new File(fileDirectory));
		
		scrollPane_1 = new JScrollPane();
		add(scrollPane_1, "cell 0 0,grow");
		tree = new JTree(model);
		scrollPane_1.setViewportView(tree);
		
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				File node = (File) tree.getLastSelectedPathComponent();
			    if(node == null) return;
			    if(node.isFile()) {
			    	EditorRootPanel.this.setImageFile(node);
			    }
			}
		});
		
		viewTabs = new JTabbedPane(JTabbedPane.TOP);
		add(viewTabs, "cell 1 0,grow");
		
		outputViewPanel = new ImagePanel();
		viewTabs.addTab("Output", null, outputViewPanel, null);
		overlayViewPanel = new ImagePanel();
		viewTabs.addTab("Output", null, outputViewPanel, null);
		
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane, "cell 2 0,grow");
		
		componentsPanel = new JPanel();
		scrollPane.setViewportView(componentsPanel);
		componentsPanel.setLayout(new MigLayout("fillx, flowy", "[grow, fill]", ""));
		

		JLabel lblThreshold = new JLabel("Components Panel");
		lblThreshold.setFont(new Font("Tahoma", Font.BOLD, 12));
		componentsPanel.add(lblThreshold);
	}

	public EditorRootPanel(File imageFile, ComponentManager compManager, String fileDirectory){
		this(fileDirectory);
		this.compManager = compManager;

		this.setImageFile(imageFile);
	}
	
	protected void setImageFile(File picFile) {
		if(picFile != null){
			Mat displayMat = Highgui.imread(picFile.getAbsolutePath());
			System.out.println("New Mat: " + displayMat);
			this.imageMat = displayMat;
			outputViewPanel.setMat(displayMat);
			stateChanged(null);
		}
	}

	public void updateView() {
		outputViewPanel.recalculate();
	}
	
	public void stateChanged(ChangeEvent e) {
		if(imageMat != null){
			int oldIdx = viewTabs.getSelectedIndex();
			viewTabs.removeAll();
			viewTabs.addTab("Output", null, outputViewPanel, null);
			viewTabs.addTab("Overlay", null, overlayViewPanel, null);
		
			Mat localMat = imageMat.clone();
			Mat overlayMat = imageMat.clone();
			compManager.setRegistryData("OVERLAY_MAT", new Mat(imageMat.height(), imageMat.width(), CvType.CV_8UC3));
			
			for(Component comp : compManager.getComponents()){
				if(comp.isApplyEnabled()){
					comp.applyComponent(localMat);
	//				System.out.println(CvType.typeToString(localMat.type()));
					if(comp.shouldVisualize()){
						addVisual(comp.getTitle(), localMat);
					}
				}
			}
			Mat overlay = (Mat)compManager.getRegistryData("OVERLAY_MAT");
			System.out.println(localMat);
			System.out.println(overlay);
	
			Core.add(localMat, overlay, localMat);
			Core.add(overlayMat, overlay, overlayMat);
			
			outputViewPanel.setMat(localMat);
			overlayViewPanel.setMat(overlayMat);
			viewTabs.setSelectedIndex(Math.min(viewTabs.getTabCount()-1, oldIdx));
		}
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
