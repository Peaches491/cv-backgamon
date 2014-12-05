package files;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;

import javax.swing.event.TableModelListener;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableModel;

public class RegionCountingTableModel extends AbstractTableModel {

	String titles[] = new String[] { "File Name", "# Regions" };

	Class types[] = new Class[] { String.class, Integer.class };

	Object data[][];

	private File[] files;

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
		return data.length;
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
		return data[r][c];
	}

	// Our own method for setting/changing the current directory
	// being displayed. This method fills the data set with file info
	// from the given directory. It also fires an update event so this
	// method could also be called after the table is on display.
	public void setFileStats(File dir) {
		System.out.println(dir);
		String files[] = dir.list();
		if(files == null){
			data = new Object[0][0];
			this.files = new File[0];
			fireTableDataChanged();
			return;
		}
		data = new Object[files.length][titles.length];
		this.files = new File[files.length];
		
		for (int i = 0; i < files.length; i++) {
			File tmp = new File(dir.getAbsolutePath() + "\\" + files[i]);
			data[i][0] = tmp.getName();
			data[i][1] = 0;
			this.files[i] = tmp;
		}

		// Just in case anyone's listening...
		fireTableDataChanged();
	}

	public File getFile(int selectedRow) {
		try {
			return this.files[selectedRow];
		} catch(IndexOutOfBoundsException e) {
			return null;
		}
	}
}
