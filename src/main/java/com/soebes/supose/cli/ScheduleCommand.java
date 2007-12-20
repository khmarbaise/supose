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
package com.soebes.supose.cli;

import java.util.List;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.option.Command;

/**
 * @author Karl Heinz Marbaise
 *
 *
 * supose schedule --configuration repository.ini
 */
public class ScheduleCommand extends CLIBase {

    private Option optionConfiguration = null;
    private Option optionConfigurationBaseDir = null;

	public ScheduleCommand() {
		setCommand(createCommand());
	}

	private Command createCommand() {
    	optionConfiguration = obuilder
			.withShortName("C")
			.withLongName("configuration")
			.withRequired(true)
			.withArgument(abuilder.withName("configuration").create())
			.withDescription("Define where to find the ini file with the information about the different repositories.")
			.create();

    	optionConfigurationBaseDir = obuilder
			.withShortName("D")
			.withLongName("configbase")
			.withRequired(true)
			.withArgument(abuilder.withName("configbase").create())
			.withDescription("Define where to put the created index files etc.")
			.create();

    	Group scanOptionIndex = gbuilder
    		.withOption(optionConfiguration)
    		.withOption(optionConfigurationBaseDir)
    		.create();
    	
    	return cbuilder
	    	.withName("schedule")
	    	.withDescription("Schedule scanning for later running")
	    	.withChildren(scanOptionIndex)
	    	.create();
	}

	public Option getOptionConfiguration() {
		return optionConfiguration;
	}

	public Option getOptionConfigBaseDir() {
		return optionConfigurationBaseDir;
	}

	@SuppressWarnings("unchecked")
	public String getConfiguration (CommandLine cline) {
		List<String> list = cline.getValues((getOptionConfiguration()));
		if (list == null || list.size() == 0) {
			//This should never happen, cause the option is required.
			return "Default";
		} else {
			return list.get(0);
		}
	}

	@SuppressWarnings("unchecked")
	public String getConfBaseDir (CommandLine cline) {
		List<String> list = cline.getValues((getOptionConfigBaseDir()));
		if (list == null || list.size() == 0) {
			return "basedir";
		} else {
			return list.get(0);
		}
	}

}
