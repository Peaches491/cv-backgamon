package components;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.io.File;
import java.util.ArrayList;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Point;
import org.opencv.core.Rect;
import org.opencv.core.Scalar;
import org.opencv.core.Size;
import org.opencv.highgui.Highgui;
import org.opencv.imgproc.Imgproc;

import components.base.Component;
import components.base.ProcessInfo;
import net.miginfocom.swing.MigLayout;

import javax.swing.JButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;
import javax.swing.JLabel;

import components.BoundingBoxComponent.Region;

import javax.swing.JSpinner;
import javax.swing.SpinnerNumberModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

@SuppressWarnings("serial")
public class RegionSavingComponent extends Component {
	private JTextField regionNumField;
	private int regionNumber = 0;
	private JSpinner paddingSpinner;
	private JButton btnSave;
	private ProcessInfo info;
	private JTextField saveField;
	private JTextField nameField;
	private JSpinner resampleSpinner;
	
	public RegionSavingComponent(int padding, int res) {
		setTitle("RegionSaving");
		setLayout(new MigLayout("fill", "[left][][grow][]", "[][][][][][]"));
		
		JLabel lblSelectedRegion = new JLabel("Selected Region");
		add(lblSelectedRegion, "cell 0 0");
		
		JButton button = new JButton("-");
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RegionSavingComponent.this.decrementRegion();
			}
		});
		add(button, "cell 1 0");
		
		regionNumField = new JTextField();
		regionNumField.setEditable(false);
		regionNumField.setHorizontalAlignment(SwingConstants.CENTER);
		regionNumField.setText("0");
		add(regionNumField, "cell 2 0,growx");
		regionNumField.setColumns(10);
		
		JButton button_1 = new JButton("+");
		button_1.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				RegionSavingComponent.this.incrementRegion();
			}
		});
		add(button_1, "cell 3 0");
		
		JLabel lblPadding = new JLabel("Padding");
		add(lblPadding, "cell 0 1");
		
		paddingSpinner = new JSpinner();
		paddingSpinner.setModel(new SpinnerNumberModel(new Integer(padding), new Integer(0), null, new Integer(1)));
		add(paddingSpinner, "cell 1 1 3 1,growx");
		paddingSpinner.addChangeListener(new ChangeListener() {
			@Override
			public void stateChanged(ChangeEvent e) {
				notifyChange();
			}
		});
		
		JLabel lblResampleSize = new JLabel("Resample Size");
		add(lblResampleSize, "cell 0 2");
		
		resampleSpinner = new JSpinner();
		resampleSpinner.setModel(new SpinnerNumberModel(new Integer(res), new Integer(1), null, new Integer(1)));
		add(resampleSpinner, "cell 1 2 3 1,growx");
		
		JLabel lblSavePath = new JLabel("Folder");
		add(lblSavePath, "cell 0 3");
		
		saveField = new JTextField();
		saveField.setEditable(false);
		add(saveField, "cell 1 3 3 1,growx");
		saveField.setColumns(10);
		
		JLabel lblFilename = new JLabel("Filename");
		add(lblFilename, "cell 0 4");
		
		nameField = new JTextField();
		nameField.setColumns(10);
		add(nameField, "cell 1 4 3 1,growx");
		
		btnSave = new JButton("Save!");
		add(btnSave, "cell 0 5 4 1,alignx center");
		btnSave.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				save();
			}
		});
		
		updateDisplay();
	}

	protected void incrementRegion() {
		regionNumber++;
		if(regionNumber >= getRegions().size()) {
			regionNumber = 0;
		}
		updateDisplay();
	}

	protected void decrementRegion() {
		regionNumber--;
		if(regionNumber < 0) {
			regionNumber = getRegions().size()-1;
		}
		updateDisplay();
	}

	private void updateDisplay() {
		regionNumField.setText("" + (regionNumber+1));
		notifyChange();
	}

	@SuppressWarnings("unchecked")
	private ArrayList<Region> getRegions() {
		return ((ArrayList<Region>) componentManager.getRegistryData("REGION_OBJS"));
	}

	@SuppressWarnings("unchecked")
	private Region selectedRegion() {
		return ((ArrayList<Region>) componentManager.getRegistryData("REGION_OBJS")).get(regionNumber);
	}

	@Override
	public void applyComponent(Mat inputMat, ProcessInfo info) {
		this.info = info;
		Scalar color = new Scalar(0, 0, 180);
		Scalar squareColor = new Scalar(255, 0, 0);
		
		Rect r = getSelectedRect();
		
		saveField.setText(info.getActiveDirectory());
		nameField.setText((Integer)resampleSpinner.getValue() + "-" + info.getFileName());
		
		Core.putText(info.getOverlayMat(), "" + r.size(),  
				r.tl(), Core.FONT_HERSHEY_DUPLEX, 0.5,  new Scalar(255, 255, 255), 1, Core.LINE_AA, false);
		Core.rectangle(info.getOverlayMat(), r.tl(), r.br(), squareColor);
		
		
	}
	
	private Rect getSelectedRect(){
		Region r = selectedRegion();
		
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
	
	private int getPadding() {
		return (Integer)paddingSpinner.getValue();
	}

	private static String getRegionFolder(String path){
		System.out.println("getRegionFolder: " + path);
		return path + "\\regions";
	}

	public void save() {
		Integer w = (Integer)resampleSpinner.getValue();
		Rect r = getSelectedRect();
		Mat orig = info.getOriginalMat();
		
		Mat paddedOriginal = new Mat(orig.height()+2*w, orig.width()+2*w, orig.type());
		Imgproc.copyMakeBorder(orig, paddedOriginal, w, w, w, w, Imgproc.BORDER_REPLICATE);
		
		r.x += w;
		r.y += w;
		
		Mat roi = new Mat(paddedOriginal, r);
//		System.out.println(roi.size());
		File dir = new File(getRegionFolder(info.getActiveDirectory()));
		if(!dir.exists()) dir.mkdir();

		Imgproc.resize(roi, roi, new Size(w, w));
		
		String file = dir.getAbsolutePath() + "\\" + nameField.getText();
		
		System.out.println(file);
		Boolean result = null;
		result = Highgui.imwrite(file, roi);
		System.out.println(result);
	}

}
