package com.soebes.supose.scan;

import java.util.ArrayList;

import org.tmatesoft.svn.core.SVNLogEntry;

import com.soebes.supose.scan.interceptors.LogEntryInterceptor;

public class ScanRepositoryBaseLogEntry extends ScanRepositoryBaseChangeSet implements LogEntryInterceptor {
	private ArrayList<LogEntryInterceptor> logEntryInterceptors;

	public ScanRepositoryBaseLogEntry() {
		super();
		setLogEntryInterceptors(new ArrayList<LogEntryInterceptor>());
	}

	public void LogEntryStart() {
		for (LogEntryInterceptor item : getLogEntryInterceptors()) {
			item.LogEntryStart();
		}
	}

	public void LogEntry(SVNLogEntry logEntry) {
		for (LogEntryInterceptor item : getLogEntryInterceptors()) {
			item.LogEntry(logEntry);
		}
	}

	public void LogEntryStop() {
		for (LogEntryInterceptor item : getLogEntryInterceptors()) {
			item.LogEntryStop();
		}
	}
	
	public void registerLogEntryInterceptor(LogEntryInterceptor interceptor) {
		getLogEntryInterceptors().add(interceptor);
	}

	public void setLogEntryInterceptors(ArrayList<LogEntryInterceptor> logEntryInterceptors) {
		this.logEntryInterceptors = logEntryInterceptors;
	}

	public ArrayList<LogEntryInterceptor> getLogEntryInterceptors() {
		return logEntryInterceptors;
	}

}
