/*
 * The (S)ubversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (C) 2007 by Karl Heinz Marbaise

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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA *
 *
 * The License can viewed online under http://www.gnu.org/licenses/gpl.html
 * If you have any questions about the Software or about the license
 * just write an email to license@soebes.de
 *
 */
package com.soebes.supose.scan;

import java.io.ByteArrayOutputStream;

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.io.SVNRepository;

import com.soebes.supose.FieldNames;

/**
 * @author Karl Heinz Marbaise
 *
 * This class will handle all files types which are not handled by particular
 * implementation of the <code>AScanDocument</code> class.
 * This will happen if we have files like:
 * *.mdb, *....
 * All other types of files (which will be defined by Subversion as Text) 
 * their contents will be indexed.
 * 
 */
public class ScanDefaultDocument extends AScanDocument {
	private static Logger LOGGER = Logger.getLogger(ScanDefaultDocument.class);

	public ScanDefaultDocument() {
	}

	@Override
	public void indexDocument(SVNRepository repository, String path, long revision) {
		LOGGER.info("Scanning document");
		
		try {
			if (isBinary()) {
				//We can make a decision to ignore binary content completeley
				//cause this is the default document scanner.
//TODO: Check this if this is ok? Or should we simply do not make any entry?
				addTokenizedField(FieldNames.CONTENTS, "BINARY CONTENT");
			} else {
				ByteArrayOutputStream baos = new ByteArrayOutputStream();
				//This means we get the contents of the file only. No properties.
				repository.getFile(path, revision, null, baos);
				addTokenizedField(FieldNames.CONTENTS, baos.toString());
			}
		} catch (Exception e) {
			LOGGER.error("Something has gone wrong with WordDocuments " + e);
		}
	}
	
}
