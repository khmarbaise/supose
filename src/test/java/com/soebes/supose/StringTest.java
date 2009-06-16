package com.soebes.supose;

import org.testng.annotations.Test;

public class StringTest {

	@Test
	public void testFormat1() {
		double f = 2.34456;
		Long l = new Long(1313123);

		String result = String.format("%.3f '%10d'", f, l);
		System.out.println("result=" + result);
	}
}
