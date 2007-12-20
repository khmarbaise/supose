package com.soebes.supose.scan;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;
import org.testng.annotations.Test;

public class IndexMergeTest {

	private static void addTokenizedField(Document doc, String fieldName, String value) {
		doc.add(new Field(fieldName,  value, Field.Store.YES, Field.Index.TOKENIZED));
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
