package com.soebes.supose.scan.interceptors;

import org.tmatesoft.svn.core.SVNDirEntry;

public interface ChangeSetInterceptor {

	void startIndexChangeSet();
	void beginIndexChangeSetItem(SVNDirEntry dirEntry);
	void endIndexChangeSetItem(SVNDirEntry dirEntry);
	void stopIndexChangeSet();
}
