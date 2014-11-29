package components;

import java.awt.BorderLayout;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.JToggleButton;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import visualization.EditorRootPanel;

import net.miginfocom.swing.MigLayout;

import components.base.GeneralComponent;

@SuppressWarnings("serial")
public class FileChooserComponent extends GeneralComponent {

	private JPanel panel;
	private JToggleButton btnChooseImgRoot;
	private JTextField textField;

	//	protected Mat faceMat;
	protected EditorRootPanel eRP;

	/**
	 * Constructor
	 * Let the user choose the image file
	 * @param eRP - reference to RootPanel (so we can change the matrix)
	 */
	public FileChooserComponent(EditorRootPanel eRP){
		this.setTitle("File Chooser");
		//		this.faceMat = faceMat;
		this.eRP = eRP;

		panel = new JPanel();
		containerPanel.add(panel, BorderLayout.SOUTH);
		panel.setLayout(new MigLayout("fill", "[fill, grow][]", "[]"));

		JLabel lblFileNme = new JLabel("File:");
		add(lblFileNme, "cell 0 0,alignx trailing");

		textField = new JTextField();
		textField.setEditable(false);
		textField.setText("?");
		add(textField, "cell 1 0,growx");
		textField.setColumns(20);

		btnChooseImgRoot = new JToggleButton("Sel");
		add(btnChooseImgRoot, "cell 2 0,growx");
		btnChooseImgRoot.addChangeListener(new ChangeListener() {
			public void stateChanged(ChangeEvent e) {
				if(btnChooseImgRoot.isSelected()){
					chooseFile();
					notifyChange();
				}
			}
		});
		panel.add(btnChooseImgRoot, "cell 0 0,alignx left,aligny top");
	}

	/**
	 * Allow user to choose an image file.
	 * Brings up a FileChooser dialog
	 * To avoid the user having to navigate directories every time
	 * to find the file, this keeps the name of the last-accessed-image-file
	 * in a state file and uses that as default next time
	 */
	public void chooseFile() {

		// keep a state file which holds the file name of most recent image
		String stateFileName = "lastImage.txt";
		String imageFileName;
		File imageFile = null;
		try {
			FileReader fr = new FileReader(stateFileName);
			BufferedReader textReader = new BufferedReader(fr);
			imageFileName = textReader.readLine();
			if (imageFileName==null)
				imageFileName = "";
			textReader.close();	
		}
		catch (IOException e) {
			imageFileName = "";
		}
		imageFile = new File(imageFileName);

		// open a file chooser with the name of the last image accessed
		JFileChooser chooser = new JFileChooser();
		chooser.setSelectedFile(imageFile);
		int returnVal = chooser.showOpenDialog(this);
		if(returnVal == JFileChooser.APPROVE_OPTION) {
			imageFile = chooser.getSelectedFile();
			textField.setText(imageFile.getPath());
		}

		// write the name of the image file back out to state file for next time
		try {
			FileWriter fileWriter = new FileWriter(stateFileName);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(imageFile.getPath());
			bufferedWriter.newLine();
			bufferedWriter.close();
		}
		catch(IOException ex) {
			System.out.println("Error writing to file '" + stateFileName + "'");
		}

		// point EditorRootPanel to the new image
		String picPath = imageFile.getAbsolutePath();
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
		Mat faceMat= Highgui.imread(picPath);
		componentManager.initialize();
		eRP.setDisplayMat(faceMat);
	}
}
