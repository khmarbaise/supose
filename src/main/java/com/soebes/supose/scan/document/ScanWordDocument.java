/*
 * The (S)ubversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007, 2008 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (c) 2007, 2008 by Karl Heinz Marbaise

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
package com.soebes.supose.scan.document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import org.apache.log4j.Logger;
import org.apache.poi.hwpf.extractor.WordExtractor;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;

import com.soebes.supose.FieldNames;
import com.soebes.supose.repository.Repository;

/**
 * @author Karl Heinz Marbaise
 *
 */
public class ScanWordDocument extends AScanDocument {
	private static Logger LOGGER = Logger.getLogger(ScanWordDocument.class);

	public ScanWordDocument() {
	}

	@Override
	public void indexDocument(Repository repository, SVNDirEntry dirEntry, String path, long revision) {
		LOGGER.info("Scanning word document");
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			//This means we get the contents of the file only. No properties.
			repository.getRepository().getFile(path, revision, null, baos);
			ByteArrayInputStream str = new ByteArrayInputStream(baos.toByteArray());

			WordExtractor we = new WordExtractor(str);
			addTokenizedField(FieldNames.CONTENTS, we.getText());
		} catch (SVNException e) {
			LOGGER.error("Exception by SVN: " + e);
		} catch (Exception e) {
			LOGGER.error("Something has gone wrong with WordDocuments " + e);
		}
	}

}
