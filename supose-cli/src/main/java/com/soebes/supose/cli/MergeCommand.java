/**
 * The (Su)bversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007-2016 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (c) 2007-2016 by Karl Heinz Marbaise
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

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;
import com.beust.jcommander.converters.FileConverter;

/**
 * The merge command for command line.
 *
 * @author Karl Heinz Marbaise
 */
@Parameters(commandDescription = "Merge multiple indexes into a single index.", separators = "=")
public class MergeCommand extends BaseCommand implements ICommand {

    @Parameter(names = { "--destination",
            "-D" }, description = "The destination index.", converter = FileConverter.class)
    private File destinationIndex;

    @Parameter(names = { "--index",
            "-I" }, description = "The indexes you would like to merge into destination.", converter = FileConverter.class)
    private List<File> indexes = new ArrayList<File>();

    public File getDestinationIndex() {
        return destinationIndex;
    }

    public List<File> getIndexes() {
        return indexes;
    }

}
