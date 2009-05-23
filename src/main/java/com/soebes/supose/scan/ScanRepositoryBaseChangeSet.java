package com.soebes.supose.scan;

import java.util.ArrayList;

import org.tmatesoft.svn.core.SVNDirEntry;

import com.soebes.supose.scan.interceptors.ChangeSetInterceptor;

public class ScanRepositoryBaseChangeSet implements ChangeSetInterceptor {
	private ArrayList<ChangeSetInterceptor> changeSetInterceptors;

	public ScanRepositoryBaseChangeSet() {
		setChangeSetInterceptors(new ArrayList<ChangeSetInterceptor>());
	}

	public void beginIndexChangeSetItem(SVNDirEntry dirEntry) {
		for (ChangeSetInterceptor item : getChangeSetInterceptors()) {
			item.beginIndexChangeSetItem(dirEntry);
		}
	}

	public void endIndexChangeSetItem(SVNDirEntry dirEntry) {
		for (ChangeSetInterceptor item : getChangeSetInterceptors()) {
			item.endIndexChangeSetItem(dirEntry);
		}
	}

	public void startIndexChangeSet() {
		for (ChangeSetInterceptor item : getChangeSetInterceptors()) {
			item.startIndexChangeSet();
		}
	}

	public void stopIndexChangeSet() {
		for (ChangeSetInterceptor item : getChangeSetInterceptors()) {
			item.stopIndexChangeSet();
		}
	}

	public void registerChangeSetInterceptor(ChangeSetInterceptor interceptor) {
		getChangeSetInterceptors().add(interceptor);
	}

	public void setChangeSetInterceptors(ArrayList<ChangeSetInterceptor> changeSetInterceptors) {
		this.changeSetInterceptors = changeSetInterceptors;
	}

	public ArrayList<ChangeSetInterceptor> getChangeSetInterceptors() {
		return changeSetInterceptors;
	}

}
