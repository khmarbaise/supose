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
// SupoSE
package com.soebes.supose;

import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.Date;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;

/**
 * @author Karl Heinz Marbaise
 *
 */
public class TestLucene {

	public static void main(String[] args) {
		File indexDir = new File("indexDir.download");
		File dataDir = new File("c:/download");
		long start = new Date().getTime();
		try {
			int numIndexed = index(indexDir, dataDir);
		} catch (IOException e) {
			e.printStackTrace();
		}
		long stop = new Date().getTime();
		System.out.println("Time: " + (stop-start));
	}

	public static int index(File indexDir, File dataDir) throws IOException {

		IndexWriter writer = new IndexWriter(indexDir, new StandardAnalyzer(), true);
		writer.setUseCompoundFile(false);
		indexDirectory(writer, dataDir);
		
		int numIndexed = writer.docCount();
		writer.optimize();
		writer.close();
		return numIndexed;
	}
	
	
	private static void indexDirectory(IndexWriter writer, File dir) throws IOException {
		File[] files = dir.listFiles();
		for (int i = 0; i < files.length; i++) {
			File file = files[i];
			if (file.isDirectory()) {
				indexDirectory(writer, file);
			} else {
				indexFile(writer, file);
			}
		}
	}
	
	private static void indexFile(IndexWriter writer, File f) throws IOException {
		if (f.isHidden() || !f.exists() || !f.canRead()) {
			return;
		}
		
		System.out.println("Indexing: " + f.getCanonicalPath());
		Document doc = new Document();
		
		FileReader fr = new FileReader(f);
		System.out.println("content: " + fr.toString());
		doc.add(new Field("contents", new FileReader(f)));
		doc.add(new Field("filename", f.getCanonicalPath(), Field.Store.YES, Field.Index.UN_TOKENIZED));
		writer.addDocument(doc);
	}
	
	
}
