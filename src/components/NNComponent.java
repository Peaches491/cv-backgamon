package components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Vector;

import org.opencv.core.Core;
import org.opencv.core.CvType;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.imgproc.Imgproc;
import org.opencv.ml.CvANN_MLP;

import ann.DiceClassifier;
import components.BoundingBoxComponent.Region;
import components.base.Component;
import components.base.ProcessInfo;
import net.miginfocom.swing.MigLayout;

import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JSpinner;
import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.JPanel;
import javax.swing.JComboBox;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListDataListener;
import javax.swing.SpinnerNumberModel;

public class NNComponent extends Component {
	private JTextField directoryField;
	private JTextField topologyField;
	private Vector<Integer> regionSizeVector = new Vector<Integer>(Arrays.asList(16, 24, 36, 48, 64));
	private File directory = new File("C:\\Users\\Daniel\\Dropbox\\School Stuff\\Graduate\\Computer Vision\\CV Group Project (1)\\images\\Dice\\Red\\");
	private JComboBox<Integer> regionSizeComboBox;
	private JSpinner paddingSpinner;
	protected Mat lastMat;
	protected ProcessInfo lastInfo;
	private DiceClassifier dc;
	private Integer regionSize;
	private int[] topology;

	@SuppressWarnings("serial")
	public NNComponent(){
		super();
		setTitle("Neural Net");
		setLayout(new MigLayout("fill", "[][grow,fill]", "[][][][][]"));
		
		JLabel lblTrainingDirectory = new JLabel("Training Directory");
		add(lblTrainingDirectory, "cell 0 0,aligny bottom");
		
		JButton directorySelectBtn = new JButton("Select");
		add(directorySelectBtn, "cell 1 0,aligny bottom");
		directorySelectBtn.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				JFileChooser fChoose = new JFileChooser();
				fChoose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

				int fileChooseResult = fChoose.showDialog(null, "Select Directory");

				switch (fileChooseResult) {
				case JFileChooser.APPROVE_OPTION:
					System.out.println("Approved!");
					NNComponent.this.directory = fChoose.getSelectedFile();
					NNComponent.this.settingsChanged();
					directoryField.setText(directory.getAbsolutePath());
					break;
				case JFileChooser.ERROR_OPTION:
					System.err
							.println("Error occurred opening selected file or folder. Exiting. ");
//					System.exit(1);
					break;
				case JFileChooser.CANCEL_OPTION:
					System.err.println("File selection cancelled. Exiting. ");
//					System.exit(1);
					break;
				default:
					break;
				}
			}
		});
		
		directoryField = new JTextField(directory.getAbsolutePath());
		add(directoryField, "cell 0 1 2 1,growx,aligny top");
		directoryField.setEditable(false);
		directoryField.setColumns(10);
		
		JLabel lblPadding = new JLabel("Padding");
		add(lblPadding, "cell 0 2");
		
		paddingSpinner = new JSpinner();
		paddingSpinner.setModel(new SpinnerNumberModel(new Integer(5), new Integer(0), null, new Integer(1)));
		add(paddingSpinner, "cell 1 2");
		paddingSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				NNComponent.this.settingsChanged();
			}
		});
		
		JLabel lblTopology = new JLabel("Topology");
		add(lblTopology, "cell 0 3");
		
		topologyField = new JTextField("20");
		add(topologyField, "cell 1 3");
		topologyField.setColumns(10);
		topologyField.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				NNComponent.this.settingsChanged();
			}
		});
		
		JLabel lblRegionSize = new JLabel("Region Size");
		add(lblRegionSize, "cell 0 4");
		
		regionSizeComboBox = new JComboBox<Integer>();
		add(regionSizeComboBox, "cell 1 4");
		regionSizeComboBox.setModel(new DefaultComboBoxModel<Integer>(regionSizeVector));
		regionSizeComboBox.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				NNComponent.this.settingsChanged();
			}
		});
		
		settingsChanged();
	}
	
	protected void settingsChanged() {
		checkSettings();
		
		if(regionSize == null || topology == null) {
			dc = null;
			return;
		}
		
		// Build Classifier
		dc = new DiceClassifier(directory, regionSize, topology);

		if(!dc.loadSamples()) return;
		dc.train();
		
		if(this.lastMat != null && this.lastInfo != null)
			applyComponent(NNComponent.this.lastMat, NNComponent.this.lastInfo);
	}

	private Rect getRect(Region r){
		if(r == null) return null;
		Point minSq = new Point(r.getMinPoint().x, r.getMinPoint().y);
		Point maxSq = new Point(r.getMaxPoint().x, r.getMaxPoint().y);
		Point dist = new Point(maxSq.x-minSq.x, maxSq.y-minSq.y);
		double size = Math.max(dist.x, dist.y);
		boolean stretchDown = dist.y <= dist.x;

		if(stretchDown){
			maxSq.y = minSq.y + size;
		} else {
			size -= dist.x;
			minSq.x = minSq.x - size/2.0;
			maxSq.x = maxSq.x + size/2.0;
		}
		
		Integer pad = (Integer)paddingSpinner.getValue();
		minSq.x -= pad;
		minSq.y -= pad;
		maxSq.x += pad;
		maxSq.y += pad;
		
		return new Rect(minSq, maxSq);
	}
	
	@Override
	public void applyComponent(Mat inputMat, ProcessInfo info) {
		lastMat = inputMat;
		lastInfo = info;
//		directory = new File("C:\\Users\\Daniel\\Dropbox\\School Stuff\\Graduate\\Computer Vision\\CV Group Project (1)\\images\\Dice\\Red\\");

		checkSettings();
		
		Mat orig = info.getOriginalMat();
		List<Region> regions = ((ArrayList<Region>) componentManager.getRegistryData("REGION_OBJS"));
		Integer paddingSize = (Integer)paddingSpinner.getValue();
		
		if(dc != null){
			for(int i = 0; i < regions.size(); i++) {
				Region reg = regions.get(i);
				if(reg == null) return;
				
				Mat paddedOriginal = new Mat(orig.height()+2*paddingSize, orig.width()+2*paddingSize, orig.type());
				Imgproc.copyMakeBorder(orig, paddedOriginal, paddingSize, paddingSize, paddingSize, paddingSize, Imgproc.BORDER_REPLICATE);
				
				Rect r = getRect(reg);
				r.x += paddingSize;
				r.y += paddingSize;
				
				Mat roi = new Mat(paddedOriginal, r);
				Imgproc.resize(roi, roi, new Size(regionSize, regionSize));
		
				Imgproc.cvtColor(roi, roi, Imgproc.COLOR_BGR2GRAY);
				roi.convertTo(roi, CvType.CV_32FC1);
				
				System.out.println("ROI: " + roi);
				int diceVal = dc.classify(roi);
				System.out.println("Classify: " + diceVal);
				setError("");
				
				System.out.println("" + reg.getMaxPoint());
				
				if(info.getMetaFile() != null) {
					System.out.println(info.getMetaFile().setRegionClassification(i, diceVal));
				}
				
				Core.putText(info.getOverlayMat(), ""+diceVal, reg.getMinPoint(), Core.FONT_HERSHEY_DUPLEX, 0.8, 
						new Scalar(255, 255, 255), 1, Core.LINE_AA, false);
			}
		} else {
			setError("Network has not been trained!");
		}
	}

	private void checkSettings() {
		// Check Region Size
		Integer regionSize = (Integer)regionSizeComboBox.getSelectedItem();
		if(regionSize == null || regionSize == 0){
			setError("Region Size cannot be null or zero!");
		}
		
		// Build topology
		String[] intStrings = topologyField.getText().split("[, ]");
		int[] intArray = new int[intStrings.length];
		
		if(intStrings.length == 0 ) {
			setError("No topology specified!");
			return;
		}
		for(int i = 0; i < intStrings.length; i++) {
		    try{
		    	intArray[i] = Integer.parseInt(intStrings[i]);
		    } catch(NumberFormatException e) {
		    	setError("Topology Format Exception: " + e.getMessage());
		    	return;
		    }
		}
		
		// Check Training Directory
		if(directory == null || !directory.exists()) {
			setError("Training data directory not set!");
			return;
		}
		
		this.regionSize = regionSize;
		this.topology = intArray;
	}
}
