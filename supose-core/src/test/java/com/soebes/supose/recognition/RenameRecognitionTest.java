/**
 * The (Su)bversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007, 2008, 2009, 2010 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (c) 2007, 2008, 2009, 2010 by Karl Heinz Marbaise
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
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.soebes.supose.TestBase;
import com.soebes.supose.repository.Repository;

public class RenameRecognitionTest extends TestBase {
	private static Logger LOGGER = Logger.getLogger(RenameRecognitionTest.class);

	private Repository repository = null;
	private RenameRecognition tbr = null;
	
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
		tbr = new RenameRecognition();
		tbr.setRepository(repository);
	}
	

	@Test
	public void analyzeTestFirstTag() throws SVNException {
		ArrayList<RenameType> result = analyzeLog(repository);
	    assertEquals(result.size(), 1);
	    RenameType rt = result.get(0);
	    LOGGER.debug("Renamed: " + rt.getRevision() + " " + rt.getDestinationName() + " (from " + rt.getSourceName() + ":" + rt.getCopyFromRevision() + ")");
	    assertEquals(result.get(0).getSourceName(), "/project1/trunk/f1.txt");
	    assertEquals(result.get(0).getDestinationName(), "/project1/trunk/f3.txt");
	    assertEquals(result.get(0).getCopyFromRevision(), 9);
	    assertEquals(result.get(0).getRevision(), 11);
	}
	
	private ArrayList<RenameType> analyzeLog(Repository repository) throws SVNException {
		ArrayList<RenameType> result = new ArrayList<RenameType>();
		Collection logEntries = repository.getRepository().log(new String[] {""}, null, 1, -1, true, true);
        for (Iterator iterator = logEntries.iterator(); iterator.hasNext();) {
			SVNLogEntry logEntry = (SVNLogEntry) iterator.next();
			//Only if the changeset contains more than one entry we could have a rename operation.
			if (logEntry.getChangedPaths().size() > 1) {
				Set changedPathsSet = logEntry.getChangedPaths().keySet();
				
				ArrayList<RenameType> tmpResult = tbr.checkForRename(logEntry, changedPathsSet);
				if (tmpResult.size() > 0) {
					result.addAll(tmpResult);
				}
			}

        }
        return result;
	}


}
