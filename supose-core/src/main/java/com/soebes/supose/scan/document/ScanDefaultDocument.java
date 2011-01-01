/**
 * The (Su)bversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007-2011 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (c) 2007-2011 by Karl Heinz Marbaise
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
package com.soebes.supose.scan.document;

import java.io.ByteArrayOutputStream;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.SVNDirEntry;

import com.soebes.supose.FieldNames;
import com.soebes.supose.repository.Repository;

/**
 * This class will handle all files types which are not handled by particular
 * implementation of the <code>AScanDocument</code> class. This will happen if
 * we have files like: *.mdb, *.... All other types of files (which will be
 * defined by Subversion as Text) their contents will be indexed.
 *
 * @author Karl Heinz Marbaise
 *
 */
public class ScanDefaultDocument extends AScanDocument {
    private static Logger LOGGER = Logger.getLogger(ScanDefaultDocument.class);

    public ScanDefaultDocument() {
    }

    @Override
    public void indexDocument(Repository repository, SVNDirEntry dirEntry,
            String path, long revision) {
        LOGGER.debug("Scanning document");

        try {
            if (isBinary()) {
                // We can make a decision to ignore binary content completeley
                // cause this is the default document scanner.
                // TODO: Check this if this is ok? Or should we simply do not
                // make any entry?
                LOGGER.debug("We don't scan the contents, cause it's binary.");
                getDocument().addTokenizedField(FieldNames.CONTENTS,
                "BINARY CONTENT");
            } else {
                LOGGER.debug("We scan the contents, cause it's text.");
                if (dirEntry.getSize() > 10 * 1024 * 1024) {
                    LOGGER.warn("Document size exceeds limit of 10 Mibi!");
                } else {
                    // Contents will be scanned if it is less than 10 Mibi
                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    // This means we get the contents of the file only. No
                    // properties.
                    repository.getRepository().getFile(path, revision, null,
                            baos);
                    getDocument().addTokenizedField(FieldNames.CONTENTS,
                            baos.toString());
                }
            }
        } catch (Exception e) {
            LOGGER.error("Something has gone wrong with DefaultDocuments", e);
        }
    }

}
