package com.soebes.supose.cli;

import com.soebes.supose.scan.ScanInterceptor;

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
		System.out.printf("%3d %% of %7d (Changeset size: %3d) (Rev:%7d)    \r", div, numberOfRevisions, changeSetSize, revision);
	}
	
	public void scanEndRevision(Long revision, Integer changeSetSize) {
		//We will do nothing.
	}

}
