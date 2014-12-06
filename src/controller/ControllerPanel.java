package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;

import javax.swing.JPanel;
import javax.swing.JButton;
import javax.swing.JCheckBox;

public class ControllerPanel extends JPanel {
	private Runnable runAction;
	private JCheckBox chckbxSaveAll;

	public ControllerPanel(){
		
		JButton btnRunSegmentation = new JButton("Run Segmentation");
		add(btnRunSegmentation);
		
		chckbxSaveAll = new JCheckBox("Save All?");
		add(chckbxSaveAll);
		btnRunSegmentation.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(runAction != null) {
					new Thread(runAction).start();
				}
			}
		});
		
	}

	public void setRunAction(Runnable runAction) {
		this.runAction = runAction;
	}

	public boolean saveAllSelected() {
		return chckbxSaveAll.isSelected();
	}
}
