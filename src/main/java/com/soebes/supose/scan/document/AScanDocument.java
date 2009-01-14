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
package com.soebes.supose.scan.document;

import java.util.Calendar;
import java.util.Date;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNProperties;
import org.tmatesoft.svn.core.SVNProperty;

import com.soebes.supose.repository.Repository;

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

	private Document doc;
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

	public void setDocument(Document doc) {
		this.doc = doc;
	}
	
	public Document getDocument() {
		return doc;
	}
	
	protected void addTokenizedField(String fieldName, String value) {
		doc.add(new Field(fieldName, value, Field.Store.YES, Field.Index.TOKENIZED));
	}
	protected void addTokenizedField(String fieldName, byte[] value) {
		doc.add(new Field(fieldName, value, Field.Store.YES));
	}
	protected void addUnTokenizedField(String fieldName, String value) {
		doc.add(new Field(fieldName,  value, Field.Store.YES, Field.Index.UN_TOKENIZED));
	}
	protected void addUnTokenizedField(String fieldName, Long value) {
		doc.add(new Field(fieldName,  value.toString(), Field.Store.YES, Field.Index.UN_TOKENIZED));
	}
	protected void addUnTokenizedField(String fieldName, Date value) {
		doc.add(new Field(fieldName,  value.toGMTString(), Field.Store.YES, Field.Index.UN_TOKENIZED));
	}
	protected void addUnTokenizedField(String fieldName, Calendar value) {
		//TODO: May be we have to change the format information for the Calendar object.
		doc.add(new Field(fieldName,  value.toString(), Field.Store.YES, Field.Index.UN_TOKENIZED));
	}
	protected void addUnTokenizedField(String fieldName, Character value) {
		doc.add(new Field(fieldName,  value.toString(), Field.Store.YES, Field.Index.UN_TOKENIZED));
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
