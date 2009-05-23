package com.soebes.supose.cli;

import org.tmatesoft.svn.core.SVNLogEntry;

import com.soebes.supose.scan.interceptors.LogEntryInterceptor;

public class CLILogEntryInterceptor implements LogEntryInterceptor {

	public void LogEntryStart() {
	}
	
	public void LogEntry(SVNLogEntry logEntry) {
		System.out.printf("Log entry processing: %7d\r", logEntry.getRevision());
	}

	public void LogEntryStop() {
		System.out.println("");
	}

}
