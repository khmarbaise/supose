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

import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.io.SVNRepository;

import com.soebes.supose.utility.FileExtensionProperty;
import com.soebes.supose.utility.FileName;

/**
 * @author Karl Heinz Marbaise
 *
 */
public class FileExtensionHandler {
	private static Logger LOGGER = Logger.getLogger(FileExtensionProperty.class);

	public void execute(SVNRepository repository, String path, long revision) {

		FileName fn = new FileName(path);
		//Check if we have an extension...
		if (fn.getExt().length() > 0) {
			try {
				String className = FileExtensionProperty.getInstance().getProperty(fn.getExt());
				Class handlerClass = Class.forName(className);
				AScanDocument dh = (AScanDocument) handlerClass.newInstance();
				dh.indexDocument(repository, path, revision);
				
			} catch (Exception e) {
				//There is no entry for the extension
				LOGGER.info("There is no property entry defined for the file extension '" + fn.getExt() + "'");
			}
		} else {
			LOGGER.info("We have no file extension found for the file '" + path + "'");
		}
		
		
//		AScanDocument documentScanner = new AScanDocument();
//		
//		documentScanner.indexDocument(repository, path, revision);
//		indexWriter.addDocument(documentScanner.getDocument());

	}
}
