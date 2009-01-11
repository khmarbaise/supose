/**
 * The (S)ubversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
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
// SupoSE
package com.soebes.supose.cli;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.commandline.Parser;
import org.apache.commons.cli2.option.Command;

/**
 * @author Karl Heinz Marbaise
 *
 */
public class SuposeCommandLine extends CLIBase {

    private Group commands = null;
    private Group suposeOptions = null;

    //All global options
    private Option globalOptionH = null;

    //All available commands
    private Command scanCommand = null;
    private Command searchCommand = null;
    private Command mergeCommand = null;
    private Command scheduleCommand = null;

    private SearchCommand cliSearchCommand = null;
    private ScanCommand cliScanCommand = null;
    private MergeCommand cliMergeCommand = null;
    private ScheduleCommand cliScheduleCommand = null;

    private void initGlobalOptions() {
    	globalOptionH = obuilder
	    	.withShortName("H")
	    	.withLongName("help")
	    	.withDescription("Displays usage information for command.")
	    	.create();
    }

    private void initCommands() {
    	scanCommand = cliScanCommand.getCommand();
    	searchCommand = cliSearchCommand.getCommand();
    	mergeCommand = cliMergeCommand.getCommand();
    	scheduleCommand = cliScheduleCommand.getCommand();
    }

    public ScanCommand getScliScanCommand() {
    	return cliScanCommand;
    }

    public SearchCommand getScliSearchCommand() {
    	return cliSearchCommand;
    }

    public MergeCommand getScliMergeCommand() {
    	return cliMergeCommand;
    }
    
    public ScheduleCommand getScliScheduleCommand() {
    	return cliScheduleCommand;
    }

    public void init() {
	    initCommands();
		initGlobalOptions();
	
		commands = gbuilder
			.withName("commands")
				.withOption(scanCommand)
				.withOption(searchCommand)
				.withOption(mergeCommand)
				.withOption(scheduleCommand)
	            .create();
	
	    suposeOptions = gbuilder
	        .withName("supose-global options")
	            .withOption(globalOptionH)
	            .withOption(commands)
	            .create();
	
	}

	public SuposeCommandLine () {
		cliSearchCommand = new SearchCommand();
		cliScanCommand = new ScanCommand();
		cliMergeCommand = new MergeCommand();
		cliScheduleCommand = new ScheduleCommand();
		init();
	}

	public CommandLine doParseArgs(String[] args) throws OptionException {
		final Parser parser = new Parser();
		parser.setGroup(suposeOptions);
		return parser.parse(args);
	}

	public Command getScanCommand() {
		return scanCommand;
	}

	public Command getSearchCommand() {
		return searchCommand;
	}

	public Command getMergeCommand() {
		return mergeCommand;
	}
	
	public Command getScheduleCommand() {
		return scheduleCommand;
	}

	public Option getGlobalOptionH() {
		return globalOptionH;
	}

	public void setGlobalOptionH(Option globalOptionH) {
		this.globalOptionH = globalOptionH;
	}

	public Group getSuposeOptions() {
		return suposeOptions;
	}
}
