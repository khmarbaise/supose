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
// SupoSE
package com.soebes.supose.search;

import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;

import com.soebes.supose.FieldNames;

public class SearchRepository {
	private static Logger LOGGER = Logger.getLogger(SearchRepository.class);
	
	private String indexDirectory = null;
	
	private Analyzer analyzer = null;
	private Searcher searcher = null;
	private IndexReader reader = null;
	
	public SearchRepository() {
		setIndexDirectory(null);
		setAnalyzer(new StandardAnalyzer());
	}

	public SearchRepository(String indexDirectory) {
		setIndexDirectory(indexDirectory);
		setAnalyzer(new StandardAnalyzer());
		setReader(null);
	}

	public TopDocs getQueryResult(String queryLine) {
	    IndexReader reader = null;
	    TopDocs result = null;	    
	    try {
	    	
	    	reader = IndexReader.open(getIndexDirectory());
	    	setReader(reader);

	    	BooleanQuery.setMaxClauseCount(Integer.MAX_VALUE);
	    	Searcher searcher = new IndexSearcher(reader);
	    	setSearcher(searcher);
	    	SortField[] sf = {
	    		new SortField(FieldNames.REVISION),
	    		new SortField(FieldNames.DFILENAME), //We use for sorting the display Filename
	    	};
	    	Sort sort = new Sort(sf);
	    	//Here we define the default field for searching.
	        QueryParser parser = new CustomQueryParser(FieldNames.CONTENTS, getAnalyzer());
	        //We will allow using a wildcard at the beginning of the expression.
	        parser.setAllowLeadingWildcard(true);
	        //The search term will not be expanded to lowercase.
	        parser.setLowercaseExpandedTerms(true);
	        Query query = parser.parse(queryLine);

	        //That's not the best idea...but currently i have not better solution for this...
	        //This is intended to get all results not only a limited number results.
	        TopDocs tmp = searcher.search(query, null, 20, sort);
		    result = searcher.search(query, null, tmp.totalHits, sort);
	    } catch (CorruptIndexException e) {
			LOGGER.error("Error: The index is corrupted: " + e);
	    } catch (IOException e) {
			LOGGER.error("Error: IOException: " + e);
		} catch (Exception e) {
			LOGGER.error("Error: Something has gone wrong: " + e);
		}
		return result;
	}

	public void setIndexDirectory(String indexDirectory) {
		this.indexDirectory = indexDirectory;
	}

	public String getIndexDirectory() {
		return indexDirectory;
	}

	public void setAnalyzer(Analyzer analyzer) {
		this.analyzer = analyzer;
	}

	public Analyzer getAnalyzer() {
		return analyzer;
	}

	public void setSearcher(Searcher searcher) {
		this.searcher = searcher;
	}

	public Searcher getSearcher() {
		return searcher;
	}

	public void setReader(IndexReader reader) {
		this.reader = reader;
	}

	public IndexReader getReader() {
		return reader;
	}

}
