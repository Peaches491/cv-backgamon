package components.base;

import org.opencv.core.Mat;

import files.MetaFile;

public class ProcessInfo {
	private String filePath;
	private String fileName;
	private String activeDirectory;
	private Mat overlayMat;
	private Mat originalMat;
	private MetaFile metaFile;
	
	public ProcessInfo(String _filePath, String _fileName, String _activeDirectory, Mat _overlayMat, Mat _originalMat) {
		this.filePath = _filePath;
		this.fileName = _fileName;
		this.activeDirectory = _activeDirectory;
		this.overlayMat = _overlayMat;
		this.originalMat = _originalMat;
	}
	
	public String getActiveDirectory() {
		return activeDirectory;
	}
	
	public void setActiveDirectory(String activeDirectory) {
		this.activeDirectory = activeDirectory;
	}
	
	public String getFileName() {
		return fileName;
	}
	
	public void setFileName(String fileName) {
		this.fileName = fileName;
	}
	
	public String getFilePath() {
		return filePath;
	}
	
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	public Mat getOverlayMat() {
		return overlayMat;
	}

	public void setOverlayMat(Mat overlayMat) {
		this.overlayMat = overlayMat;
	}

	public Mat getOriginalMat() {
		return originalMat;
	}

	public void setOriginalMat(Mat originalMat) {
		this.originalMat = originalMat;
	}
	
	public MetaFile getMetaFile(){
		return metaFile;
	}

	public void setMetaFile(MetaFile metaFile) {
		this.metaFile = metaFile;
	}
}
