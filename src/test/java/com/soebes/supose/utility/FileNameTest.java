/*
 * The (S)ubversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (C) 2007 by Karl Heinz Marbaise

 * This program is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA *
 *
 * The License can viewed online under http://www.gnu.org/licenses/gpl.html
 * If you have any questions about the Software or about the license
 * just write an email to license@soebes.de
 *
 */
package com.soebes.supose.utility;

import static org.testng.Assert.assertEquals;

import org.testng.annotations.Test;

import com.soebes.supose.utility.FileName;

/**
 * @author Karl Heinz Marbaise
 *
 */
public class FileNameTest {

	@Test
	public void testF10() {
		String fileName = "/usr/local/test.zip";
		FileName fn = new FileName(fileName);
		assertEquals(fn.getExt(), "zip", "The extension is not as expected.");
		assertEquals(fn.getBaseName(), "test.zip", "The basename is not as expected.");
		assertEquals(fn.getPath(), "/usr/local", "The basename is not as expected.");
	}
	
	@Test
	public void testF20() {
		String fileName = "/usr/local/test.tar.gz";
		FileName fn = new FileName(fileName);
		assertEquals(fn.getExt(), "tar.gz", "The extension is not as expected.");
		assertEquals(fn.getBaseName(), "test.tar.gz", "The basename is not as expected.");
		assertEquals(fn.getPath(), "/usr/local", "The basename is not as expected.");
	}

	@Test
	public void testF30() {
		String fileName = "C:/Programme/x-y-z.zip";
		FileName fn = new FileName(fileName);
		assertEquals(fn.getExt(), "zip", "The extension is not as expected.");
		assertEquals(fn.getBaseName(), "x-y-z.zip", "The basename is not as expected.");
		assertEquals(fn.getPath(), "C:/Programme", "The basename is not as expected.");
	}

	@Test
	public void testF40() {
		String fileName = "/x.zip";
		FileName fn = new FileName(fileName);
		assertEquals(fn.getExt(), "zip", "The extension is not as expected.");
		assertEquals(fn.getBaseName(), "x.zip", "The basename is not as expected.");
		assertEquals(fn.getPath(), "/", "The basename is not as expected.");
	}
}
