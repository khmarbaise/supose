package com.soebes.supose.cli;

import org.tmatesoft.svn.core.SVNDirEntry;

import com.soebes.supose.scan.interceptors.ChangeSetInterceptor;

public class CLIChangeSetInterceptor implements ChangeSetInterceptor {

	private int counter;

	public void beginIndexChangeSetItem(SVNDirEntry dirEntry) {
		System.out.printf("[%4d item size: %10d]", counter, dirEntry.getSize());
	}

	public void endIndexChangeSetItem(SVNDirEntry dirEntry) {
		System.out.printf("\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010\010");
		counter++;
	}

	public void startIndexChangeSet() {
		counter = 1;
	}

	public void stopIndexChangeSet() {
	}

}
