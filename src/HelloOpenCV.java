import java.io.File;

import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.apache.commons.cli.BasicParser;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;
import org.opencv.core.Core;

import components.BinaryRegionTransformationComponent;
import components.BoundingBoxComponent;
import components.ChannelSelectorComponent;
import components.ContrastAdjustComponent;
import components.RegionLabelingComponent;
import components.ThresholdComponent;
import components.WarpAffineComponent;
import components.WarpPerspectiveComponent;
import components.base.ComponentManager;
import visualization.EditorRootPanel;

public class HelloOpenCV {

	private static void helpAndExit(Options opt) {
		HelpFormatter hFormat = new HelpFormatter();
		hFormat.printHelp("HelloOpenCV", opt);
		System.exit(-1);
	}

	public static void main(String[] args) throws ClassNotFoundException,
	InstantiationException, IllegalAccessException,
	UnsupportedLookAndFeelException {
		System.out.println("Hello, OpenCV!");

		UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

		Options opt = new Options();
		opt.addOption(new Option("d", "dir", true,
				"The directory to be displayed in the GUI."));
		opt.addOption(new Option("f", "file", true,
				"The intial file to be loaded by the program."));

		CommandLineParser parser = new BasicParser();
		CommandLine cmd = null;
		try {
			cmd = parser.parse(opt, args);
		} catch (ParseException e) {
			helpAndExit(opt);
		}

		File img = null;
		File dir = null;
		if (cmd.hasOption("d")) {
			dir = new File(cmd.getOptionValue("d"));
		}
		if (cmd.hasOption("f")) {
			img = new File(cmd.getOptionValue("f"));
		}

		if (dir != null) {
			if(!dir.isDirectory()) {
				System.err.println("Specified directory path " + dir.getPath() + " does not point to a directory! Exiting.");
				helpAndExit(opt);
			}
			if(!dir.exists()) {
				System.err.println("Specified directory " + dir.getPath() + " does not exist! Exiting.");
				helpAndExit(opt);
			}
		}

		if (img != null) {
			if(img.isDirectory()) {
				System.err.println("Specified image path " + img.getPath() + " does not point to a file! Exiting.");
				helpAndExit(opt);
			}
			if(!img.exists()) {
				System.err.println("Specified image " + img.getPath() + " does not exist! Exiting.");
				helpAndExit(opt);
			}
		}

		if(dir == null && img == null) {
			JFileChooser fChoose = new JFileChooser();
			fChoose.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);

			int fileChooseResult = fChoose.showDialog(null,
					"Select Directory");

			switch (fileChooseResult) {
			case JFileChooser.APPROVE_OPTION:
				System.out.println("Approved!");
				dir = fChoose.getSelectedFile();
				break;
			case JFileChooser.ERROR_OPTION:
				System.err
				.println("Error occurred opening selected file or folder. Exiting. ");
				System.exit(1);
			case JFileChooser.CANCEL_OPTION:
				System.err.println("File selection cancelled. Exiting. ");
				System.exit(1);
			default:
				break;
			}
		}

		System.out.println(dir.getAbsolutePath());

		// Load the native library.
		System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

		ComponentManager compManager = new ComponentManager();

		final EditorRootPanel rootPanel = new EditorRootPanel(img, compManager,
				dir.getAbsolutePath());

		// ChannelSelectorComponent channelSelector = new
		// ChannelSelectorComponent();
		// rootPanel.addComponent(channelSelector);
		//
		// ThresholdComponent thresh = new ThresholdComponent();
		// rootPanel.addComponent(thresh);
		//
		// RegionLabelingComponent region = new RegionLabelingComponent();
		// rootPanel.addComponent(region);
		//
		// BoundingBoxComponent box = new BoundingBoxComponent();
		// rootPanel.addComponent(box);
		//
		// ContrastAdjustComponent contrast = new ContrastAdjustComponent();
		// rootPanel.addComponent(contrast);

		compManager.addComponent(new ChannelSelectorComponent());

		compManager.addComponent(new ThresholdComponent(130));
		compManager.addComponent(new BinaryRegionTransformationComponent(9, 5,
				BinaryRegionTransformationComponent.Shape.CIRCLE, true));
		compManager.addComponent(new RegionLabelingComponent());
		compManager.addComponent(new BoundingBoxComponent(0.4, 1.0, 550, 10000,
				250, 10000, 0.25, 4));
		compManager.addComponent(new ContrastAdjustComponent());
		// compManager.addComponent(new TrainImageSaverComponent());
		// compManager.addComponent(new NNComponent());

		compManager.addComponent(new WarpAffineComponent());
		compManager.addComponent(new WarpPerspectiveComponent());

		rootPanel.initialize();

		JFrame frame2 = new JFrame();
		frame2.add(rootPanel);
		frame2.setSize(828, 600);
		frame2.setLocationRelativeTo(null);
		frame2.setExtendedState(frame2.getExtendedState()
				| JFrame.MAXIMIZED_BOTH);
		frame2.setVisible(true);

		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}