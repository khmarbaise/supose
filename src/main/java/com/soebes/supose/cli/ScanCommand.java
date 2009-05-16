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
package com.soebes.supose.cli;

import java.util.List;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.option.Command;
import org.tmatesoft.svn.core.wc.SVNRevision;

/**
 * @author Karl Heinz Marbaise
 *
 */
public class ScanCommand extends CLIBase {

    private Option optionURL = null;
    private Option optionUsername = null;
    private Option optionPassword = null;
    private Option optionFromRev = null;
    private Option optionToRev = null;
    private Option optionIndexDir = null;
    private Option optionCreate = null;

	public ScanCommand() {
		setCommand(createCommand());
	}

	private Command createCommand() {
    	optionURL = obuilder
    		.withShortName("U")
    		.withLongName("url")
    		.withArgument(
    			abuilder
    			.withName("url")
    			.withMinimum(1)
    			.withMaximum(1)
    			.create()
    		)
    		.withDescription("Define the position where to find the index created by an scan.")
    		.create();
    	
    	optionUsername = obuilder
			.withLongName("username")
			.withArgument(
				abuilder
				.withName("username")
				.withMinimum(1)
				.withMaximum(1)
				.create()
			)
			.withDescription("Define the username which is used to make an authorization against the Subversion repository.")
			.create();

    	optionPassword = obuilder
    		.withShortName("p")
			.withLongName("password")
			.withArgument(
				abuilder
				.withName("password")
				.withMinimum(1)
				.withMaximum(1)
				.create()
			)
			.withDescription("Define the password which is used to make an authorization against the Subversion repository.")
			.create();

    	optionFromRev = obuilder
			.withLongName("fromrev")
			.withArgument(
				abuilder
				.withName("fromrev")
				.withMinimum(1)
				.withMaximum(1)
				.create()
			)
			.withDescription("Define the revision from which we will start to scan the repository.")
			.create();

    	optionToRev = obuilder
	    	.withLongName("torev")
	    	.withArgument(
	    		abuilder
	    		.withName("torev")
				.withMinimum(1)
				.withMaximum(1)
	    		.create()
	    	)
	    	.withDescription("Define the revision to which we will scan the repository. If it's not given we scan til HEAD.")
	    	.create();

    	optionIndexDir = obuilder
			.withShortName("I")
			.withLongName("index")
			.withArgument(
				abuilder
				.withName("index")
				.withMinimum(1)
				.withMaximum(1)
				.create()
			)
			.withDescription("Define the index directory where the created index will be stored.")
			.create();

    	optionCreate = obuilder
	    	.withLongName("create")
	    	.withDescription("If given it means that the index will be create or overwritten if exists. Otherwise it will be used.")
	    	.create();
    	
    	Group scanOptionIndex = gbuilder
    		.withOption(optionURL)
    		.withOption(optionUsername)
    		.withOption(optionPassword)
    		.withOption(optionFromRev)
    		.withOption(optionToRev)
    		.withOption(optionIndexDir)
    		.withOption(optionCreate)
    		.create();
    	
    	return cbuilder
	    	.withName("scan")
	    	.withName("sc")
	    	.withDescription("Scan a given repository for later search.")
	    	.withChildren(scanOptionIndex)
	    	.create();
	}

	public Option getOptionURL() {
		return optionURL;
	}

	public Option getOptionUsername() {
		return optionUsername;
	}

	public Option getOptionPassword() {
		return optionPassword;
	}

	public Option getOptionFromRev() {
		return optionFromRev;
	}

	public Option getOptionToRev() {
		return optionToRev;
	}

	public Option getOptionIndexDir() {
		return optionIndexDir;
	}

	public Option getOptionCreate() {
		return optionCreate;
	}

	public boolean getCreate(CommandLine cline) {
		return cline.hasOption((getOptionCreate()));
	}

	@SuppressWarnings("unchecked")
	public String getURL (CommandLine cline) {
		List<String> list = cline.getValues((getOptionURL()));
		if (list == null || list.size() == 0) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@SuppressWarnings("unchecked")
	public String getUsername (CommandLine cline) {
		List<String> list = cline.getValues((getOptionUsername()));
		if (list == null || list.size() == 0) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@SuppressWarnings("unchecked")
	public String getPassword (CommandLine cline) {
		List<String> list = cline.getValues((getOptionPassword()));
		if (list == null || list.size() == 0) {
			return null;
		} else {
			return list.get(0);
		}
	}

	@SuppressWarnings("unchecked")
	public Long getFromRev (CommandLine cline) {
		List<String> list = cline.getValues((getOptionFromRev()));
		if (list == null || list.size() == 0) {
			//Default value for --fromrev
			return new Long(1);
		} else {
			return Long.parseLong(list.get(0));
		}
	}
	
	@SuppressWarnings("unchecked")
	public Long getToRev (CommandLine cline) {
		List<String> list = cline.getValues((getOptionToRev()));
		if (list == null || list.size() == 0) {
			//Default value for --torev HEAD
			return SVNRevision.HEAD.getNumber();
		} else {
			return Long.parseLong(list.get(0));
		}
	}

	@SuppressWarnings("unchecked")
	public String getIndexDir (CommandLine cline) {
		List<String> list = cline.getValues((getOptionIndexDir()));
		if (list == null || list.size() == 0) {
			return "indexDir.test";
		} else {
			return list.get(0);
		}
	}
	
	
}
