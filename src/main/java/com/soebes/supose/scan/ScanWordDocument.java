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
import java.util.HashMap;
import java.util.Map;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.tmatesoft.svn.core.io.SVNRepository;

import com.soebes.supose.utility.FileName;

/**
 * @author Karl Heinz Marbaise
 *
 */
public class ScanWordDocument extends AScanDocument {

	public ScanWordDocument(Document doc) {
		super(doc);
	}

	public void indexDocument(SVNRepository repository, String path, long revision) {
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Map fileProperties  = new HashMap();

		repository.getFile(path, revision, fileProperties, baos);

		Document doc = getDocument();
		doc.add(new Field("revision", Long.toString(revision), Field.Store.YES, Field.Index.UN_TOKENIZED));
		Character x = new Character(entryPath.getType());
		doc.add(new Field("kind", x.toString(), Field.Store.YES, Field.Index.UN_TOKENIZED));
		FileName fileName = new FileName(path); 
		//entryPath.getType())
		doc.add(new Field("repository", repository.getRepositoryUUID(false), Field.Store.YES, Field.Index.UN_TOKENIZED));
		//TODO: Should be filled with an usable name to distinguish different repositories..
		doc.add(new Field("repositoryname", "TESTREPOS", Field.Store.YES, Field.Index.UN_TOKENIZED));

		doc.add(new Field("path", fileName.getPath(), Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field("filename", path, Field.Store.YES, Field.Index.UN_TOKENIZED));
		
		doc.add(new Field("contents", baos.toString(), Field.Store.YES, Field.Index.TOKENIZED));

		doc.add(new Field("size", Long.toString(baos.size()), Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field("author", logEntry.getAuthor(), Field.Store.YES, Field.Index.UN_TOKENIZED));
		doc.add(new Field("message", logEntry.getMessage(), Field.Store.YES, Field.Index.TOKENIZED));
		doc.add(new Field("date", logEntry.getDate().toGMTString(), Field.Store.YES, Field.Index.UN_TOKENIZED));
	}

}
