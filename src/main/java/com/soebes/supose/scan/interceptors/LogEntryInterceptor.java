package com.soebes.supose.scan.interceptors;

import org.tmatesoft.svn.core.SVNLogEntry;

public interface LogEntryInterceptor {

	void LogEntryStart();
	void LogEntry(SVNLogEntry logEntry);
	void LogEntryStop();
}
