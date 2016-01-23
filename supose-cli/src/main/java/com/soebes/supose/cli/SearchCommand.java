/**
 * The (Su)bversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007-2011 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (c) 2007-2011 by Karl Heinz Marbaise
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 *
 * The License can viewed online under http://www.gnu.org/licenses/gpl.html If
 * you have any questions about the Software or about the license just write an
 * email to license@soebes.de
 */

package com.soebes.supose.cli;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.soebes.supose.core.FieldNames;

/**
 * The search command for command line.
 *
 * @author Karl Heinz Marbaise
 */
@Parameters(separators = "=", commandDescription = "Query the index of scanned repositories to get answers.")
public class SearchCommand extends BaseCommand implements ICommand {

    @Parameter(names = { "--index", "-I" }, description = "Define the name of the index folder.")
    private final String indexName;

    @Parameter(names = { "--field",
            "-F" }, description = "Give the name of the fields which should be printed out.", converter = FieldNamesConverter.class)
    private final List<FieldNames> fields = new ArrayList<FieldNames>();

    @Parameter(names = { "--query",
            "-Q" }, description = "Define the query you would like to use for searching in the index.")
    private String query;

    @Parameter(names = { "--xml" }, description = "print out results as XML")
    private boolean xml;

    /**
     * This will define the defaults for the different command line options.
     */
    public SearchCommand() {
        this.indexName = "indexDir.test";
    }

    public String getIndexName() {
        return indexName;
    }

    public List<FieldNames> getFields() {
        return fields;
    }

    public String getQuery() {
        return query;
    }

    public boolean isXML() {
        return xml;
    }

}
