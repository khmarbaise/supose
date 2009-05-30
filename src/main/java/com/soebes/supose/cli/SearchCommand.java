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

import java.util.List;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.option.Command;

/**
 * @author Karl Heinz Marbaise
 *
 */
public class SearchCommand extends CLIBase {

    private Option optionIndex = null;
    private Option optionQuery = null;
    private Option optionFields = null;

	public SearchCommand() {
		setCommand(createCommand());
	}

	private Command createCommand() {
		/*
		 * 
		 * suposecli search --index indexDirectory "Query"
		 */

    	optionIndex = obuilder
			.withShortName("I")
			.withLongName("index")
			.withArgument(abuilder.withName("index").create())
			.withDescription("Define the index directory where to find the index.")
			.create();

    	optionQuery = obuilder
			.withShortName("Q")
			.withLongName("query")
			.withArgument(abuilder.withName("query").create())
			.withDescription("Define the query which will be executed.")
			.create();
    	optionFields = obuilder
	    	.withShortName("F")
	    	.withLongName("fields")
	    	.withArgument(abuilder.withName("fields").create())
	    	.withDescription("Define the fields which will be shown on the result set.")
	    	.create();
    	
    	Group optionUpdate = gbuilder
    		.withOption(optionIndex)
    		.withOption(optionQuery)
    		.withOption(optionFields)
    		.create();
    	
    	return cbuilder
	    	.withName("search")
	    	.withName("se")
	    	.withDescription("Search within index with particular query.")
	    	.withChildren(optionUpdate)
	    	.create();
	}

	public Option getOptionIndex() {
		return optionIndex;
	}
	public Option getOptionQuery() {
		return optionQuery;
	}
	public Option getOptionFields() {
		return optionFields;
	}

	@SuppressWarnings("unchecked")
	public String getIndexDir (CommandLine cline) {
		List<String> list = cline.getValues((getOptionIndex()));
		if (list == null || list.size() == 0) {
			return "indexDir.test";
		} else {
			return list.get(0);
		}
	}

	@SuppressWarnings("unchecked")
	public String getQuery (CommandLine cline) {
		List<String> list = cline.getValues((getOptionQuery()));

		String result = "";
		if (list == null || list.size() == 0) {
			return result;
		}
		for(int i=0; i<list.size(); i++) {
			result += " " + list.get(i);
		}
		return result;
	}
	
	@SuppressWarnings("unchecked")
	public List<String> getFields(CommandLine cline) {
		return cline.getValues(getOptionFields());
	}

}
