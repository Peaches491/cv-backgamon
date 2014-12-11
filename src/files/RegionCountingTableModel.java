package files;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class RegionCountingTableModel extends AbstractTableModel {

	String titles[] = new String[] { "File Name", "# Regions", "Region Values"};

	Class types[] = new Class[] { String.class, Integer.class, String.class };

	private MetaFile[] files;

	public RegionCountingTableModel() {
		this(".");
	}

	public RegionCountingTableModel(String dir) {
		File pwd = new File(dir);
		setFileStats(pwd);
	}

	// Implement the methods of the TableModel interface we're interested
	// in. Only getRowCount(), getColumnCount() and getValueAt() are
	// required. The other methods tailor the look of the table.
	public int getRowCount() {
		return files.length;
	}

	public int getColumnCount() {
		return titles.length;
	}

	public String getColumnName(int c) {
		return titles[c];
	}

	public Class getColumnClass(int c) {
		return types[c];
	}

	public Object getValueAt(int r, int c) {
		switch (c) {
		case 0:
			return files[r].getFile();
		case 1:
			return files[r].getRegionCount();
		case 2:
			return files[r].getRegionString();

		default:
			return null;
		}
	}

	// Our own method for setting/changing the current directory
	// being displayed. This method fills the data set with file info
	// from the given directory. It also fires an update event so this
	// method could also be called after the table is on display.
	public void setFileStats(File dir) {
		System.out.println(dir);
		String files[] = dir.list();
		if(files == null){
			this.files = new MetaFile[0];
			fireTableDataChanged();
			return;
		}
		this.files = new MetaFile[files.length];
		
		for (int i = 0; i < files.length; i++) {
			File tmp = new File(dir.getAbsolutePath() + "\\" + files[i]);
			this.files[i] = new MetaFile(tmp);
		}

		// Just in case anyone's listening...
		fireTableDataChanged();
	}

	public File getFile(int selectedRow) {
		try {
			return this.files[selectedRow].getFile();
		} catch(IndexOutOfBoundsException e) {
			return null;
		}
	}
	
	public MetaFile getMetaFile(int selectedRow) {
		try {
			return this.files[selectedRow];
		} catch(IndexOutOfBoundsException e) {
			return null;
		}
	}
}
