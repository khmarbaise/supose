package com.soebes.supose.scan;

import static org.testng.Assert.assertEquals;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.testng.annotations.BeforeGroups;
import org.testng.annotations.BeforeTest;
import org.testng.annotations.Test;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.soebes.supose.FieldNames;
import com.soebes.supose.TestBase;
import com.soebes.supose.index.Index;
import com.soebes.supose.repository.Repository;
import com.soebes.supose.search.CustomQueryParser;

public class ScanRepositoryTest extends TestBase {
	private static Logger LOGGER = Logger.getLogger(ScanRepositoryTest.class);

	private static ScanRepository scanRepository = new ScanRepository();
	
	@BeforeTest
	public void scanRepos() throws SVNException {
		Index index = new Index ();
		//We will create a new one if --create is given on command line
		//otherwise we will append to the existing index.
		Analyzer analyzer = new StandardAnalyzer();		
		index.setAnalyzer(analyzer);
		//For the test we allways create the index.
		index.setCreate(true);
		IndexWriter indexWriter = index.createIndexWriter(getIndexDirectory());

		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(
			"", 
			""
		);
		String repositoryDir = getRepositoryDirectory();
		SVNURL url = SVNURL.fromFile(new File(repositoryDir));
		Repository repository = new Repository("file://" + url.getURIEncodedPath(), authManager);

		scanRepository.setRepository(repository);

		//We start with the revision which is given on the command line.
		//If it is not given we will start with revision 1.
		scanRepository.setStartRevision(1); 
		//We will scan the repository to the current HEAD of the repository.
		scanRepository.setEndRevision(-1);

		LOGGER.info("Scanning started.");
		scanRepository.scan(indexWriter);
		LOGGER.info("Scanning ready.");

		try {
			indexWriter.optimize();
			indexWriter.close();
		} catch (CorruptIndexException e) {
			LOGGER.error("CorruptIndexException: Error during optimization of index: " + e);
		} catch (IOException e) {
			LOGGER.error("IOException: Error during optimization of index: " + e);
		}
	}
	
	
	private TopDocs getQueryResult(String queryLine) {
		String indexDirectory = getIndexDirectory();
	    IndexReader reader = null;
	    TopDocs result = null;	    
	    try {
	    	
	    	reader = IndexReader.open(indexDirectory);
	    	
	    	Searcher searcher = new IndexSearcher(reader);
	    	Analyzer analyzer = new StandardAnalyzer();
	    	SortField[] sf = {
	    		new SortField(FieldNames.REVISION),
	    		new SortField(FieldNames.FILENAME),
	    	};
	    	Sort sort = new Sort(sf);
	    	//Here we define the default field for searching.
	        QueryParser parser = new CustomQueryParser(FieldNames.CONTENTS, analyzer);
	        //We will allow using a wildcard at the beginning of the expression.
	        parser.setAllowLeadingWildcard(true);
	        //The search term will not be expanded to lowercase.
	        parser.setLowercaseExpandedTerms(false);
	        Query query = parser.parse(queryLine);

	        //That's not the best idea...but currently i have not better solution for this...
	        TopDocs tmp = searcher.search(query, null, 20, sort);
		    result = searcher.search(query, null, tmp.totalHits, sort);
		    for (int i = 0; i < result.scoreDocs.length; i++) {
		    	Document hit = searcher.doc(result.scoreDocs[i].doc);
				List<Field> fieldList = hit.getFields();
				System.out.print((i+1) + ". ");
				for(int k=0; k<fieldList.size();k++) {
					Field field = (Field) fieldList.get(k);
					if (FieldNames.FILENAME.equals(field.name())) {
						System.out.print("F:" + field.stringValue() + " ");
					}
					if (FieldNames.REVISION.equals(field.name())) {
						long rev = Long.parseLong(field.stringValue());
						System.out.print("R:" + rev + " ");
					}
					if (FieldNames.KIND.equals(field.name())) {
						System.out.print("K:" + field.stringValue() + " ");
					}
				}
				System.out.println("");
			}

	    } catch (CorruptIndexException e) {
			LOGGER.error("Error: The index is corrupted: " + e);
	    } catch (IOException e) {
			System.err.println("Error: IOException: " + e);
		} catch (Exception e) {
			LOGGER.error("Error: Something has gone wrong: " + e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				LOGGER.error("Error: IOException during close(): " + e);
			}
		}
		return result;
	}

	@Test
	public void testQueryForFilename() {
		TopDocs result = getQueryResult("+filename:*.txt");
	    assertEquals(result.totalHits, 6);
	}
	
}
