/**
 * The (Su)bversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
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
package com.soebes.supose.scan;

import java.io.File;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.Test;

import com.soebes.supose.index.Index;

import static org.testng.Assert.*;

public class IndexMergeTest {

	static public boolean deleteDirectory(String fileName) {
		File f = new File(fileName);
		return deleteDirectory(f);
	}

	static public boolean deleteDirectory(File path) {
		if (path.exists()) {
			File[] files = path.listFiles();
			for (int i = 0; i < files.length; i++) {
				if (files[i].isDirectory()) {
					deleteDirectory(files[i]);
				} else {
					files[i].delete();
				}
			}
		}
		return (path.delete());
	}

	private static void addTokenizedField(Document doc, String fieldName, String value) {
		doc.add(new Field(fieldName,  value, Field.Store.YES, Field.Index.ANALYZED));
	}

	@AfterClass
	public void afterClass() {
		assertTrue(deleteDirectory("index1"), "Something wrong during deletion of index1");
		assertTrue(deleteDirectory("index2"), "Something wrong during deletion of index2");
		assertTrue(deleteDirectory("result"), "Something wrong during deletion of result");
	}

	//1. Create index with some contents
	//2. Create an other index with some contents
	//3. merge the two indexes..

	@Test()
	public void testIndex1 () throws Exception {
		Index index = new Index ();
		IndexWriter indexWriter = index.createIndexWriter("index1");
		Document doc = new Document();
		addTokenizedField(doc, "revision", "1");
		addTokenizedField(doc, "revision", "2");
		indexWriter.addDocument(doc);
		indexWriter.close();
	}
	
	@Test()
	public void testIndex2 () throws Exception {
		Index index = new Index ();
		IndexWriter indexWriter = index.createIndexWriter("index2");
		Document doc = new Document();
		addTokenizedField(doc, "revision", "3");
		addTokenizedField(doc, "revision", "4");
		indexWriter.addDocument(doc);
		indexWriter.close();
	}
	
	@Test(dependsOnMethods={"testIndex1", "testIndex2"})
	public void testMergeIndexes () throws Exception {
		Index index = new Index ();
		
		IndexWriter indexWriter = index.createIndexWriter("result");

		FSDirectory fsDirs[] = { FSDirectory.getDirectory("index1"), FSDirectory.getDirectory("index2") };
		indexWriter.addIndexes(fsDirs);
		indexWriter.close();
	}

}
