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
package com.soebes.supose.recognition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;

import com.soebes.supose.repository.Repository;
import com.soebes.supose.scan.RepositoryInformation;

public class RenameRecognition {
	private static Logger LOGGER = Logger.getLogger(RenameRecognition.class);

	private Repository repository = null;

	public ArrayList<RenameType> checkForRename(
			SVNLogEntry logEntry, 
			Set changedPathsSet 
		) {
		HashMap<String, RenameType> possibleRenames = new HashMap<String, RenameType>();
		ArrayList<RenameType> result = new ArrayList<RenameType>();
		
		//The first assumption the log message is correct...
		for (Iterator changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();) {

			SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());

			if (entryPath.getType() == SVNLogEntryPath.TYPE_DELETED) {
				String deletedEntry = entryPath.getPath();
				LOGGER.debug("DELETED " + deletedEntry);
				if (possibleRenames.containsKey(deletedEntry)) {
					LOGGER.debug("Rename recognized " + deletedEntry);
					//Rename recognized if we have found the associated 
					//delete operation.
					result.add(possibleRenames.get(deletedEntry));
				} else {
					LOGGER.debug("New Entry for possibleRenames " + deletedEntry);
					RenameType rt = new RenameType();
					rt.setDestinationName("null");
					rt.setSourceName(deletedEntry);
					rt.setCopyFromRevision(0);
					rt.setRevision(logEntry.getRevision());
					possibleRenames.put(deletedEntry, rt);//Erzeuge Eintrag
				}
			} else if (entryPath.getType() == SVNLogEntryPath.TYPE_ADDED) {
				if (entryPath.getCopyPath() != null) {
					SVNDirEntry destEntry = RepositoryInformation.getInformationAboutEntry(
						getRepository(), 
						logEntry.getRevision(), 
						entryPath.getPath()
					);
					SVNDirEntry sourceEntry = RepositoryInformation.getInformationAboutEntry(
						getRepository(), 
						entryPath.getCopyRevision(), 
						entryPath.getCopyPath()
					);

					//Source and destination of the copy operation must be identical
					//I assume that this always be the case!!!
					if (destEntry.getKind() == sourceEntry.getKind()) {
						String sourceName = entryPath.getCopyPath();
						String destinationName = entryPath.getPath();
						LOGGER.debug("COPY src:" + sourceName + " dest:" + destinationName);
						RenameType rt = new RenameType();
						rt.setDestinationName(destinationName);
						rt.setSourceName(sourceName);
						rt.setCopyFromRevision(entryPath.getCopyRevision());
						rt.setRevision(logEntry.getRevision());
						if (possibleRenames.containsKey(sourceName)) {
							//Rename recognized
							RenameType rt1 = possibleRenames.get(sourceName);
							//Update the information
							rt1.setDestinationName(destinationName);
							rt1.setCopyFromRevision(entryPath.getCopyRevision());
							result.add(rt1);
						} else {
							//create new entry in the list of possible candidates.
							possibleRenames.put(sourceName, rt);
						}
					}
				}
			}
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
