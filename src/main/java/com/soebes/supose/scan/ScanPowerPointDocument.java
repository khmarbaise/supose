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

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Field;
import org.apache.poi.hslf.extractor.PowerPointExtractor;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * @author Karl Heinz Marbaise
 *
 */
public class ScanPowerPointDocument extends AScanDocument {
	private static Logger LOGGER = Logger.getLogger(ScanPowerPointDocument.class);

	public ScanPowerPointDocument() {
	}

	public void indexDocument(SVNRepository repository, String path, long revision) {
		LOGGER.info("Scanning document");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Map fileProperties  = new HashMap();

		try {
			repository.getFile(path, revision, fileProperties, baos);
		} catch (SVNException e) {
			LOGGER.error("Exception happend. " + e);
		}
		
		ByteArrayInputStream str = new ByteArrayInputStream(baos.toByteArray());

		try {
			PowerPointExtractor pe = new PowerPointExtractor(str);
//TODO: Add fields for slides, title etc.
			getDocument().add(new Field("contents", pe.getText(), Field.Store.YES, Field.Index.UN_TOKENIZED));
		} catch (Exception e) {
			LOGGER.error("Something has gone wrong with WordDocuments " + e);
		}
	}

}
