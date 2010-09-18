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

package com.soebes.supose.scan;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexWriter;
import org.tmatesoft.svn.core.ISVNLogEntryHandler;
import org.tmatesoft.svn.core.SVNAuthenticationException;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;
import org.tmatesoft.svn.core.SVNProperties;

import com.soebes.supose.FieldNames;
import com.soebes.supose.config.filter.Filtering;
import com.soebes.supose.recognition.TagBranch;
import com.soebes.supose.recognition.TagBranchRecognition;
import com.soebes.supose.repository.Repository;
import com.soebes.supose.search.NumberUtils;
import com.soebes.supose.utility.FileName;

/**
 * This class will handle the whole scan of the whole or partials
 * of a repository.
 * It will scan the log entries and will than index the documents afterwards.
 * 
 * @author Karl Heinz Marbaise
 *
 */
public class ScanRepository extends ScanRepositoryBase {

	private static Logger LOGGER = Logger.getLogger(ScanRepository.class);

	private boolean abbort;

	private String name;
	
	private ArrayList<SVNLogEntry> logEntries = null;

	private Filtering filtering = null;

	public void setLogEntries(ArrayList<SVNLogEntry> logEntries) {
		this.logEntries = logEntries;
	}

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
		super();
		setStartRevision(0);
		setEndRevision(0);
		setRepository(null);
		setName("");
		setAbbort(false);
		logEntries = new ArrayList<SVNLogEntry>();
	}

	/**
	 * This method will do the real scanning of the whole repository.
	 * It will extract all log entries as first step and go on with
	 * scanning every change set.
	 * @param writer The index where the result of the scanning
	 *   will be written to.
	 * @throws SVNException 
	 */
	public void scan(IndexWriter writer) throws SVNException {

		LOGGER.debug("Repositories latest Revision: " + endRevision);
        readLogEntries();

        LOGGER.debug("We have " + logEntries.size() + " change sets to scan.");
        scanStart(logEntries.size());
        long count = 1;
        for (Iterator<?> entries = logEntries.iterator(); entries.hasNext();) {
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
					scanBeginRevision(count, logEntry.getRevision(), logEntry.getChangedPaths().size());
					workOnChangeSet(writer, logEntry);
				} catch (Exception e) {
	            	LOGGER.error("Error during workOnChangeSet() ", e);
				} finally {
					scanEndRevision(count, logEntry.getRevision(), logEntry.getChangedPaths().size());
					count++;
				}
            } else {
            	LOGGER.warn("Empty ChangeSet found in revision: " + logEntry.getRevision());
            }
            if (isAbbort()) {
            	LOGGER.warn("We have received an abort signal!");
            	break;
            }
        }
        scanStop();
		getRepository().close();
	}

	/**
	 * This method will read all entries from the repository
	 * and store the log entries into internal array list.
	 * 
	 * @throws SVNAuthenticationException
	 * @throws SVNException
	 */
	private void readLogEntries() 
		throws SVNAuthenticationException, SVNException {
		try {
        	LogEntryStart();
            getRepository().getRepository().log(new String[] {""}, startRevision, endRevision, true, true, new ISVNLogEntryHandler() {
                public void handleLogEntry(SVNLogEntry logEntry) {
                	logEntries.add(logEntry);
                	LogEntry(logEntry);
                }
            });
        } catch (SVNAuthenticationException svnae) {
            LOGGER.error("Authentication has failed. '" + getRepository().getUrl() + "'", svnae);
            throw svnae;
        } catch (SVNException svne) {
            LOGGER.error("error while collecting log information for '"
                    + getRepository().getUrl() + "'", svne);
            throw svne;
        } finally {
        	LogEntryStop();
        }
	}


	/**
	 * Here we have a single ChangeSet which will be analyzed separate.
	 * @param indexWriter 
	 * @param logEntry
	 */
	private void workOnChangeSet(IndexWriter indexWriter, SVNLogEntry logEntry) {
		Set<?> changedPathsSet = logEntry.getChangedPaths().keySet();

		TagBranchRecognition tbr = new TagBranchRecognition(getRepository());
		
		TagBranch res = null;
		//Check if we have a Tag, Branch, Maven Tag or Subversion Tag.
		if (changedPathsSet.size() == 1) {
			res = tbr.checkForTagOrBranch(logEntry, changedPathsSet);
		} else {
			res = tbr.checkForMavenTag(logEntry, changedPathsSet);
			if (res == null) {
				res = tbr.checkForSubverisonTag(logEntry, changedPathsSet);
			}
		}

		if (LOGGER.isDebugEnabled()) {
			LOGGER.debug("Number of files for revision: " + changedPathsSet.size());
		}

		startIndexChangeSet();
		for (Iterator<?> changedPaths = changedPathsSet.iterator(); changedPaths.hasNext();) {

			RevisionDocument indexRevision = new RevisionDocument();

			addTagBranchToDoc(res, indexRevision);

			//It is needed to check it in every entry 
			//This will result in making entries for every record of the ChangeSet.
			SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry.getChangedPaths().get(changedPaths.next());
			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("SVNEntry: "
			            + entryPath.getType()
			            + "	"
			            + entryPath.getPath()
			            + ((entryPath.getCopyPath() != null) ? " (from "
			                    + entryPath.getCopyPath() + " revision "
			                    + entryPath.getCopyRevision() + ")" : ""));
			}

			//If the given path should be ignored than just do it.
			if (getFiltering().ignorePath(entryPath.getPath())) {
				continue;
			}

			//We would like to know something about the entry.
			SVNDirEntry dirEntry = tbr.getEntryCache().getEntry(logEntry.getRevision(), entryPath.getPath());

			try {
				beginIndexChangeSetItem(dirEntry);
				indexFile(indexRevision, indexWriter, dirEntry, logEntry, entryPath);
			} catch (IOException e) {
				LOGGER.error("IOExcepiton: ", e);
			} catch (SVNException e) {
				LOGGER.error("SVNExcepiton: ", e);
			} catch (Exception e) {
				LOGGER.error("something wrong: ", e);
			} finally {
				endIndexChangeSetItem(dirEntry);
			}
		}
		stopIndexChangeSet();
	}

	private void addTagBranchToDoc(TagBranch res, RevisionDocument indexRevision) {
		if (res != null) {
			switch (res.getType()) {
				case BRANCH:
					indexRevision.addUnTokenizedField(FieldNames.BRANCH, res.getName());
					break;
				case TAG:
					indexRevision.addUnTokenizedField(FieldNames.TAG, res.getName());
					switch(res.getTagType()) {
						case NONE:
							break;
						case TAG: //We already have it marked as Tag.
							break;
						case MAVENTAG:
							indexRevision.addUnTokenizedField(FieldNames.MAVENTAG, res.getName());
							break;
						case SUBVERSIONTAG:
							indexRevision.addUnTokenizedField(FieldNames.SUBVERSIONTAG, res.getName());
							break;
					}
					break;
				default:
					break;
			}
		}
	}

	/**
	 * The method will index a particular document (file) into the Lucene index.
	 * It will store the majority of the information about a file into the Lucene index like
	 * revision, copyfrom, path, filename etc.
	 * 
	 * @param doc
	 * @param indexWriter
	 * @param dirEntry
	 * @param repository
	 * @param logEntry
	 * @param entryPath
	 * @throws SVNException
	 * @throws IOException
	 */
	private void indexFile(RevisionDocument indexRevision, IndexWriter indexWriter, SVNDirEntry dirEntry, SVNLogEntry logEntry, SVNLogEntryPath entryPath) 
		throws SVNException, IOException {
			SVNProperties fileProperties = new SVNProperties();

			SVNNodeKind nodeKind = null;
			//if the entry has been deleted we will check the information about the entry 
			//via the revision before...
			LOGGER.debug("Before checking...");
			nodeKind = repository.getRepository().checkPath(entryPath.getPath(), logEntry.getRevision());
			LOGGER.debug("After checking...");

			indexRevision.addUnTokenizedField(FieldNames.REVISION, NumberUtils.pad(logEntry.getRevision()));

			boolean isDir = nodeKind == SVNNodeKind.DIR;
			boolean isFile = nodeKind == SVNNodeKind.FILE;
			FileName fileName = null;
			if (isDir) {
				LOGGER.debug("The " + entryPath.getPath() + " is a directory entry.");
				indexRevision.addUnTokenizedField(FieldNames.NODE, "dir");
				fileName = new FileName(entryPath.getPath(), true);
			} else if (isFile) {
				LOGGER.debug("The " + entryPath.getPath() + " is a file entry.");
				indexRevision.addUnTokenizedField(FieldNames.NODE, "file");
				fileName = new FileName(entryPath.getPath(), false);
			} else {
				//This means a file/directory has been deleted.
				indexRevision.addUnTokenizedField(FieldNames.NODE, "unknown");
				LOGGER.debug("The " + entryPath.getPath() + " is an unknown entry.");

				//We would like to know what is has been?
				//Directory? File? So we go a step back in History...
				long rev = logEntry.getRevision() - 1;
				SVNNodeKind nodeKindUnknown = getRepository().getRepository().checkPath(entryPath.getPath(), rev);
				LOGGER.debug("NodeKind(" + rev + "): " + nodeKindUnknown.toString());
				fileName = new FileName(entryPath.getPath(), nodeKindUnknown == SVNNodeKind.DIR);
			}

			if (LOGGER.isDebugEnabled()) {
				LOGGER.debug("FileNameCheck: entryPath   -> kind:" + nodeKind.toString() + " path:" + entryPath.getPath());
				LOGGER.debug("FileNameCheck:                path:'" + fileName.getPath() + "' filename:'" + fileName.getBaseName() + "'");
			}

			if (getFiltering().ignorePath(fileName.getPath())) {
				//Ignore the path...
			} else if (getFiltering().ignoreFilename(fileName.getBaseName())) {
				//Ignore filename
			}

			//TODO: We have to check if we need to set localization
			indexRevision.addUnTokenizedFieldNoStore(FieldNames.PATH, fileName.getPath().toLowerCase());
			indexRevision.addUnTokenizedField(FieldNames.PATH, fileName.getPath());

			//Does a copy operation took place...
			if (entryPath.getCopyPath() != null) {
				indexRevision.addUnTokenizedField(FieldNames.FROM, entryPath.getCopyPath());
				indexRevision.addUnTokenizedField(FieldNames.FROMREV, entryPath.getCopyRevision());
			}

			//The field we use for searching is stored as lowercase.
			//TODO: We have to check if we need to set localization
			indexRevision.addUnTokenizedFieldNoStore(FieldNames.FILENAME, fileName.getBaseName().toLowerCase());
			indexRevision.addUnTokenizedField(FieldNames.FILENAME, fileName.getBaseName());

			indexRevision.addUnTokenizedField(FieldNames.AUTHOR, logEntry.getAuthor() == null ? "" : logEntry.getAuthor());

			//We will add the message as tokenized field to be able to search within the log messages.
			indexRevision.addTokenizedField(FieldNames.MESSAGE, logEntry.getMessage() == null ? "" : logEntry.getMessage());
			indexRevision.addUnTokenizedField(FieldNames.DATE, logEntry.getDate());

			indexRevision.addUnTokenizedField(FieldNames.KIND, String.valueOf(entryPath.getType()).toLowerCase());

//TODO: May be don't need this if we use repository name?
			indexRevision.addUnTokenizedField(FieldNames.REPOSITORYUUID, getRepository().getRepository().getRepositoryUUID(false));
			
			indexRevision.addUnTokenizedField(FieldNames.REPOSITORY, getName());

			if (nodeKind == SVNNodeKind.NONE) {
				LOGGER.debug("The " + entryPath.getPath() + " is a NONE entry.");
			} else if (nodeKind == SVNNodeKind.DIR) {
				//The given entry is a directory.
				LOGGER.debug("The " + entryPath.getPath() + " is a directory.");
				//Here we need to call getDir to get directory properties.
				Collection<SVNDirEntry> dirEntries = null;
				getRepository().getRepository().getDir(entryPath.getPath(), logEntry.getRevision(), fileProperties, dirEntries);
				indexProperties(fileProperties, indexRevision);

			} else if (nodeKind == SVNNodeKind.FILE) {
				
				//The given entry is a file.
				//This means we will get every file from the repository....
				//Get only the properties of the file

				indexRevision.addTokenizedField(FieldNames.SIZE, Long.toString(dirEntry.getSize()));
				getRepository().getRepository().getFile(entryPath.getPath(), logEntry.getRevision(), fileProperties, null);
				indexProperties(fileProperties, indexRevision);

				FileExtensionHandler feh = new FileExtensionHandler();
				feh.setFileProperties(fileProperties);
				feh.setDoc(indexRevision);
				feh.execute(getRepository(), dirEntry, entryPath.getPath(), logEntry.getRevision());
			}

			indexWriter.addDocument(indexRevision.getDoc());
			LOGGER.debug("File " + entryPath.getPath() + " indexed...");
	}


	/**
	 * This method will index only those properties which do not start
	 * with {@link SVN_WC_PREFIX} nor with {@link SVN_ENTRY_PREFIX}.
	 * @param fileProperties
	 * @param doc
	 */
	private void indexProperties(SVNProperties fileProperties, RevisionDocument indexRevision) {
		SVNProperties list = fileProperties.getRegularProperties();

		for (Iterator<String> iterator = list.nameSet().iterator(); iterator.hasNext();) {
			String propname = (String) iterator.next();
			LOGGER.debug("Indexing property: " + propname);
			indexRevision.addUnTokenizedFieldNoStore(propname, list.getStringValue(propname).toLowerCase());
			indexRevision.addUnTokenizedField(propname, list.getStringValue(propname));
		}
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

	public boolean isAbbort() {
		return abbort;
	}

	public void setAbbort(boolean abbort) {
		this.abbort = abbort;
	}

	public void setFiltering(Filtering filtering) {
		this.filtering = filtering;
	}

	public Filtering getFiltering() {
		return filtering;
	}

}
