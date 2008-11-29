package com.soebes.supose.lucene;

import static org.testng.Assert.*;

import java.io.IOException;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.soebes.supose.FieldNames;

public class LuceneTest {
    // Store the index in memory:
    private Directory directory = new RAMDirectory();
    // Now search the index:
    private IndexSearcher isearcher = null;

	private void addUnTokenizedField(Document doc, String fieldName, String value) {
		doc.add(new Field(fieldName,  value, Field.Store.YES, Field.Index.UN_TOKENIZED));
	}
	private void addTokenizedField(Document doc, String fieldName, String value) {
		doc.add(new Field(fieldName,  value, Field.Store.YES, Field.Index.TOKENIZED));
	}

	@BeforeClass
	public void beforeClass() throws CorruptIndexException, LockObtainFailedException, IOException {
		 Analyzer analyzer = new StandardAnalyzer();

	    // To store an index on disk, use this instead:
	    //Directory directory = FSDirectory.getDirectory("/tmp/testindex");
	    IndexWriter iwriter = new IndexWriter(directory, analyzer, true);
	    iwriter.setMaxFieldLength(25000);

	    Document doc = new Document();
	    String text = "This is the text to be indexed.";
	    addTokenizedField(doc, FieldNames.CONTENTS, text);
	    addUnTokenizedField(doc, FieldNames.FILENAME, "/trunk/doc/testXML.doc");
	    iwriter.addDocument(doc);
	    
	    doc = new Document();
	    text = "This is different text.";
	    addTokenizedField(doc, FieldNames.CONTENTS, text);
	    addUnTokenizedField(doc, FieldNames.FILENAME, "/tags/docs/XYZabc.java");
	    iwriter.addDocument(doc);
	    
	    doc = new Document();
	    text = "This is more different text.";
	    addTokenizedField(doc, FieldNames.CONTENTS, text);
	    addUnTokenizedField(doc, FieldNames.FILENAME, "/tags/docs/SCMPlan.doc");
	    iwriter.addDocument(doc);

	    doc = new Document();
	    text = "This is the third text.";
	    addTokenizedField(doc, FieldNames.CONTENTS, text);
	    addUnTokenizedField(doc, FieldNames.FILENAME, "/trunk/subdir/elviraXML.doc");
	    iwriter.addDocument(doc);
	    
	    iwriter.optimize();
	    iwriter.close();
	    
	    isearcher = new IndexSearcher(directory);
	}

	private void printOut(String msg, Hits hits) throws CorruptIndexException, IOException {
		System.out.println(msg);
	    for (int i = 0; i < hits.length(); i++) {
	      Document hitDoc = hits.doc(i);
	      System.out.println("\tDoc [" + i + "]"); 
	      System.out.println("\t   filename:" + hitDoc.get(FieldNames.FILENAME));
	      System.out.println("\t  fieldname:" + hitDoc.get(FieldNames.CONTENTS));
	    }
	}
	
	@AfterClass
	public void afterClass() throws IOException {
		isearcher.close();
		directory.close();		
	}

	@Test
	public void testTheFirstSearch() throws ParseException, IOException {
		 Analyzer analyzer = new StandardAnalyzer();
	    // Parse a simple query that searches for "text":
	    QueryParser parser = new QueryParser(FieldNames.CONTENTS, analyzer);
	    Query query = parser.parse("+filename:/*.doc");
	    Hits hits = isearcher.search(query);
	    // Iterate through the results:
	    printOut("testTheFirstSearch[" + hits.length() + "]", hits);
	    assertEquals(hits.length(), 3);
	}

	
	@Test
	public void testTheSecondSearch() throws ParseException, IOException {
		 Analyzer analyzer = new StandardAnalyzer();
	    // Parse a simple query that searches for "text":
	    QueryParser parser = new QueryParser(FieldNames.CONTENTS, analyzer);
	    Query query = parser.parse("+filename:/trunk/*.doc");
	    Hits hits = isearcher.search(query);
	    // Iterate through the results:
	    printOut("testTheSecondSearch[" + hits.length() + "]", hits);
	    assertEquals(hits.length(), 2);
	}

	@Test
	public void testTheThirdSearch() throws ParseException, IOException {
		 Analyzer analyzer = new StandardAnalyzer();
	    // Parse a simple query that searches for "text":
	    QueryParser parser = new QueryParser(FieldNames.CONTENTS, analyzer);
	    Query query = parser.parse("+filename:/*te*.doc");
	    System.out.println("Query: " + query.toString());
	    Hits hits = isearcher.search(query);
	    printOut("testTheThirdSearch[" + hits.length() + "]", hits);
	    assertEquals(hits.length(), 1);
	}

	@Test
	public void testTheForthSearch() throws ParseException, IOException {
		 Analyzer analyzer = new StandardAnalyzer();
	    // Parse a simple query that searches for "text":
	    QueryParser parser = new QueryParser(FieldNames.CONTENTS, analyzer);
	    System.out.println("X:" + parser.getLowercaseExpandedTerms());
	    System.out.println("S:" + parser.getPhraseSlop());
	    parser.setLowercaseExpandedTerms(false);
	    Query query = parser.parse("+filename:/*SCM*.doc");
	    System.out.println("Query: " + query.toString());
	    Hits hits = isearcher.search(query);
	    printOut("testTheForthSearch[" + hits.length() + "]", hits);
	    assertTrue(hits.length() == 1, "Expected to get at least one element.");
	}
	
}
