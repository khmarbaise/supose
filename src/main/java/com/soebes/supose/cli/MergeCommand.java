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

/**
 * @author Karl Heinz Marbaise
 *
 *
 * supose merge --index anton berta egon --destination result
 */
public class MergeCommand extends CLIBase {

    private Option optionDestination = null;
    private Option optionIndex = null;

	public MergeCommand() {
		setCommand(createCommand());
	}

	private Command createCommand() {
    	optionDestination = obuilder
	    	.withLongName("destination")
	    	.withRequired(true)
	    	.withArgument(abuilder.withName("destination").create())
	    	.withDescription("Define the destination directory of the merged index.")
	    	.create();

    	optionIndex = obuilder
			.withShortName("I")
			.withLongName("index")
			.withArgument(abuilder.withName("index").create())
			.withDescription("Define the index directory where the created index will be stored.")
			.create();

    	Group scanOptionIndex = gbuilder
    		.withOption(optionIndex)
    		.withOption(optionDestination)
    		.create();
    	
    	return cbuilder
	    	.withName("merge")
	    	.withName("mg")
	    	.withDescription("Merge existing lucene index together.")
	    	.withChildren(scanOptionIndex)
	    	.create();
	}

	public Option getOptionIndex() {
		return optionIndex;
	}
	public Option getOptionDestination() {
		return optionDestination;
	}

	@SuppressWarnings("unchecked")
	public List<String> getIndex (CommandLine cline) {
		List<String> list = cline.getValues((getOptionIndex()));
		return list;
	}
	
	@SuppressWarnings("unchecked")
	public String getDestination (CommandLine cline) {
		List<String> list = cline.getValues((getOptionDestination()));
		if (list == null || list.size() == 0) {
			return "destination.Dir";
		} else {
			return list.get(0);
		}
	}

}
