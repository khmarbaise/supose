package com.soebes.supose.recognition;

public class RenameType {

	private String sourceName;
	private String destinationName;
	private long revision;
	private long copyFromRevision;
	
	public String getSourceName() {
		return sourceName;
	}
	public void setSourceName(String sourceName) {
		this.sourceName = sourceName;
	}
	public String getDestinationName() {
		return destinationName;
	}
	public void setDestinationName(String destinationName) {
		this.destinationName = destinationName;
	}
	public long getRevision() {
		return revision;
	}
	public void setRevision(long revision) {
		this.revision = revision;
	}
	public long getCopyFromRevision() {
		return copyFromRevision;
	}
	public void setCopyFromRevision(long copyFromRevision) {
		this.copyFromRevision = copyFromRevision;
	}

}
