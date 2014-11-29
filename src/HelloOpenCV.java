
import java.io.File;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.highgui.Highgui;

import components.BinaryRegionTransformationComponent;
import components.BoundingBoxComponent;
import components.ChannelSelectorComponent;
import components.ContrastAdjustComponent;
import components.FileChooserComponent;
import components.RegionLabelingComponent;
import components.ThresholdComponent;
import components.base.ComponentManager;
import visualization.EditorRootPanel;

public class HelloOpenCV {
	public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {

		System.out.println("Hello, OpenCV");

		ComponentManager compManager = new ComponentManager();

		final EditorRootPanel rootPanel = new EditorRootPanel(compManager);

		compManager.addFileChooserComponent(new FileChooserComponent(rootPanel));
		compManager.addComponent(new ChannelSelectorComponent());
		compManager.addComponent(new ThresholdComponent(95));
		compManager.addComponent(new BinaryRegionTransformationComponent(9, 5, BinaryRegionTransformationComponent.Shape.CIRCLE, true));
		compManager.addComponent(new RegionLabelingComponent());
		compManager.addComponent(new BoundingBoxComponent());
		compManager.addComponent(new ContrastAdjustComponent());

		UIManager.setLookAndFeel(
				UIManager.getSystemLookAndFeelClassName());

		rootPanel.initialize();

		JFrame frame2 = new JFrame();
		frame2.add(rootPanel);
		frame2.setSize(828, 600);
		frame2.setLocationRelativeTo(null);
		frame2.setVisible(true);

		frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	}
}