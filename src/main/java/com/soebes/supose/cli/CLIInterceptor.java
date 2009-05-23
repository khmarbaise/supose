package com.soebes.supose.cli;

import com.soebes.supose.scan.interceptors.ScanInterceptor;

public class CLIInterceptor implements ScanInterceptor{
	private Integer numberOfRevisions;

	public void scanStart(Integer revision) {
		numberOfRevisions = revision;
		System.out.println("We will scan " + revision);
	}
	
	public void scanStop() {
		//Nothing will be done at the end.
	}
	public void scanBeginRevision(Long revision, Integer changeSetSize) {
		Long div = revision *100 / numberOfRevisions;
		System.out.printf("%3d %% of %7d (Revision:%7d) Changeset: %4d ", div, numberOfRevisions, revision, changeSetSize);
	}
	
	public void scanEndRevision(Long revision, Integer changeSetSize) {
		//We will do nothing.
		System.out.print("\r");
	}

}
