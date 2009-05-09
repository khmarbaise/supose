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
package com.soebes.supose.scan;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;

import com.soebes.supose.repository.Repository;

/**
 * This class is intended to analyze if a tag or branch is on hand.
 * 
 * @author Karl Heinz Marbaise
 *
 */
public class TagBranchRecognition {
	private static Logger LOGGER = Logger.getLogger(TagBranchRecognition.class);

	public static final String TAGS = "/tags/";
	public static final String MAVEN_TAG_PREFIX = "[maven-release-plugin]  copy for tag ";

	private Repository repository = null;

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
	
	/**
	 * A <a href="http://maven.apache.org">Maven</a> tag is currently 
	 * based on a <a href="http://svnbook.red-bean.com/en/1.5/svn-book.html#svn.branchmerge.tags.mkcomplex">complex tag</a> 
	 * in Subversion.
	 * @param result
	 * @param logEntry
	 * @param changedPathsSet
	 * @return Will return the TagType or null if no appropriate Type (Maven Tag) has been found.
	 */
	public TagType checkForMavenTag(
			SVNLogEntry logEntry, 
			Set changedPathsSet 
		) {
		TagType result = null;
		//The log message is the first indication for a maven tag...
//FIXME: The hard coded value for the message of Maven must be made configurable...		
		if (!logEntry.getMessage().startsWith(MAVEN_TAG_PREFIX)) {
			return result;
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
						if (entryPath.getPath().contains(TagBranchRecognition.TAGS)) {
							bt.setType(TagType.Type.TAG);
							bt.setMavenTag(true);
							result = bt;
						}
					}
				}
			}
		}
		return result;
	}

	/**
	 * This method will check entries within a ChangeSet to check 
	 * if a Tag/Branch has been created.
	 * The first indication is the copy operation which has been done
	 * and the ChangeSet does contain only a single
	 * (<a href="http://www.supose.org/issues/show/106">Issue 106</a>).
	 *   
	 * @param result
	 * @param logEntry
	 * @param changedPathsSet
	 */
	public TagType checkForTagOrBranch(
		SVNLogEntry logEntry, 
		Set changedPathsSet
		) {

		TagType result = null;
		Iterator changedPaths = changedPathsSet.iterator();
		SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());

		//a copy-to has happened so we can have a branch or a tag?
		if (entryPath.getCopyPath() != null) {
			TagType bt = new TagType();
			bt.setName(entryPath.getPath());
			bt.setRevision(logEntry.getRevision());
			bt.setCopyFromRevision(entryPath.getCopyRevision());
//FIXME: the hard coded value "/tags/" must be made configurably.				
			if (entryPath.getPath().contains(TagBranchRecognition.TAGS)) {
				bt.setType(TagType.Type.TAG);
			} else {
				bt.setType(TagType.Type.BRANCH);
			}
			result = bt;
		}
		return result;
	}
	
	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	
}
