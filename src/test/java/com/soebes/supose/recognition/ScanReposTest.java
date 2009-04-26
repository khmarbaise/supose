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
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc.admin.SVNAdminClient;

import com.soebes.supose.TestBase;
import com.soebes.supose.repository.Repository;

public class ScanReposTest extends TestBase {
	private static Logger LOGGER = Logger.getLogger(ScanReposTest.class);

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
		ArrayList<BranchType> result = analyzeLog(repository);
	    assertEquals(result.size(), 2);
	    assertEquals(result.get(0).getType(), BranchType.Type.TAG);
	    assertEquals(result.get(0).getName(), "/project1/tags/RELEASE-0.0.1");
	    assertEquals(result.get(0).getCopyFromRevision(), 2);
	    assertEquals(result.get(0).getRevision(), 3);
	    
	    assertEquals(result.get(1).getType(), BranchType.Type.BRANCH);
	    assertEquals(result.get(1).getName(), "/project1/branches/B_0.0.2");
	    assertEquals(result.get(1).getCopyFromRevision(), 3);
	    assertEquals(result.get(1).getRevision(), 4);
	    
//	    assertEquals(result.get(2).getType(), BranchType.Type.TAG);
//	    assertEquals(result.get(2).getName(), "/project1/tags/supose-0.0.1");
//	    assertEquals(result.get(2).getCopyFromRevision(), 3);
//	    assertEquals(result.get(2).getRevision(), 7);
	}

	private ArrayList<BranchType> analyzeLog(Repository repository) throws SVNException {
		ArrayList<BranchType> result = new ArrayList<BranchType>();
		Collection logEntries = null;
        logEntries = repository.getRepository().log(new String[] {""}, null, 1, -1, true, true);
        for (Iterator iterator = logEntries.iterator(); iterator.hasNext();) {
			SVNLogEntry logEntry = (SVNLogEntry) iterator.next();
			LOGGER.info("Rev: " + logEntry.getRevision());
			System.out.println("Rev:" + logEntry.getRevision());
			if (logEntry.getChangedPaths().size() > 0) {
				System.out.println();
				System.out.println("changed paths:");
				Set changedPathsSet = logEntry.getChangedPaths().keySet();

				for (Iterator changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();) {
					SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());
					if (changedPathsSet.size() == 1) {
						//Here we change if we usual tags/branches
						checkForTagOrBranch(result, logEntry, changedPathsSet, entryPath);
					} else {
						//Particular situations like Maven Tags.
//						checkForParticularTags(result, logEntry, changedPathsSet);
					}
					System.out.println(" "
							+ " Type:" + entryPath.getType()
							+ " "
							+ entryPath.getPath()
							+ ((entryPath.getCopyPath() != null) ? " (from "
									+ entryPath.getCopyPath() + " revision "
									+ entryPath.getCopyRevision() + ")" : ""));
				}
			}

        }
        return result;
	}

	private void checkForParticularTags(
			ArrayList<BranchType> result,
			SVNLogEntry logEntry, 
			Set changedPathsSet, 
			SVNLogEntryPath entryPath
		) {
		//
		if (logEntry.getMessage().startsWith("[maven-release-plugin]  copy for tag ")) {
			//Might be a Maven Tag...
		}
	}

	private void checkForTagOrBranch(
		ArrayList<BranchType> result, 
		SVNLogEntry logEntry, 
		Set changedPathsSet,
		SVNLogEntryPath entryPath
		) {

		//a copy-to has happened so we can have a branch or a tag?
		if (entryPath.getCopyPath() != null) {
			BranchType bt = new BranchType();
			bt.setName(entryPath.getPath());
			bt.setRevision(logEntry.getRevision());
			bt.setCopyFromRevision(entryPath.getCopyRevision());
//FIXME: the hard coded value "/tags/" must be made configurably.				
			if (entryPath.getPath().contains("/tags/")) {
				bt.setType(BranchType.Type.TAG);
			} else {
				bt.setType(BranchType.Type.BRANCH);
			}
			result.add(bt);
		}
	}
}
