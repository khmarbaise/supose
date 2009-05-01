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

import com.soebes.supose.recognition.TagType;
import com.soebes.supose.repository.Repository;

public class TagBranchRecognition {
	private static Logger LOGGER = Logger.getLogger(TagBranchRecognition.class);

	public static final String TAGS = "/tags/";

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
	
	public void checkForMavenTag(
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
						if (entryPath.getPath().contains(TagBranchRecognition.TAGS)) {
							bt.setType(TagType.Type.TAG);
							bt.setMavenTag(true);
							result.add(bt);
						}
					}
				}
			}
		}
	}

	public void checkForTagOrBranch(
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
			if (entryPath.getPath().contains(TagBranchRecognition.TAGS)) {
				bt.setType(TagType.Type.TAG);
			} else {
				bt.setType(TagType.Type.BRANCH);
			}
			result.add(bt);
		}
	}
	
	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	
}
