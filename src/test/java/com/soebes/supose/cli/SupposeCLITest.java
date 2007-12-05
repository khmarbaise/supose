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
// SupoSE
package com.soebes.supose.cli;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import org.apache.commons.cli2.CommandLine;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * @author Karl Heinz Marbaise
 *
 */
public class SupposeCLITest {

	private SuposeCommandLine suposecli = null;

	@SuppressWarnings("unused")
	@BeforeClass
	private void beforeClass() throws Exception {
		suposecli = new SuposeCommandLine();
	}

	@Test()
	public void testNothingGiven() throws Exception {
		CommandLine cl = suposecli.doParseArgs(new String[0]);
		assertNotNull(cl, "The return value of the parse is null!");
	}

	@Test()
	public void testGlobalOptionH() throws Exception {
		final String[] args = new String[] { "-H" };
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertTrue(cl.hasOption(suposecli.getGlobalOptionH()));
	}

	
	@Test()
	public void testCommandCheckScan() throws Exception {
		final String[] args = new String[] { "scan" };
		CommandLine cl = suposecli.doParseArgs(args);
		assertTrue(cl.hasOption(suposecli.getScanCommand()));
		assertFalse(cl.hasOption(suposecli.getSearchCommand()));
	}
	@Test()
	public void testCommandCheckSearch() throws Exception {
		final String[] args = new String[] { "search" };
		CommandLine cl = suposecli.doParseArgs(args);
		assertFalse(cl.hasOption(suposecli.getScanCommand()));
		assertTrue(cl.hasOption(suposecli.getSearchCommand()));
	}

	@Test()
	public void testCommandScan() throws Exception {
		final String[] args = new String[] { "scan" };
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertFalse(cl.hasOption(suposecli.getGlobalOptionH()), "Globel Help option set.");
		assertTrue(cl.hasOption(suposecli.getScanCommand()));
	}

	@Test()
	public void testCommandScanAbbreviation() throws Exception {
		final String[] args = new String[] { "sc" };
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertFalse(cl.hasOption(suposecli.getGlobalOptionH()), "Globel Help option set.");
		assertTrue(cl.hasOption(suposecli.getScanCommand()));
	}

	@Test()
	public void testOptionHCommandScan() throws Exception {
		final String[] args = new String[] { "scan", "-H"};
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertTrue(cl.hasOption(suposecli.getGlobalOptionH()), "Globel Help option not set.");
		assertTrue(cl.hasOption(suposecli.getScanCommand()));
	}

	@Test()
	public void testCommandScanShortURL() throws Exception {
		final String[] args = new String[] { "scan", "-U", "file:///testrepos/private"};
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertFalse(cl.hasOption(suposecli.getGlobalOptionH()), "Globel Help option not set.");
		assertTrue(cl.hasOption(suposecli.getScanCommand()));


		ScanCommand scanCommand = suposecli.getScliScanCommand();
		String url = scanCommand.getURL(cl);
		assertNotNull(url, "The string for URL is expected not to be null!");
		assertEquals(url, "file:///testrepos/private", "The parameter of the option --URL option is not as expected");
	}

	@Test()
	public void testCommandScanLongURL() throws Exception {
		final String[] args = new String[] { "scan", "--url", "file:///testrepos/private"};
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertFalse(cl.hasOption(suposecli.getGlobalOptionH()), "Globel Help option not set.");
		assertTrue(cl.hasOption(suposecli.getScanCommand()));

		ScanCommand scanCommand = suposecli.getScliScanCommand();
		String url = scanCommand.getURL(cl);
		assertNotNull(url, "The string for URL is expected not to be null!");
		assertEquals(url, "file:///testrepos/private", "The parameter of the option --URL option is not as expected");
	}

	@Test()
	public void testCommandScanUsername() throws Exception {
		final String[] args = new String[] { "scan", "--username", "xyz"};
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertFalse(cl.hasOption(suposecli.getGlobalOptionH()), "Globel Help option not set.");
		assertTrue(cl.hasOption(suposecli.getScanCommand()));

		ScanCommand scanCommand = suposecli.getScliScanCommand();
		String password = scanCommand.getPassword(cl);
		assertTrue(password == null, "The string for password is expected be null!");
		
		String username = scanCommand.getUsername(cl);
		assertNotNull(username, "The string is expected not be null!");
		assertEquals(username, "xyz", "The parameter of the option --username is not as expected");
	}

	@Test()
	public void testCommandScanPassword() throws Exception {
		final String[] args = new String[] { "scan", "--password", "xyz"};
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertFalse(cl.hasOption(suposecli.getGlobalOptionH()), "Globel Help option not set.");
		assertTrue(cl.hasOption(suposecli.getScanCommand()));

		ScanCommand scanCommand = suposecli.getScliScanCommand();

		String username = scanCommand.getUsername(cl);
		assertTrue(username == null, "The string for username is expected to be null!");

		String url = scanCommand.getURL(cl);
		assertTrue(url == null, "The string for URL is expected to be null!");

		String password = scanCommand.getPassword(cl);
		assertNotNull(password, "The string for password is expected not to be null!");
		assertEquals(password, "xyz", "The parameter of the option --password option is not as expected");
	}

	@Test()
	public void testCommandSearch() throws Exception {
		final String[] args = new String[] { "search" };
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertFalse(cl.hasOption(suposecli.getGlobalOptionH()), "Globel Help option set.");
		assertTrue(cl.hasOption(suposecli.getSearchCommand()));
	}

	@Test()
	public void testCommandSearchAbbreviation() throws Exception {
		final String[] args = new String[] { "se" };
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertFalse(cl.hasOption(suposecli.getGlobalOptionH()), "Globel Help option set.");
		assertTrue(cl.hasOption(suposecli.getSearchCommand()));
	}

	@Test()
	public void testOptionHCommandSearch() throws Exception {
		final String[] args = new String[] { "search", "-H" };
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertTrue(cl.hasOption(suposecli.getGlobalOptionH()), "Globel Help option not set.");
		assertTrue(cl.hasOption(suposecli.getSearchCommand()));
	}

}
