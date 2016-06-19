/**
 * The (Su)bversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007-2016 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (c) 2007-2016 by Karl Heinz Marbaise
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
package com.soebes.supose.core.recognition;

import java.util.Iterator;
import java.util.Set;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.SVNLogEntryPath;
import org.tmatesoft.svn.core.SVNNodeKind;

import com.soebes.supose.core.recognition.TagBranch.TagType;
import com.soebes.supose.core.repository.Repository;

/**
 * This class is intended to analyze if a tag or branch is on hand.
 * 
 * @author Karl Heinz Marbaise
 * 
 */
public class TagBranchRecognition {
    private static Logger LOGGER = Logger.getLogger(TagBranchRecognition.class);

    /**
     * @FIXME: Move this constants to a configuration file.
     */
    public static final String TAGS = "/tags/";
    /**
     * @FIXME: Move this constants to a configuration file.
     */
    public static final String MAVEN_TAG_PREFIX = "[maven-release-plugin]  copy for tag ";

    private Repository repository = null;

    private EntryCache entryCache = null;

    public TagBranchRecognition(Repository repository) {
        setRepository(repository);
        setEntryCache(new EntryCache(repository));
    }

    /**
     * A <a href="http://maven.apache.org">Maven</a> tag is currently based on a
     * <a href=
     * "http://svnbook.red-bean.com/en/1.5/svn-book.html#svn.branchmerge.tags.mkcomplex"
     * >complex tag</a> in Subversion.
     * 
     * @param result
     * @param logEntry
     * @param changedPathsSet
     * @return Will return the TagType or null if no appropriate Type (Maven
     *         Tag) has been found.
     */
    public TagBranch checkForMavenTag(SVNLogEntry logEntry,
            Set<?> changedPathsSet) {
        TagBranch result = null;
        // The log message is the first indication for a maven tag...
        if (!logEntry.getMessage().startsWith(MAVEN_TAG_PREFIX)) {
            return result;
        }

        // The first assumption the log message is correct...
        for (Iterator<?> changedPaths = changedPathsSet.iterator(); changedPaths
                .hasNext();) {
            SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry
                    .getChangedPaths().get(changedPaths.next());

            if (entryPath.getType() == SVNLogEntryPath.TYPE_ADDED) {
                if (entryPath.getCopyPath() != null) {
                    SVNDirEntry destEntry = getEntryCache().getEntry(
                            logEntry.getRevision(), entryPath.getPath());
                    SVNDirEntry sourceEntry = getEntryCache().getEntry(
                            logEntry.getRevision(), entryPath.getCopyPath());

                    TagBranch bt = new TagBranch();
                    bt.setName(entryPath.getPath());
                    bt.setRevision(logEntry.getRevision());
                    bt.setCopyFromRevision(entryPath.getCopyRevision());

                    // Source and destination of the copy operation must be a
                    // directory
                    if (destEntry.getKind() == SVNNodeKind.DIR
                            && sourceEntry.getKind() == SVNNodeKind.DIR) {

                        // If we the /tags/ part this is assumed to be a Tag.
                        if (entryPath.getPath().contains(
                                TagBranchRecognition.TAGS)) {
                            bt.setType(TagBranch.Type.TAG);
                            bt.setTagType(TagBranch.TagType.MAVENTAG);
                            // Interception Point: MavenTagRecognized()
                            result = bt;
                            LOGGER.debug("Maven tag recognized");
                        }
                    }
                }
            }
        }
        return result;
    }

    /**
     * This method will check entries within a ChangeSet to check if a
     * Tag/Branch has been created. The first indication is the copy operation
     * which has been done and the ChangeSet does contain only a single (<a
     * href="http://www.supose.org/issues/show/106">Issue 106</a>).
     * 
     * @param result
     * @param logEntry
     * @param changedPathsSet
     */
    public TagBranch checkForTagOrBranch(SVNLogEntry logEntry,
            Set<?> changedPathsSet) {

        TagBranch result = null;
        Iterator<?> changedPaths = changedPathsSet.iterator();
        SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry
                .getChangedPaths().get(changedPaths.next());

        // a copy-to has happened so we can have a branch or a tag?
        if (entryPath.getCopyPath() != null) {
            SVNDirEntry destEntry = getEntryCache().getEntry(
                    logEntry.getRevision(), entryPath.getPath());
            SVNDirEntry sourceEntry = getEntryCache().getEntry(
                    entryPath.getCopyRevision(), entryPath.getCopyPath());

            // Source and destination of the copy operation must be a
            // directories
            if (destEntry.getKind() == SVNNodeKind.DIR
                    && sourceEntry.getKind() == SVNNodeKind.DIR) {
                TagBranch bt = new TagBranch();
                bt.setName(entryPath.getPath());
                bt.setRevision(logEntry.getRevision());
                bt.setCopyFromRevision(entryPath.getCopyRevision());
                if (entryPath.getPath().contains(TagBranchRecognition.TAGS)) {
                    // Interception Point: tagRecognized()
                    bt.setType(TagBranch.Type.TAG);
                    bt.setTagType(TagType.TAG);
                } else {
                    bt.setType(TagBranch.Type.BRANCH);
                    bt.setTagType(TagType.NONE);
                    // Interception Point: branchRecognized()
                }
                result = bt;
            }

        }
        return result;
    }

    /**
     * This method will recognize a Subversion Tag. This kind of tags is used by
     * the Subversion team to mark a particular release.
     * 
     * The following pattern defines such kind of Subversion Tag.
     * 
     * <pre>
     * A /tags/RELEASE-1.0.0 (from: /branches/1.6.1:39700)
     * M /tags/RELEASE-1.0.0/svn_version.h
     * </pre>
     * 
     * In Subversion terms it's called a complex tag. (<a
     * href="http://www.supose.org/issues/show/196">Feature 196</a>).
     * 
     * @param logEntry
     * @param changedPathsSet
     * @return null otherwise the information about the complex tag.
     */
    public TagBranch checkForSubverisonTag(SVNLogEntry logEntry,
            Set<?> changedPathsSet) {
        TagBranch result = null;

        // The first assumption the log message is correct...
        for (Iterator<?> changedPaths = changedPathsSet.iterator(); changedPaths
                .hasNext();) {

            SVNLogEntryPath entryPath = (SVNLogEntryPath) logEntry
                    .getChangedPaths().get(changedPaths.next());

            if (entryPath.getType() == SVNLogEntryPath.TYPE_MODIFIED) {
                // No copy operation has taken place.
                if (entryPath.getCopyPath() == null) {
                    SVNDirEntry destEntry = getEntryCache().getEntry(
                            logEntry.getRevision(), entryPath.getPath());
                    if (destEntry.getKind() == SVNNodeKind.FILE) {
                        // That might be a candidate...
                        if (result != null) {
                            if (entryPath.getPath()
                                    .startsWith(result.getName())) {
                                // If the modification is done in the Tags
                                // path...
                                // Interception Point: subversionTagRecognized()
                                result.setTagType(TagType.SUBVERSIONTAG);
                            } else {
                                result = null;
                            }
                        }
                    }
                }
            } else if (entryPath.getType() == SVNLogEntryPath.TYPE_ADDED) {
                // The usual Tag part... /tags/RELEASE-1.0.0 (from: /trunk:23)
                if (entryPath.getCopyPath() != null) {
                    SVNDirEntry destEntry = getEntryCache().getEntry(
                            logEntry.getRevision(), entryPath.getPath());
                    SVNDirEntry sourceEntry = getEntryCache().getEntry(
                            entryPath.getCopyRevision(),
                            entryPath.getCopyPath());
                    TagBranch bt = new TagBranch();
                    bt.setName(entryPath.getPath());
                    bt.setRevision(logEntry.getRevision());
                    bt.setCopyFromRevision(entryPath.getCopyRevision());

                    // Source and destination of the copy operation must be a
                    // directory
                    if (destEntry.getKind() == SVNNodeKind.DIR
                            && sourceEntry.getKind() == SVNNodeKind.DIR) {

                        // If we the /tags/ part this is assumed to be a Tag.
                        if (entryPath.getPath().contains(
                                TagBranchRecognition.TAGS)) {
                            bt.setType(TagBranch.Type.TAG);
                            bt.setTagType(TagType.TAG);
                            result = bt;
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

    public EntryCache getEntryCache() {
        return entryCache;
    }

    public void setEntryCache(EntryCache entryCache) {
        this.entryCache = entryCache;
    }

}
