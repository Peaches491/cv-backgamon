package visualization;

import javax.swing.JPanel;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import components.base.Component;
import components.base.ComponentManager;
import components.base.ProcessInfo;
import controller.ControllerPanel;
import files.FileTreeModel;
import files.RegionCountingTableModel;
import net.miginfocom.swing.MigLayout;

import javax.swing.JTabbedPane;
import javax.swing.JSlider;
import javax.swing.JLabel;

import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.Font;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableModelListener;
import javax.swing.event.TreeModelEvent;
import javax.swing.event.TreeModelListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.table.TableModel;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeCellRenderer;
import javax.swing.tree.TreeCellRenderer;
import javax.swing.tree.TreeModel;
import javax.swing.JScrollPane;
import javax.swing.ScrollPaneConstants;
import javax.swing.JTree;
import javax.swing.JTable;
import javax.swing.ListSelectionModel;

@SuppressWarnings("serial")
public class EditorRootPanel extends JPanel implements ChangeListener {
	
	private File selectedFile;
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
	private ProcessInfo imgInfo;
	private JTable table;
	private JScrollPane tableScrollPane;
	private RegionCountingTableModel tableModel;
	private ControllerPanel controllerPanel;

	
	/**
	 * @wbp.parser.constructor
	 */
	public EditorRootPanel(String fileDirectory){
		super();
		
		setLayout(new MigLayout("fill, hidemode 3", "[400px!,grow,fill][][(pref)!,fill]", "[grow][][grow]"));
		
		TreeModel model = new FileTreeModel(new File(fileDirectory));
		
		scrollPane_1 = new JScrollPane();
		add(scrollPane_1, "cell 0 0,grow");
		tree = new JTree(model);
		scrollPane_1.setViewportView(tree);
		tree.setCellRenderer(new DefaultTreeCellRenderer() {
			@Override
			public java.awt.Component getTreeCellRendererComponent(JTree tree,
					Object value, boolean selected, boolean expanded, boolean leaf,
					int row, boolean hasFocus) {
				JLabel label = (JLabel) super.getTreeCellRendererComponent(tree, value, selected, expanded, leaf, row, hasFocus);
				label.setText(((File) value).getName());
				
				JPanel panel = new JPanel(new FlowLayout(FlowLayout.LEFT, 2, 0));
				panel.add(label);
				panel.setOpaque(false);
				return panel;
			}
		});
		
		tree.addTreeSelectionListener(new TreeSelectionListener() {
			@Override
			public void valueChanged(TreeSelectionEvent e) {
				File node = (File) tree.getLastSelectedPathComponent();
			    if(node == null) return;
			    System.out.println(node);
			    tableScrollPane.setVisible(true);
		    	tableModel.setFileStats(node);
			    EditorRootPanel.this.revalidate();
			}
		});
		
		viewTabs = new JTabbedPane(JTabbedPane.TOP);
		add(viewTabs, "cell 1 0 1 2,grow");
		
		outputViewPanel = new ImagePanel();
		viewTabs.addTab("Output", null, outputViewPanel, null);
		overlayViewPanel = new ImagePanel();
		viewTabs.addTab("Output", null, outputViewPanel, null);
		
		scrollPane = new JScrollPane();
		scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_ALWAYS);
		scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);
		add(scrollPane, "cell 2 0 1 2,grow");
		
		componentsPanel = new JPanel();
		scrollPane.setViewportView(componentsPanel);
		componentsPanel.setLayout(new MigLayout("fillx, flowy", "[grow, fill]", ""));
		
		JLabel lblThreshold = new JLabel("Components Panel");
		lblThreshold.setFont(new Font("Tahoma", Font.BOLD, 12));
		componentsPanel.add(lblThreshold);
		
		table = new JTable();
		table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
		table.setFillsViewportHeight(true);
		tableModel = new RegionCountingTableModel();
		table.setModel(tableModel);
		
		ListSelectionModel cellSelectionModel = table.getSelectionModel();
		cellSelectionModel.addListSelectionListener(new ListSelectionListener() {
			@Override
			public void valueChanged(ListSelectionEvent e) {
				if(!e.getValueIsAdjusting()) {
					File node = tableModel.getFile(table.getSelectedRow());
				    if(node != null && node.isFile()) {
				    	EditorRootPanel.this.setImageFile(node);
				    	EditorRootPanel.this.selectedFile = node;
				    }
				}
			}
		});
		
		tableScrollPane = new JScrollPane();
		add(tableScrollPane, "cell 0 1,grow");
		tableScrollPane.setViewportView(table);
		
		controllerPanel = new ControllerPanel();
		add(controllerPanel, "cell 0 2,grow");
		tableScrollPane.setVisible(false);
		
		imgInfo = new ProcessInfo(fileDirectory, "", fileDirectory, null, null);
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
			imgInfo.setActiveDirectory(picFile.getParentFile().getAbsolutePath());
			imgInfo.setFileName(picFile.getName());
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
			Mat overlayMat = new Mat(imageMat.height(), imageMat.width(), imageMat.type());
			imgInfo.setOverlayMat(overlayMat);
			imgInfo.setOriginalMat(imageMat);

			for(Component comp : compManager.getComponents()){
				if(comp.isApplyEnabled()){
//					System.out.print(comp.getTitle() + ": " + CvType.typeToString(localMat.type()) + " -> ");
					comp.applyComponent(localMat, imgInfo);
//					System.out.println(CvType.typeToString(localMat.type()));
					if(comp.shouldVisualize()){
						addVisual(comp.getTitle(), localMat);
					}
				}
			}
	
			Core.add(localMat, overlayMat, localMat);
			Core.add(imageMat, overlayMat, overlayMat);
			
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
	
	public ControllerPanel getControllerPanel(){
		return controllerPanel;
	}

	public void selectNextFile() {
		int row = table.getSelectedRow();
		row++;
		table.setRowSelectionInterval(row, row);
	}

	public boolean hasNextFile() {
		return tableModel.getRowCount()-1 != table.getSelectedRow();
	}
}
