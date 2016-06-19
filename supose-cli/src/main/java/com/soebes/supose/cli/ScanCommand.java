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

import java.net.URI;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.Parameters;

/**
 * The scan command for command line.
 *
 * @author Karl Heinz Marbaise
 */
@Parameters(separators = "=", commandDescription = "Scan the contents of a repository")
public class ScanCommand extends BaseCommand implements ICommand {

    @Parameter(names = { "--index", "-I" }, description = "Define the name of the index folder.")
    private String indexName;
    @Parameter(names = { "--create",
            "-c" }, description = "Usually an index will be updated except you have given this option which will result in creating a new index.")
    private boolean createIndex;
    @Parameter(names = { "--username",
            "-u" }, description = "The username which is used to authenticate against the repository")
    private String username;
    @Parameter(names = { "--password",
            "-p" }, description = "The password which is used to authenticate against the repository.")
    private String password;
    @Parameter(names = { "--url",
            "-U" }, description = "Define the URL of the repository which should be scanned.", converter = URIConverter.class)
    private URI uri;
    @Parameter(names = {
            "--fromrev" }, description = "The revision where to start the scanning process", converter = RevisionConverter.class)
    private Long fromRev;
    @Parameter(names = {
            "--torev" }, description = "The revision to which the scanning process will run.", converter = RevisionConverter.class)
    private Long toRev;

    /**
     * This will define the defaults for the different command line options.
     */
    public ScanCommand() {
        this.fromRev = new Long(1);
        this.toRev = new Long(-1);
        this.indexName = "indexDir.test";
        this.createIndex = false;
        this.uri = null;
        this.username = null;
        this.password = null;
    }

    public long getFromRev() {
        return fromRev;
    }

    public long getToRev() {
        return toRev;
    }

    public String getUsername() {
        return username;
    }

    public String getPassword() {
        return password;
    }

    public String getIndexName() {
        return indexName;
    }

    public boolean isCreateIndex() {
        return createIndex;
    }

    public URI getUri() {
        return uri;
    }
}
