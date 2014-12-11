package files;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class MetaFile {
	private final File file;
	private final List<Integer> regionDieValue = new ArrayList<Integer>();
	
	public MetaFile(File file) {
		this.file = file;
	}
	
	public int getRegionCount(){
		return regionDieValue.size();
	}
	
	public void addClassifiedRegion(Integer regionValue) {
		regionDieValue.add(regionValue);
	}
	
	public void addUnclassifiedRegion() {
		regionDieValue.add(null);
	}
	
	public boolean setRegionClassification(int regionNumber, Integer regionValue) {
		try{
			regionDieValue.set(regionNumber, regionValue);
			return true;
		} catch(IndexOutOfBoundsException e) {
			return false;
		}
	}

	public File getFile() {
		return file;
	}

	public String getRegionString() {
		StringBuilder sb = new StringBuilder();
		for(int i = 0; i < regionDieValue.size(); i++) {
			sb.append(regionDieValue.get(i));
			if(i < regionDieValue.size()-1) {
				sb.append(", ");
			}
		}
		return sb.toString();
	}

	public void clearRegions() {
		regionDieValue.clear();
	}

	 public void setRegionNumber(int size) {
		for(int i = 0; i < size; i++)
			addUnclassifiedRegion();
	}
}
