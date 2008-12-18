package com.soebes.supose.search;

import java.text.DecimalFormat;

public class NumberUtils {
	private static final DecimalFormat formatter = new DecimalFormat("0000000000000000000");

	public static String pad(long n) {
		return formatter.format(n);
	}
}
