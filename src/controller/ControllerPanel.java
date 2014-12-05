package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JPanel;
import javax.swing.JButton;

public class ControllerPanel extends JPanel {
	private Runnable runAction;

	public ControllerPanel(){
		
		JButton btnRunSegmentation = new JButton("Run Segmentation");
		add(btnRunSegmentation);
		btnRunSegmentation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				
				System.out.println("BTN CALLBACK");
				if(runAction != null) {
					new Thread(runAction).start();
				}
			}
		});
		
	}

	public void setRunAction(Runnable runAction) {
		this.runAction = runAction;
	}
}
