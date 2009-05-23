package com.soebes.supose.scan;

import java.util.ArrayList;

import org.tmatesoft.svn.core.SVNLogEntry;

public class ScanRepositoryBaseLogEntry implements LogEntryInterceptor {
	private ArrayList<LogEntryInterceptor> logEntryInterceptors;

	public ScanRepositoryBaseLogEntry() {
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
