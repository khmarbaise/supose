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

import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;

import com.beust.jcommander.JCommander;

/**
 * SupoSE CLI Client.
 *
 * @author Karl Heinz Marbaise
 */
public class SupoSECommandLine {
    private static Logger LOGGER = Logger.getLogger(SupoSECommandLine.class);

    public static final String PROGRAMM_NAME = "supose";

    /**
     * This enumeration type defines the existing commands
     * which means their names which have to be used on command line.
     *
     */
    public enum SupoSECommands {
        MERGE("merge"),       // supose merge
        SCAN("scan"),         // supose scan
        SEARCH("search"),     // supose search
        SCHEDULE("schedule"); // supose schedule

        private String commandName;
        private SupoSECommands(String commandName) {
            this.commandName = commandName;
        }
        public String getCommandName() {
            return commandName;
        }

    }

    private Map<SupoSECommands, ICommand> commandList = null;

    private final MainCommand mainCommand;

    private final JCommander commander;

    public SupoSECommandLine(String[] args) {
        commandList = new HashMap<SupoSECommands, ICommand>();

        commandList.put(SupoSECommands.MERGE, new MergeCommand());
        commandList.put(SupoSECommands.SCAN, new ScanCommand());
        commandList.put(SupoSECommands.SCHEDULE, new ScheduleCommand());
        commandList.put(SupoSECommands.SEARCH, new SearchCommand());

        mainCommand = new MainCommand();
        commander = new JCommander(mainCommand);

        for (SupoSECommands item : SupoSECommands.values()) {
            getCommander().addCommand(item.getCommandName(), commandList.get(item));
        }

        getCommander().setProgramName(PROGRAMM_NAME);
        getCommander().parse(args);
    }

    /**
     * Check to see if for one command a help option is given or not.
     *
     * @return true if a command is given with help option false otherwise.
     */
    public boolean isHelpForCommand() {
        boolean result = false;
        SupoSECommands command = getCommand();
        ICommand baseCommand = commandList.get(command);
        if (baseCommand == null) {
            result = false;
        } else {
            result = baseCommand.isHelp();
        }
        return result;
    }

    public SupoSECommands getCommand() {
        SupoSECommands command = null;
        for (SupoSECommands item : SupoSECommands.values()) {
            if (item.getCommandName().equalsIgnoreCase(getCommander().getParsedCommand())) {
                command = item;
            }
        }
        return command;
    }

    public JCommander getCommander() {
        return this.commander;
    }

    public MainCommand getMainCommand() {
        return this.mainCommand;
    }

    public MergeCommand getMergeCommand() {
        return (MergeCommand) commandList.get(SupoSECommands.MERGE);
    }
    public ScanCommand getScanCommand() {
        return (ScanCommand) commandList.get(SupoSECommands.SCAN);
    }
    public ScheduleCommand getScheduleCommand() {
        return (ScheduleCommand) commandList.get(SupoSECommands.SCHEDULE);
    }
    public SearchCommand getSearchCommand() {
        return (SearchCommand) commandList.get(SupoSECommands.SEARCH);
    }
}
