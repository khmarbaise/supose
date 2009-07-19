/**
 * The (Su)bversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007, 2008, 2009 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (c) 2007, 2008, 2009 by Karl Heinz Marbaise
 *
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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301 USA
 *
 * The License can viewed online under http://www.gnu.org/licenses/gpl.html
 * If you have any questions about the Software or about the license
 * just write an email to license@soebes.de
 */
package com.soebes.supose.cli;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertFalse;
import static org.testng.Assert.assertNotNull;
import static org.testng.Assert.assertTrue;

import java.util.List;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.OptionException;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * @author Karl Heinz Marbaise
 *
 */
@Test
public class SupposeCLITest {

	private SuposeCommandLine suposecli = null;

	@SuppressWarnings("unused")
	@BeforeClass
	private void beforeClass() throws Exception {
		suposecli = new SuposeCommandLine();
	}

	public void testNothingGiven() throws Exception {
		CommandLine cl = suposecli.doParseArgs(new String[0]);
		assertNotNull(cl, "The return value of the parse is null!");
	}

	public void testGlobalOptionH() throws Exception {
		final String[] args = new String[] { "-H" };
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertTrue(cl.hasOption(suposecli.getGlobalOptionH()));
	}

	public void testCommandCheckScan() throws Exception {
		final String[] args = new String[] { "scan", "--url", "URL" };
		CommandLine cl = suposecli.doParseArgs(args);
		assertTrue(cl.hasOption(suposecli.getScanCommand()));
		assertFalse(cl.hasOption(suposecli.getSearchCommand()));
	}

	public void testCommandCheckSearch() throws Exception {
		final String[] args = new String[] { "search" };
		CommandLine cl = suposecli.doParseArgs(args);
		assertFalse(cl.hasOption(suposecli.getScanCommand()));
		assertTrue(cl.hasOption(suposecli.getSearchCommand()));
	}


	@Test(expectedExceptions = OptionException.class)
	public void testCommandScan() throws Exception {
		final String[] args = new String[] { "scan" };
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertFalse(cl.hasOption(suposecli.getGlobalOptionH()), "Globel Help option set.");
		//We will never reach this, cause it will throw an exception
		assertTrue(cl.hasOption(suposecli.getScanCommand()));
	}

	public void testCommandScanAbbreviation() throws Exception {
		final String[] args = new String[] { "sc", "--url", "URL" };
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertFalse(cl.hasOption(suposecli.getGlobalOptionH()), "Globel Help option set.");
		assertTrue(cl.hasOption(suposecli.getScanCommand()));
	}

	public void testOptionHCommandScan() throws Exception {
		final String[] args = new String[] { "scan", "--url", "test", "-H"};
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertTrue(cl.hasOption(suposecli.getGlobalOptionH()), "Globel Help option not set.");
		assertTrue(cl.hasOption(suposecli.getScanCommand()));
	}

	@Test(expectedExceptions = OptionException.class)
	public void testCommandScanShortURLWithoutParameter() throws Exception {
		final String[] args = new String[] { "scan", "-U"};
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertFalse(cl.hasOption(suposecli.getGlobalOptionH()), "Globel Help option not set.");
		assertTrue(cl.hasOption(suposecli.getScanCommand()));
	}

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

	public void testCommandScanUsername() throws Exception {
		final String[] args = new String[] { "scan", "--url", "URL", "--username", "xyz"};
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

	public void testCommandScanPassword() throws Exception {
		final String[] args = new String[] { "scan", "--url", "URL", "--password", "xyz"};
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertFalse(cl.hasOption(suposecli.getGlobalOptionH()), "Globel Help option not set.");
		assertTrue(cl.hasOption(suposecli.getScanCommand()));

		ScanCommand scanCommand = suposecli.getScliScanCommand();

		String username = scanCommand.getUsername(cl);
		assertTrue(username == null, "The string for username is expected to be null!");

		String url = scanCommand.getURL(cl);
		assertTrue(url != null, "The string for URL is expected not to be null, cause it must be given");

		String password = scanCommand.getPassword(cl);
		assertNotNull(password, "The string for password is expected not to be null!");
		assertEquals(password, "xyz", "The parameter of the option --password option is not as expected");
	}

	public void testCommandFromRev() throws Exception {
		final String[] args = new String[] { "scan",  "--url", "URL", "--fromrev", "21"};
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertFalse(cl.hasOption(suposecli.getGlobalOptionH()), "Globel Help option not set.");
		assertTrue(cl.hasOption(suposecli.getScanCommand()));

		ScanCommand scanCommand = suposecli.getScliScanCommand();

		long fromRev = scanCommand.getFromRev(cl);
		long toRev = scanCommand.getToRev(cl);
		assertEquals(fromRev, 21, "We didn't get the expected fromRev value.");
		assertEquals(toRev, SVNRevision.HEAD.getNumber(), "We didn't get the expected toRev value(HEAD).");
	}

	public void testCommandFromRevHead() throws Exception {
		final String[] args = new String[] { "scan",  "--url", "URL", "--fromrev", "head"};
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertFalse(cl.hasOption(suposecli.getGlobalOptionH()), "Globel Help option not set.");
		assertTrue(cl.hasOption(suposecli.getScanCommand()));

		ScanCommand scanCommand = suposecli.getScliScanCommand();

		long fromRev = scanCommand.getFromRev(cl);
		long toRev = scanCommand.getToRev(cl);
		assertEquals(fromRev, SVNRevision.HEAD.getNumber(), "We didn't get the expected HEAD for fromRev.");
		assertEquals(toRev, SVNRevision.HEAD.getNumber(), "We didn't get the expected toRev value(HEAD).");
	}

	@Test(expectedExceptions = NumberFormatException.class)
	public void testCommandFromRevWrongParameter() throws Exception {
		final String[] args = new String[] { "scan",  "--url", "URL", "--fromrev", "elizabeth"};
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertFalse(cl.hasOption(suposecli.getGlobalOptionH()), "Globel Help option not set.");
		assertTrue(cl.hasOption(suposecli.getScanCommand()));

		ScanCommand scanCommand = suposecli.getScliScanCommand();

		long fromRev = scanCommand.getFromRev(cl);
		//We will never reach till here...
		assertEquals(fromRev, SVNRevision.HEAD.getNumber(), "We didn't get the expected HEAD for fromRev.");
	}

	public void testCommandFromToRev() throws Exception {
		final String[] args = new String[] { "scan", "--url", "URL", "--fromrev", "156", "--torev", "200"};
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertFalse(cl.hasOption(suposecli.getGlobalOptionH()), "Globle Help option not set.");
		assertTrue(cl.hasOption(suposecli.getScanCommand()));

		ScanCommand scanCommand = suposecli.getScliScanCommand();

		long fromRev = scanCommand.getFromRev(cl);
		long toRev = scanCommand.getToRev(cl);
		assertEquals(fromRev, 156, "We didn't get the expected fromRev value.");
		assertEquals(toRev, 200, "We didn't get the expected toRev value.");

		boolean create = cl.hasOption(scanCommand.getOptionCreate());
		assertFalse(create, "We had expected to get an deactivated create flag.");
	}

	public void testCommandScanCreate() throws Exception {
		final String[] args = new String[] { "scan", "--url", "URL", "--create" };
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertFalse(cl.hasOption(suposecli.getGlobalOptionH()), "Globle Help option not set.");
		assertTrue(cl.hasOption(suposecli.getScanCommand()));

		ScanCommand scanCommand = suposecli.getScliScanCommand();

		boolean create = cl.hasOption(scanCommand.getOptionCreate());
		assertTrue(create, "We had expected to get an activated create flag.");
	}

	public void testCommandSearch() throws Exception {
		final String[] args = new String[] { "search" };
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertFalse(cl.hasOption(suposecli.getGlobalOptionH()), "Globle Help option set.");
		assertTrue(cl.hasOption(suposecli.getSearchCommand()));
	}

	public void testCommandSearchAbbreviation() throws Exception {
		final String[] args = new String[] { "se" };
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertFalse(cl.hasOption(suposecli.getGlobalOptionH()), "Globle Help option set.");
		assertTrue(cl.hasOption(suposecli.getSearchCommand()));
	}

	@Test(expectedExceptions = OptionException.class)
	public void testCommandSearchFieldsWrong() throws Exception {
		final String[] args = new String[] { "search", "--fields", "test"};
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertFalse(cl.hasOption(suposecli.getGlobalOptionH()), "Globle Help option set.");
		assertTrue(cl.hasOption(suposecli.getSearchCommand()));
	}

	public void testCommandSearchFields() throws Exception {
		final String[] args = new String[] { "search", "--fields", "author", "revision", "date"};
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertFalse(cl.hasOption(suposecli.getGlobalOptionH()), "Globle Help option set.");
		assertTrue(cl.hasOption(suposecli.getSearchCommand()));
	}
	
	public void testCommandSearchFieldsResult() throws Exception {
		final String[] args = new String[] { "search", "--fields", "author", "revision", "date"};
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertFalse(cl.hasOption(suposecli.getGlobalOptionH()), "Globle Help option set.");
		assertTrue(cl.hasOption(suposecli.getSearchCommand()));

		SearchCommand searchCommand = suposecli.getScliSearchCommand();
		List<String> cliFields = searchCommand.getFields(cl);
		assertEquals(cliFields.get(0), "author");
		assertEquals(cliFields.get(1), "revision");
		assertEquals(cliFields.get(2), "date");
		
	}

	public void testCommandSearchXML() throws Exception {
		final String[] args = new String[] { "search", "--xml"};
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertFalse(cl.hasOption(suposecli.getGlobalOptionH()), "Globle Help option set.");
		assertTrue(cl.hasOption(suposecli.getSearchCommand()));
		SearchCommand searchCommand = suposecli.getScliSearchCommand();
		
		boolean xml = cl.hasOption(searchCommand.getOptionXML());
		assertTrue(xml, "We had expected to get an activated XML flag.");
	}
	
	public void testOptionHCommandSearch() throws Exception {
		final String[] args = new String[] { "search", "-H" };
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertTrue(cl.hasOption(suposecli.getGlobalOptionH()), "Globle Help option not set.");
		assertTrue(cl.hasOption(suposecli.getSearchCommand()));
	}
	
	public void testMergeCommand() throws Exception {
		final String[] args = new String[] { "merge", "--destination", "result" };
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertFalse(cl.hasOption(suposecli.getGlobalOptionH()), "Globle Help option not set.");
		assertTrue(cl.hasOption(suposecli.getMergeCommand()));
	}

	public void testMergeCommandIndexList() throws Exception {
		final String[] args = new String[] { "merge", "--index", "anton", "egon", "--destination", "result" };
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertFalse(cl.hasOption(suposecli.getGlobalOptionH()), "Globle Help option not set.");
		assertFalse(cl.hasOption(suposecli.getScanCommand()));
		assertFalse(cl.hasOption(suposecli.getSearchCommand()));
		assertFalse(cl.hasOption(suposecli.getScheduleCommand()));
		assertTrue(cl.hasOption(suposecli.getMergeCommand()));
		
		MergeCommand mergeCommand = suposecli.getScliMergeCommand();

		List<String> indexList = mergeCommand.getIndex(cl);
		assertNotNull(indexList, "We had expected to get a list of index direcotriy");
		assertEquals(indexList.size(), 2, "We had expected to get a least two elements");
	}
	
	public void testScheduleCommand() throws Exception {
		final String[] args = new String[] {
			"schedule", 
			"--configuration", 
			"/home/kama/supose/test", 
			"--configbase", "/home/kama/base" 
		};
		CommandLine cl = suposecli.doParseArgs(args);
		assertNotNull(cl, "The return value of the parse is null!");
		assertFalse(cl.hasOption(suposecli.getGlobalOptionH()), "Globle Help option not set.");
		assertFalse(cl.hasOption(suposecli.getScanCommand()));
		assertFalse(cl.hasOption(suposecli.getSearchCommand()));
		assertFalse(cl.hasOption(suposecli.getMergeCommand()));
		assertTrue(cl.hasOption(suposecli.getScheduleCommand()));
		
		ScheduleCommand scheduleCommand = suposecli.getScliScheduleCommand();
		String configBaseDir = scheduleCommand.getConfBaseDir(cl);
		assertTrue(configBaseDir.length() > 0, "We had expected to get information for --configbasedir ..");
		assertEquals(configBaseDir, "/home/kama/base", "We had expected to get exactly /home/kama/base");
	}
}
