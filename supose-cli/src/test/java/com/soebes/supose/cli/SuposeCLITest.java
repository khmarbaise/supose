/**
 * The (Su)bversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007-2011 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (c) 2007-2011 by Karl Heinz Marbaise
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
import static org.testng.Assert.assertTrue;

import java.io.File;

import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.beust.jcommander.ParameterException;
import com.soebes.supose.cli.SupoSECommandLine.SupoSECommands;

/**
 * @author Karl Heinz Marbaise
 *
 */
@Test
public class SuposeCLITest {

    private SupoSECommandLine suposecli = null;

    @SuppressWarnings("unused")
    @BeforeClass
    private void beforeClass() throws Exception {
    }

    public void nothingGiven() throws Exception {
        suposecli = new SupoSECommandLine(new String[0]);
        assertFalse(suposecli.isHelpForCommand());
    }

    public void globalOptionH() throws Exception {
        final String[] args = new String[] { "--help" };
        suposecli = new SupoSECommandLine(args);
        assertFalse(suposecli.isHelpForCommand());
        assertTrue(suposecli.getMainCommand().isHelp());
    }

    public void globalVersion() throws Exception {
        final String[] args = new String[] { "--version" };
        suposecli = new SupoSECommandLine(args);
        assertFalse(suposecli.isHelpForCommand());
        assertFalse(suposecli.getMainCommand().isHelp());
        assertTrue(suposecli.getMainCommand().isVersion());
    }
    
    public void scanCommandWithURLParameter() throws Exception {
        final String[] args = new String[] { "scan", "--url", "http://www.google.de" };

        suposecli = new SupoSECommandLine(args);
        assertFalse(suposecli.getMainCommand().isHelp());
        assertFalse(suposecli.isHelpForCommand());

        assertEquals(suposecli.getCommand(), SupoSECommands.SCAN);

        assertEquals(suposecli.getScanCommand().getUri().toString(), "http://www.google.de");
        assertEquals(suposecli.getScanCommand().getFromRev(), 1);
        assertEquals(suposecli.getScanCommand().getToRev(), -1);
    }

    public void scanCommandWithURLAndFromRevParameter() throws Exception {
        final String[] args = new String[] { "scan", "--fromrev", "200", "--url", "file:///repos/x1" };

        suposecli = new SupoSECommandLine(args);
        assertFalse(suposecli.getMainCommand().isHelp());
        assertFalse(suposecli.isHelpForCommand());

        assertEquals(suposecli.getCommand(), SupoSECommands.SCAN);

        assertEquals(suposecli.getScanCommand().getUri().toString(), "file:///repos/x1");
        assertEquals(suposecli.getScanCommand().getFromRev(), 200);
        assertEquals(suposecli.getScanCommand().getToRev(), -1);
    }

    public void scanCommandWithURLAndToRevParameter() throws Exception {
        final String[] args = new String[] { "scan", "--torev", "22000", "--url", "file:///repos/x1" };

        suposecli = new SupoSECommandLine(args);
        assertFalse(suposecli.getMainCommand().isHelp());
        assertFalse(suposecli.isHelpForCommand());

        assertEquals(suposecli.getCommand(), SupoSECommands.SCAN);

        assertEquals(suposecli.getScanCommand().getUri().toString(), "file:///repos/x1");
        assertEquals(suposecli.getScanCommand().getFromRev(), 1);
        assertEquals(suposecli.getScanCommand().getToRev(), 22000);
    }

    public void scanCommandWithURLAndToRevHeadParameter() throws Exception {
        final String[] args = new String[] { "scan", "--torev", "HEAD", "--url", "file:///repos/x1" };

        suposecli = new SupoSECommandLine(args);
        assertFalse(suposecli.getMainCommand().isHelp());
        assertFalse(suposecli.isHelpForCommand());

        assertEquals(suposecli.getCommand(), SupoSECommands.SCAN);

        assertEquals(suposecli.getScanCommand().getUri().toString(), "file:///repos/x1");
        assertEquals(suposecli.getScanCommand().getFromRev(), 1);
        assertEquals(suposecli.getScanCommand().getToRev(), -1);
    }

    @Test(expectedExceptions = ParameterException.class )
    public void scanCommandWithURLAndWrongToRevParameter() {
        final String[] args = new String[] { "scan", "--torev", "test", "--url", "file:///repos/x1" };

        suposecli = new SupoSECommandLine(args);
    }

    public void scanCommandWithURLAndFromRevAndToRevParameter() throws Exception {
        final String[] args = new String[] { "scan", "--fromrev", "100", "--torev", "22123", "--url", "file:///repos/x1" };

        suposecli = new SupoSECommandLine(args);
        assertFalse(suposecli.getMainCommand().isHelp());
        assertFalse(suposecli.isHelpForCommand());

        assertEquals(suposecli.getCommand(), SupoSECommands.SCAN);

        assertEquals(suposecli.getScanCommand().getUri().toString(), "file:///repos/x1");
        assertEquals(suposecli.getScanCommand().getFromRev(), 100);
        assertEquals(suposecli.getScanCommand().getToRev(), 22123);
    }

    public void scanCommandWithURLAndFromRevAndToRevAndUserNameAndPasswordParameter() throws Exception {
        final String[] args = new String[] { "scan", "--username", "jerimia", "--password", "test", "--fromrev", "101", "--torev", "22124", "--url", "file:///repos/x1" };

        suposecli = new SupoSECommandLine(args);
        assertFalse(suposecli.getMainCommand().isHelp());
        assertFalse(suposecli.isHelpForCommand());

        assertEquals(suposecli.getCommand(), SupoSECommands.SCAN);

        assertEquals(suposecli.getScanCommand().getUri().toString(), "file:///repos/x1");
        assertEquals(suposecli.getScanCommand().getFromRev(), 101);
        assertEquals(suposecli.getScanCommand().getToRev(), 22124);
        assertEquals(suposecli.getScanCommand().getUsername(), "jerimia");
        assertEquals(suposecli.getScanCommand().getPassword(), "test");
    }

    public void searchCommand() throws Exception {
        final String[] args = new String[] { "search" };
        suposecli = new SupoSECommandLine(args);
        assertFalse(suposecli.getMainCommand().isHelp());
        assertFalse(suposecli.isHelpForCommand());

        assertEquals(suposecli.getCommand(), SupoSECommands.SEARCH);
        assertFalse(suposecli.getSearchCommand().isXML());
    }

    public void searchCommandWithIndexParameter() throws Exception {
        final String[] args = new String[] { "search", "--index", "testIndex" };
        suposecli = new SupoSECommandLine(args);
        assertFalse(suposecli.getMainCommand().isHelp());
        assertFalse(suposecli.isHelpForCommand());

        assertEquals(suposecli.getCommand(), SupoSECommands.SEARCH);
        assertFalse(suposecli.getSearchCommand().isXML());
        assertEquals(suposecli.getSearchCommand().getIndexName(), "testIndex");

    }

    public void searchCommandWithIndexAndQueryParameter() throws Exception {
        final String[] args = new String[] { "search", "--index", "testIndex", "--query", "+filename:*.doc" };
        suposecli = new SupoSECommandLine(args);
        assertFalse(suposecli.getMainCommand().isHelp());
        assertFalse(suposecli.isHelpForCommand());

        assertEquals(suposecli.getCommand(), SupoSECommands.SEARCH);
        assertFalse(suposecli.getSearchCommand().isXML());
        assertEquals(suposecli.getSearchCommand().getIndexName(), "testIndex");
        assertEquals(suposecli.getSearchCommand().getQuery(), "+filename:*.doc");
    }

    public void searchCommandWithIndexAndQueryAndXMLParameter() throws Exception {
        final String[] args = new String[] { "search", "--index", "testIndex", "--query", "+filename:*.doc", "--xml" };
        suposecli = new SupoSECommandLine(args);
        assertFalse(suposecli.getMainCommand().isHelp());
        assertFalse(suposecli.isHelpForCommand());

        assertEquals(suposecli.getCommand(), SupoSECommands.SEARCH);
        assertEquals(suposecli.getSearchCommand().getIndexName(), "testIndex");
        assertEquals(suposecli.getSearchCommand().getQuery(), "+filename:*.doc");
        assertTrue(suposecli.getSearchCommand().isXML());
    }


    public void searchCommandWithIndexAndQueryAndFieldsParameter() throws Exception {
        final String[] args = new String[] { "search", "--index", "testIndex", "--query", "+filename:*.doc", "--field", "revision", "--field", "author" };
        suposecli = new SupoSECommandLine(args);
        assertFalse(suposecli.getMainCommand().isHelp());
        assertFalse(suposecli.isHelpForCommand());

        assertEquals(suposecli.getCommand(), SupoSECommands.SEARCH);
        assertFalse(suposecli.getSearchCommand().isXML());
        assertEquals(suposecli.getSearchCommand().getIndexName(), "testIndex");
        assertEquals(suposecli.getSearchCommand().getQuery(), "+filename:*.doc");

//FIXME: What about the default values for the fields? How to handle this?
        assertEquals(suposecli.getSearchCommand().getFields().size(), 2);
    }

    public void mergeCommand() throws Exception {
        final String[] args = new String[] { "merge" };
        suposecli = new SupoSECommandLine(args);
        assertFalse(suposecli.getMainCommand().isHelp());
        assertFalse(suposecli.isHelpForCommand());

        assertEquals(suposecli.getCommand(), SupoSECommands.MERGE);
    }

    public void mergeCommandWithSingleIndexAndDestinationParameters() throws Exception {
        final String[] args = new String[] { "merge", "--index", "test1", "--destination", "dest" };
        suposecli = new SupoSECommandLine(args);
        assertFalse(suposecli.getMainCommand().isHelp());
        assertFalse(suposecli.isHelpForCommand());

        assertEquals(suposecli.getCommand(), SupoSECommands.MERGE);
        assertEquals(suposecli.getMergeCommand().getIndexes().size(), 1);
        assertEquals(suposecli.getMergeCommand().getIndexes().get(0), new File("test1"));
        assertEquals(suposecli.getMergeCommand().getDestinationIndex(), new File("dest"));
    }

    public void mergeCommandWithMultipleIndexesAndDestinationParameters() throws Exception {
        final String[] args = new String[] { "merge", "--index", "test1", "--index", "test2", "--destination", "dest" };
        suposecli = new SupoSECommandLine(args);
        assertFalse(suposecli.getMainCommand().isHelp());
        assertFalse(suposecli.isHelpForCommand());

        assertEquals(suposecli.getCommand(), SupoSECommands.MERGE);
        assertEquals(suposecli.getMergeCommand().getIndexes().size(), 2);
        assertEquals(suposecli.getMergeCommand().getIndexes().get(0), new File("test1"));
        assertEquals(suposecli.getMergeCommand().getIndexes().get(1), new File("test2"));
        assertEquals(suposecli.getMergeCommand().getDestinationIndex(), new File("dest"));
    }

    public void scheduleCommand() throws Exception {
        final String[] args = new String[] { "schedule" };
        suposecli = new SupoSECommandLine(args);
        assertFalse(suposecli.getMainCommand().isHelp());
        assertFalse(suposecli.isHelpForCommand());

        assertEquals(suposecli.getCommand(), SupoSECommands.SCHEDULE);
    }

    public void scheduleCommandWithoutParameters() throws Exception {
        final String[] args = new String[] { "schedule" };
        suposecli = new SupoSECommandLine(args);
        assertFalse(suposecli.getMainCommand().isHelp());
        assertFalse(suposecli.isHelpForCommand());

        assertEquals(suposecli.getCommand(), SupoSECommands.SCHEDULE);
        assertEquals(suposecli.getScheduleCommand().getConfigurationBase(), null);
        assertEquals(suposecli.getScheduleCommand().getConfigurationFile(), null);
    }

    public void scheduleCommandWithConfigurationBase() throws Exception {
        final String[] args = new String[] { "schedule", "--configbase", "configbase" };
        suposecli = new SupoSECommandLine(args);
        assertFalse(suposecli.getMainCommand().isHelp());
        assertFalse(suposecli.isHelpForCommand());

        assertEquals(suposecli.getCommand(), SupoSECommands.SCHEDULE);
        assertEquals(suposecli.getScheduleCommand().getConfigurationBase(), new File("configbase"));
        assertEquals(suposecli.getScheduleCommand().getConfigurationFile(), null);
    }

    public void scheduleCommandWithConfigurationBaseAndConfiguration() throws Exception {
        final String[] args = new String[] { "schedule", "--configbase", "configbase", "--configuration", "configuration" };
        suposecli = new SupoSECommandLine(args);
        assertFalse(suposecli.getMainCommand().isHelp());
        assertFalse(suposecli.isHelpForCommand());

        assertEquals(suposecli.getCommand(), SupoSECommands.SCHEDULE);
        assertEquals(suposecli.getScheduleCommand().getConfigurationBase(), new File("configbase"));
        assertEquals(suposecli.getScheduleCommand().getConfigurationFile(), new File("configuration"));
    }

}
