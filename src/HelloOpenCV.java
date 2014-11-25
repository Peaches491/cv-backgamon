
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
import components.RegionLabelingComponent;
import components.ThresholdComponent;
import components.base.ComponentManager;
import visualization.EditorRootPanel;

public class HelloOpenCV {
  public static void main(String[] args) throws ClassNotFoundException, InstantiationException, IllegalAccessException, UnsupportedLookAndFeelException {
    System.out.println("Hello, OpenCV");

    System.out.println("Winsor has committed some crap");

    
    
    
    ClassLoader loader = Thread.currentThread().getContextClassLoader();
    System.out.println(loader.getResource("lena1.png"));
//    System.out.println(loader.getResource("/lena1.png"));
//    System.out.println(loader.getResource("resources/lena1.png"));
//    System.out.println(loader.getResource("/resources/lena1.png"));
    
    String filename = "2014-11-14 14-19-46.798-crop.jpg";
    String fullPath = "C:/Users/Daniel/Dropbox/School Stuff/Graduate/Computer Vision/CV Group Project/images/Dice/Random Rolls/" + filename;
    
    File f = new File(fullPath);
    
    String picPath = f.getAbsolutePath();
    System.out.println(picPath);

//    f = new File(loader.getResource("test.gif").getPath().substring(1));
//    picPath = f.getAbsolutePath();
//    System.out.println(picPath);
//    String detectorPath = loader.getResource("lbpcascade_frontalface.xml").getPath().substring(1);
    
    // Load the native library.
    System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
    final Mat faceMat= Highgui.imread(picPath);
    
    
    ComponentManager compManager = new ComponentManager();
    
    final EditorRootPanel rootPanel = new EditorRootPanel(faceMat, compManager);
    
//    ChannelSelectorComponent channelSelector = new ChannelSelectorComponent();
//    rootPanel.addComponent(channelSelector);
//    
//    ThresholdComponent thresh = new ThresholdComponent();
//    rootPanel.addComponent(thresh);
//    
//    RegionLabelingComponent region = new RegionLabelingComponent();
//    rootPanel.addComponent(region);
//
//    BoundingBoxComponent box = new BoundingBoxComponent();
//    rootPanel.addComponent(box);
//
//    ContrastAdjustComponent contrast = new ContrastAdjustComponent();
//    rootPanel.addComponent(contrast);
    
    

    UIManager.setLookAndFeel(
            UIManager.getSystemLookAndFeelClassName());
    
    compManager.addComponent(new ChannelSelectorComponent());

    compManager.addComponent(new ThresholdComponent(95));
    compManager.addComponent(new BinaryRegionTransformationComponent(9, 5, BinaryRegionTransformationComponent.Shape.CIRCLE, true));
    compManager.addComponent(new RegionLabelingComponent());
    compManager.addComponent(new BoundingBoxComponent());
    compManager.addComponent(new ContrastAdjustComponent());
    
    rootPanel.initialize();
    
    JFrame frame2 = new JFrame();
    frame2.add(rootPanel);
    frame2.setSize(828, 600);
    frame2.setLocationRelativeTo(null);
    frame2.setVisible(true);

    
    frame2.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
  }
}