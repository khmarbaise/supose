package com.soebes.supose.recognition;

public class BranchType {

	public enum Type {
		BRANCH,
		TAG
	}

	private Type type;
	private String name;
	private long revision;
	private long copyFromRevision;

	public BranchType(String name, long revision, long copyFromRevision, Type type) {
		super();
		this.copyFromRevision = copyFromRevision;
		this.name = name;
		this.revision = revision;
		this.type = type;
	}

	public BranchType() {
		this.name = null;
		this.type = null;
		this.revision = -1;
		this.copyFromRevision = -1;
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
	public Type getType() {
		return type;
	}
	public void setType(Type type) {
		this.type = type;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}

	
}
