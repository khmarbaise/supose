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

import java.io.IOException;
import java.util.Collection;
import java.util.Date;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;

import com.soebes.supose.FieldNames;
import com.soebes.supose.repository.Repository;
import com.soebes.supose.search.NumberUtils;
import com.soebes.supose.utility.FileName;

/**
 * @author Karl Heinz Marbaise
 *
 */
public class ScanRepository {
	private static Logger LOGGER = Logger.getLogger(ScanRepository.class);

	private String name;
	
	/**
	 * This defines the revision from where we start to scan the given repository.
	 */
	private long startRevision;
	/**
	 * This defines the revision to which we will scan the given repository. 
	 */
	private long endRevision;
	
	private Repository repository = null;

	public ScanRepository() {
		setStartRevision(0);
		setEndRevision(0);
		setRepository(null);
		setName("");
	}

	/**
	 * This method will do the real scanning of the whole repository.
	 * It will extract all log entries as first step and go on with
	 * scanning every change set.
	 * @param writer The index where the result of the scanning
	 *   will be written to.
	 */
	@SuppressWarnings("unchecked")
	public void scan(IndexWriter writer) {

       LOGGER.debug("Repositories latest Revision: " + endRevision);
        Collection<SVNLogEntry> logEntries = null;
        try {
            logEntries = repository.getRepository().log(new String[] {""}, null, startRevision, endRevision, true, true);
        } catch (SVNException svne) {
            LOGGER.error("error while collecting log information for '"
                    + repository.getUrl() + "': " + svne);
            return;
        }

        LOGGER.debug("We have " + logEntries.size() + " change sets to scan.");
        for (Iterator entries = logEntries.iterator(); entries.hasNext();) {
            SVNLogEntry logEntry = (SVNLogEntry) entries.next();

            if (LOGGER.isDebugEnabled()) {
	            LOGGER.debug("---------------------------------------------");
	            LOGGER.debug("revision: " + logEntry.getRevision());
	            LOGGER.debug("author: " + logEntry.getAuthor());
	            LOGGER.debug("date: " + logEntry.getDate());
	            LOGGER.debug("log message: " + logEntry.getMessage());
            }

            if (logEntry.getChangedPaths().size() > 0) {
            	LOGGER.debug("changed paths:");
				try {
					workOnChangeSet(writer, repository, logEntry);
				} catch (Exception e) {
	            	LOGGER.error("Error during workOnChangeSet() " + e);
				}                
            } else {
            	LOGGER.debug("No changed paths found!");
            }
        }
		repository.close();
	}


	/**
	 * Here we have a single ChangeSet which will be analyzed separate.
	 * @param indexWriter 
	 * @param repository
	 * @param logEntry
	 */
	private void workOnChangeSet(IndexWriter indexWriter, Repository repository, SVNLogEntry logEntry) {
		Set changedPathsSet = logEntry.getChangedPaths().keySet();

		int count = 0;
		LOGGER.info("Number of files for revision: " + changedPathsSet.size());
		for (Iterator changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();) {
			count ++;

			SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());
			LOGGER.debug(" "
		            + entryPath.getType()
		            + "	"
		            + entryPath.getPath()
		            + ((entryPath.getCopyPath() != null) ? " (from "
		                    + entryPath.getCopyPath() + " revision "
		                    + entryPath.getCopyRevision() + ")" : ""));

			//We would like to know something about the entry.
			SVNDirEntry dirEntry = getInformationAboutEntry(repository, logEntry, entryPath);
		    
			try {
				if (SVNLogEntryPath.TYPE_ADDED == entryPath.getType()) {
					LOGGER.debug("File " + entryPath.getPath() + " added...");
					//get All file content and index it.
					indexFile(indexWriter, dirEntry, repository, logEntry, entryPath);
				}
				if (SVNLogEntryPath.TYPE_MODIFIED == entryPath.getType()) {
					//Get all file content and index it...
					LOGGER.debug("Modified file...");
					//get All file content and index it.
					indexFile(indexWriter, dirEntry, repository, logEntry, entryPath);
				}
				if (SVNLogEntryPath.TYPE_REPLACED == entryPath.getType()) {
					//Get all file content and index it...
					LOGGER.debug("Replaced file...");
					//get All file content and index it.
					indexFile(indexWriter, dirEntry, repository, logEntry, entryPath);
				}
				if (SVNLogEntryPath.TYPE_DELETED == entryPath.getType()) {
					LOGGER.debug("The file '" + entryPath.getPath() + "' has been deleted.");
					indexFile(indexWriter, dirEntry, repository, logEntry, entryPath);
				}
			} catch (IOException e) {
				LOGGER.error("IOExcepiton: " + e);
			} catch (SVNException e) {
				LOGGER.error("SVNExcepiton: " + e);
			} catch (Exception e) {
				LOGGER.error("something wrong: " + e);
			}
		}
	}

	protected void addTokenizedField(Document doc, String fieldName, String value) {
		doc.add(new Field(fieldName,  value, Field.Store.YES, Field.Index.ANALYZED));
	}
	private void addUnTokenizedField(Document doc, String fieldName, String value) {
		doc.add(new Field(fieldName,  value, Field.Store.YES, Field.Index.NOT_ANALYZED));
	}
	private void addUnTokenizedField(Document doc, String fieldName, Long value) {
		doc.add(new Field(fieldName,  value.toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
	}
	private void addUnTokenizedField(Document doc, String fieldName, Date value) {
		doc.add(new Field(fieldName,  value.toGMTString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
	}
	private void addUnTokenizedField(Document doc, String fieldName, Character value) {
		doc.add(new Field(fieldName,  value.toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
	}

	private void indexFile(IndexWriter indexWriter, SVNDirEntry dirEntry, Repository repository, SVNLogEntry logEntry, SVNLogEntryPath entryPath) 
		throws SVNException, IOException {
			SVNProperties fileProperties = new SVNProperties();

			SVNNodeKind nodeKind = repository.getRepository().checkPath(entryPath.getPath(), logEntry.getRevision());

			Document doc = new Document();
			addUnTokenizedField(doc, FieldNames.REVISION, NumberUtils.pad(logEntry.getRevision()));

			boolean isDir = nodeKind == SVNNodeKind.DIR;
			boolean isFile = nodeKind == SVNNodeKind.FILE;
			FileName fileName = new FileName(entryPath.getPath(), isDir);
			LOGGER.info("FileName: '" + entryPath.getPath() + "'");
			addUnTokenizedField(doc, FieldNames.PATH, fileName.getPath());

			if (isDir) {
				LOGGER.debug("The " + entryPath.getPath() + " is a directory entry.");
				addUnTokenizedField(doc, FieldNames.NODE, "dir");
			} else if (isFile) {
				LOGGER.debug("The " + entryPath.getPath() + " is a file entry.");
				addUnTokenizedField(doc, FieldNames.NODE, "file");
			} else {
				//This means a file/directory has been deleted.
				addUnTokenizedField(doc, FieldNames.NODE, "unknown");
				LOGGER.debug("The " + entryPath.getPath() + " is an unknown entry.");
			}

			//Does a copy operation took place...
			if (entryPath.getCopyPath() != null) {
				addUnTokenizedField(doc, FieldNames.FROM, entryPath.getCopyPath());
				addUnTokenizedField(doc, FieldNames.FROMREV, entryPath.getCopyRevision());
			}

			addUnTokenizedField(doc, FieldNames.FILENAME, entryPath.getPath());
			addUnTokenizedField(doc, FieldNames.AUTHOR, logEntry.getAuthor() == null ? "" : logEntry.getAuthor());

			//We will add the message as tokenized field to be able to search within the log messages.
			addTokenizedField(doc, FieldNames.MESSAGE, logEntry.getMessage() == null ? "" : logEntry.getMessage());
			addUnTokenizedField(doc, FieldNames.DATE, logEntry.getDate());

			addUnTokenizedField(doc, FieldNames.KIND, entryPath.getType());

//TODO: May be don't need this if we use repository name?
			addUnTokenizedField(doc, FieldNames.REPOSITORYUUID, repository.getRepository().getRepositoryUUID(false));
			
			addUnTokenizedField(doc, FieldNames.REPOSITORY, getName());

			if (nodeKind == SVNNodeKind.NONE) {
				LOGGER.debug("The " + entryPath.getPath() + " is a NONE entry.");
			} else if (nodeKind == SVNNodeKind.DIR) {
				//The given entry is a directory.
				LOGGER.debug("The " + entryPath.getPath() + " is a directory.");
				//Here we need to call getDir to get directory properties.
				Collection<SVNDirEntry> dirEntries = null;
				repository.getRepository().getDir(entryPath.getPath(), logEntry.getRevision(), fileProperties, dirEntries);
				indexProperties(fileProperties, doc);

			} else if (nodeKind == SVNNodeKind.FILE) {
				
				//The given entry is a file.
				//This means we will get every file from the repository....
				//Get only the properties of the file
				
				repository.getRepository().getFile(entryPath.getPath(), logEntry.getRevision(), fileProperties, null);
				indexProperties(fileProperties, doc);

				FileExtensionHandler feh = new FileExtensionHandler();
				feh.setFileProperties(fileProperties);
				feh.setDoc(doc);
				feh.execute(repository, dirEntry, entryPath.getPath(), logEntry.getRevision());
			}

			indexWriter.addDocument(doc);
			LOGGER.debug("File " + entryPath.getPath() + " indexed...");
	}


	/**
	 * This method will index only those properties which do not start
	 * with {@link SVN_WC_PREFIX} nor with {@link SVN_ENTRY_PREFIX}.
	 * @param fileProperties
	 * @param doc
	 */
	private void indexProperties(SVNProperties fileProperties, Document doc) {
		SVNProperties list = fileProperties.getRegularProperties();

		for (Iterator<String> iterator = list.nameSet().iterator(); iterator.hasNext();) {
			String propname = (String) iterator.next();
			LOGGER.debug("Indexing property: " + propname); 
			addUnTokenizedField(doc, propname, list.getStringValue(propname));
		}
	}

	private SVNDirEntry getInformationAboutEntry(Repository repository, SVNLogEntry logEntry, SVNLogEntryPath entryPath) {
		SVNDirEntry dirEntry = null;
		try {
			LOGGER.debug("getInformationAboutEntry() name:" + entryPath.getPath() + " rev:" + logEntry.getRevision());
			dirEntry = repository.getRepository().info(entryPath.getPath(), logEntry.getRevision());
		} catch (SVNException e) {
			LOGGER.error("Unexpected Exception: " + e);
		}
		return dirEntry;
	}

	public long getStartRevision() {
		return startRevision;
	}

	public void setStartRevision(long startRevision) {
		this.startRevision = startRevision;
	}

	public long getEndRevision() {
		return endRevision;
	}

	public void setEndRevision(long endRevision) {
		this.endRevision = endRevision;
	}

	public Repository getRepository() {
		return repository;
	}

	public void setRepository(Repository repository) {
		this.repository = repository;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
