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

package com.soebes.supose.scan.document;

import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNProperty;

import com.soebes.supose.repository.Repository;
import com.soebes.supose.scan.RevisionDocument;

/**
 * This abstract class defines the 
 * basic interface to index a document.
 * 
 * It includes some helper method.
 * If you use an derived class of this you will be called
 * via the <code>indexDocument</code> method. Before the call the 
 * <code>properties</code> had been filled.
 * 
 * @author Karl Heinz Marbaise
 *
 */
public abstract class AScanDocument {

	private RevisionDocument doc;
	private SVNProperties properties;


	public AScanDocument() {
		setDocument(null);
	}

	/**
	 * This method will do the real scanning job of a single document.
	 * In other words it will do a scanning on a single file.
	 * @param repository Instance of the Repository class
	 * @param dirEntry   SVNDirEntry which will hold some information about the entry e.g. 
	 *  				 the size if it is a file.
	 * @param path		 The path in the repository.
	 * @param revision   The particular revision in the repository.
	 */
	public abstract void indexDocument (Repository repository, SVNDirEntry dirEntry, String path, long revision);

	public void setDocument(RevisionDocument doc) {
		this.doc = doc;
	}
	
	public RevisionDocument getDocument() {
		return doc;
	}
	
	protected boolean isBinary () {
		String mimeType = getProperties().getStringValue(SVNProperty.MIME_TYPE);
		return SVNProperty.isBinaryMimeType(mimeType);
	}
	protected boolean isText () {
		String mimeType = getProperties().getStringValue(SVNProperty.MIME_TYPE);
		return SVNProperty.isTextMimeType(mimeType);
	}

	
	public SVNProperties getProperties() {
		return properties;
	}

	public void setProperties(SVNProperties properties) {
		this.properties = properties;
	}


}
