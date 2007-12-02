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

import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.Option;
import org.apache.commons.cli2.option.Command;

/**
 * @author Karl Heinz Marbaise
 *
 */
public class SearchCommand extends CLIBase {

    private Option optionIndex = null;

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
    		.withDescription("Define the position where to find the index created by an scan.")
    		.create();
    	
    	Group optionUpdate = gbuilder
    		.withOption(optionIndex)
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

	public void setOptionIndex(Option optionIndex) {
		this.optionIndex = optionIndex;
	}

}
