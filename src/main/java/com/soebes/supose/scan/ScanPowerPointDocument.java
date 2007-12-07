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

import org.apache.log4j.Logger;
import org.apache.poi.hslf.extractor.PowerPointExtractor;

import com.soebes.supose.FieldNames;

/**
 * @author Karl Heinz Marbaise
 *
 */
public class ScanPowerPointDocument extends AScanDocument {
	private static Logger LOGGER = Logger.getLogger(ScanPowerPointDocument.class);

	public ScanPowerPointDocument() {
	}

	public void indexDocument(ByteArrayOutputStream baos) {
		LOGGER.info("Scanning document");

		ByteArrayInputStream str = new ByteArrayInputStream(baos.toByteArray());

		try {
//TODO: Check if this enough...
			PowerPointExtractor pe = new PowerPointExtractor(str);
//TODO: Add fields for slides, title etc.
			addTokenizedField(FieldNames.CONTENTS, pe.getText());
		} catch (Exception e) {
			LOGGER.error("Something has gone wrong with WordDocuments " + e);
		}
	}

}
