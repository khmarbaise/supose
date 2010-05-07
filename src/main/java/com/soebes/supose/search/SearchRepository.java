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

package com.soebes.supose.search;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
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
import com.soebes.supose.scan.ScanRepository;

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

	public Method getGetterByName(Class z, String name) {
		Method[] methods = z.getMethods();
		Method result = null;
		for (Method method : methods) {
			if (method.getName().toLowerCase().startsWith("get")) {
				String m = method.getName().toLowerCase();
				if (m.equals("get" + name.toLowerCase())) {
//					System.out.println("MS:" + name);
					result = method;
				}
			}
		}
		return result;
	}
	
	
	public Method getSetterByName(Class z, String name) {
		Method[] methods = z.getMethods();
		Method result = null;
		for (Method method : methods) {
			if (method.getName().toLowerCase().startsWith("set")) {
				String m = method.getName().toLowerCase();
				if (m.equals("set" + name.toLowerCase())) {
//					System.out.println("MS:" + name);
					result = method;
				}
			}
		}
		return result;
	}
	
	
	public Object callGetterByName(ResultEntry z, String name) {
		Method m = getGetterByName(z.getClass(), name);
		Object attribute = null;
		if (m == null) {
			LOGGER.fatal("Method get" + name + " not found!");
			return attribute; 
		}
		try {
			attribute = m.invoke(z);
		} catch (IllegalArgumentException e) {
			LOGGER.fatal("IllegalArgumentException", e);
		} catch (IllegalAccessException e) {
			LOGGER.fatal("IllegalAccessException", e);
		} catch (InvocationTargetException e) {
			LOGGER.fatal("InvocationTargetException", e);
		}
		return attribute;
	}
	
	
	public List<ResultEntry> getResult(String queryLine) {
		TopDocs result = getQueryResult(queryLine);
		ArrayList<ResultEntry> resultList = new ArrayList<ResultEntry>();
		
		try {
			for (int i = 0; i < result.scoreDocs.length; i++) {
		    	Document hit = getSearcher().doc(result.scoreDocs[i].doc);
				List<Field> fieldList = hit.getFields();
				ResultEntry re = new ResultEntry();
				for(int k=0; k<fieldList.size();k++) {
					Field field = (Field) fieldList.get(k);
					Method method = getSetterByName(re.getClass(), field.name());
					if (method != null) {
//						System.out.println(
//							"-> Name:" + field.name() 
//							+ " " + field.stringValue()
//							+ " [" + field.getBinaryLength() + "] "
//							+ " Method:" + method.getName());
						try {
							method.invoke(re, field.stringValue());
						} catch (IllegalArgumentException e) {
							LOGGER.fatal("IllegalArgumentException", e);
						} catch (IllegalAccessException e) {
							LOGGER.fatal("IllegalAccessException", e);
						} catch (InvocationTargetException e) {
							LOGGER.fatal("InvocationTargetException", e);
						}
					} else {
						if (field.name().startsWith(ScanRepository.DISPLAY_PROPERTIES_PREFIX)) {
							//Only properties which starts with "D" are those which should be displayed.
							//We assume we have found an field with an property.
							re.addProperty(field.name().substring(ScanRepository.DISPLAY_PROPERTIES_PREFIX.length()), field.stringValue());
						}
					}
				}
				resultList.add(re);
			}
		} catch (Exception e) {
			LOGGER.fatal("Exception", e);
		}
		return resultList;
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
	    		new SortField(FieldNames.REVISION.toString()),
	    		new SortField(FieldNames.FILENAME.toString()), //We use for sorting the filename
	    	};
	    	Sort sort = new Sort(sf);
	    	//Here we define the default field for searching.
	        QueryParser parser = new CustomQueryParser(FieldNames.CONTENTS.toString(), getAnalyzer());
	        //We will allow using a wildcard at the beginning of the expression.
	        parser.setAllowLeadingWildcard(true);
	        //The search term will not be expanded to lowercase.
	        parser.setLowercaseExpandedTerms(true);
	        Query query = parser.parse(queryLine);
	        LOGGER.info("Query: " + query.toString());
	        //That's not the best idea...but currently i have not better solution for this...
	        //This is intended to get all results not only a limited number results.
	        TopDocs tmp = searcher.search(query, null, 20, sort);
		    result = searcher.search(query, null, tmp.totalHits, sort);
	    } catch (CorruptIndexException e) {
			LOGGER.error("Error: The index is corrupted: ", e);
	    } catch (IOException e) {
			LOGGER.error("Error: IOException: ", e);
		} catch (Exception e) {
			LOGGER.error("Error: Something has gone wrong: ", e);
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
