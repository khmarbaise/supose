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
package com.soebes.supose.recognition;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.soebes.supose.TestBase;
import com.soebes.supose.repository.Repository;

public class ScanReposTest extends TestBase {
	private static Logger LOGGER = Logger.getLogger(ScanReposTest.class);

	private static final String TAGS = "/tags/";

	private Repository repository = null;

	@BeforeTest
	public void beforeTest() throws SVNException {
		//For the test repositories we don't use authentication, cause
		//we are working with file based repositories.
		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(
			null, 
			null
		);
		String repositoryDir = getRepositoryDirectory();
		SVNURL url = SVNURL.fromFile(new File(repositoryDir));
		repository = new Repository("file://" + url.getURIEncodedPath(), authManager);
	}
	

	@Test
	public void analyzeTestFirstTag() throws SVNException {
		ArrayList<TagType> result = analyzeLog(repository);
	    assertEquals(result.size(), 3);
	    assertEquals(result.get(0).getType(), TagType.Type.TAG);
	    assertEquals(result.get(0).getName(), "/project1/tags/RELEASE-0.0.1");
	    assertEquals(result.get(0).getCopyFromRevision(), 2);
	    assertEquals(result.get(0).getRevision(), 3);
	    
	    assertEquals(result.get(1).getType(), TagType.Type.TAG);
	    assertEquals(result.get(1).getName(), "/project1/tags/supose-0.0.2");
	    assertEquals(result.get(1).getRevision(), 6);

	    assertEquals(result.get(2).getType(), TagType.Type.BRANCH);
	    assertEquals(result.get(2).getName(), "/project1/branches/B_0.0.2");
	    assertEquals(result.get(2).getCopyFromRevision(), 7);
	    assertEquals(result.get(2).getRevision(), 8);
	    
	}

	private ArrayList<TagType> analyzeLog(Repository repository) throws SVNException {
		ArrayList<TagType> result = new ArrayList<TagType>();
		Collection logEntries = null;
        logEntries = repository.getRepository().log(new String[] {""}, null, 1, -1, true, true);
        for (Iterator iterator = logEntries.iterator(); iterator.hasNext();) {
			SVNLogEntry logEntry = (SVNLogEntry) iterator.next();
			if (logEntry.getChangedPaths().size() > 0) {
				Set changedPathsSet = logEntry.getChangedPaths().keySet();

				if (changedPathsSet.size() == 1) {
					//Here we change if we usual tags/branches
					checkForTagOrBranch(result, logEntry, changedPathsSet);
				} else {
					//Particular situations like Maven Tags.
					checkForMavenTag(result, logEntry, changedPathsSet);
				}
			}

        }
        return result;
	}

	private void checkForMavenTag(
			ArrayList<TagType> result,
			SVNLogEntry logEntry, 
			Set changedPathsSet 
		) {
		//The log message is the first indication for a maven tag...
//FIXME: The hard coded value for the message of Maven must be made configurable...		
		if (!logEntry.getMessage().startsWith("[maven-release-plugin]  copy for tag ")) {
			return;
		}

		//The first assumption the log message is correct...
		for (Iterator changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();) {
			SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());
			if (entryPath.getType() == SVNLogEntryPath.TYPE_ADDED) {
				if (entryPath.getCopyPath() != null) {
					SVNDirEntry destEntry = getInformationAboutEntry(logEntry.getRevision(), entryPath.getPath());
					SVNDirEntry sourceEntry = getInformationAboutEntry(logEntry.getRevision(), entryPath.getCopyPath());
					
					TagType bt = new TagType();
					bt.setName(entryPath.getPath());
					bt.setRevision(logEntry.getRevision());
					bt.setCopyFromRevision(entryPath.getCopyRevision());

					//Source and destination of the copy operation must be a directory
					if (	destEntry.getKind() == SVNNodeKind.DIR
						&&	sourceEntry.getKind() == SVNNodeKind.DIR) {

						//If we the /tags/ part this is assumed to be a Tag.
						if (entryPath.getPath().contains(TAGS)) {
							bt.setType(TagType.Type.TAG);
							result.add(bt);
						}
					}
				}
			}
		}
	}

	
	private SVNDirEntry getInformationAboutEntry(long revision, String path) {
		SVNDirEntry dirEntry = null;
		try {
			LOGGER.debug("getInformationAboutEntry() name:" + path + " rev:" + revision);
			dirEntry = repository.getRepository().info(path, revision);
		} catch (SVNException e) {
			LOGGER.error("Unexpected Exception: " + e);
		}
		return dirEntry;
	}

	private void checkForTagOrBranch(
		ArrayList<TagType> result, 
		SVNLogEntry logEntry, 
		Set changedPathsSet
		) {

		Iterator changedPaths = changedPathsSet.iterator();
		SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());

		//a copy-to has happened so we can have a branch or a tag?
		if (entryPath.getCopyPath() != null) {
			TagType bt = new TagType();
			bt.setName(entryPath.getPath());
			bt.setRevision(logEntry.getRevision());
			bt.setCopyFromRevision(entryPath.getCopyRevision());
//FIXME: the hard coded value "/tags/" must be made configurably.				
			if (entryPath.getPath().contains(TAGS)) {
				bt.setType(TagType.Type.TAG);
			} else {
				bt.setType(TagType.Type.BRANCH);
			}
			result.add(bt);
		}
	}
}
