package com.soebes.supose.utility;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;

public class AnalyzerFactory {

	public static Analyzer createInstance() {
		return new StandardAnalyzer();
	}
}
