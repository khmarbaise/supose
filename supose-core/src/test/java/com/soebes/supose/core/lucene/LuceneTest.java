/**
 * The (Su)bversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007-2011 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (c) 2007-2011 by Karl Heinz Marbaise
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
package com.soebes.supose.core.lucene;

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.assertTrue;

import java.io.IOException;
import java.util.Iterator;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.LockObtainFailedException;
import org.apache.lucene.store.RAMDirectory;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.soebes.supose.core.FieldNames;
import com.soebes.supose.core.search.CustomQueryParser;
import com.soebes.supose.core.search.NumberUtils;
import com.soebes.supose.core.utility.AnalyzerFactory;

public class LuceneTest {
    // Store the index in memory:
    private Directory directory = new RAMDirectory();
    // Now search the index:
    private IndexSearcher isearcher = null;

    private void addUnTokenizedField(Document doc, String fieldName,
            String value) {
        doc.add(new Field(fieldName, value, Field.Store.YES,
                Field.Index.NOT_ANALYZED));
    }

    private void addTokenizedField(Document doc, String fieldName, String value) {
        doc.add(new Field(fieldName, value, Field.Store.YES,
                Field.Index.ANALYZED));
    }

    @BeforeClass
    public void beforeClass() throws CorruptIndexException,
            LockObtainFailedException, IOException {
        Analyzer analyzer = AnalyzerFactory.createInstance();

        // To store an index on disk, use this instead:
        // Directory directory = FSDirectory.getDirectory("/tmp/testindex");
        IndexWriter iwriter = new IndexWriter(directory, analyzer, true,
                IndexWriter.MaxFieldLength.UNLIMITED);
        iwriter.setMaxFieldLength(25000);

        Document doc = new Document();
        String text = "This is the text to be indexed.";
        addUnTokenizedField(doc, FieldNames.REVISION.getValue(),
                NumberUtils.pad(1));
        addTokenizedField(doc, FieldNames.CONTENTS.getValue(), text);
        addUnTokenizedField(doc, FieldNames.FILENAME.getValue(),
                "/trunk/doc/testXML.doc");
        iwriter.addDocument(doc);

        doc = new Document();
        text = "This is different text.";
        addUnTokenizedField(doc, FieldNames.REVISION.getValue(),
                NumberUtils.pad(2));
        addTokenizedField(doc, FieldNames.CONTENTS.getValue(), text);
        addUnTokenizedField(doc, FieldNames.FILENAME.getValue(),
                "/tags/docs/XYZabc.java");
        iwriter.addDocument(doc);

        doc = new Document();
        text = "This is more different text.";
        addUnTokenizedField(doc, FieldNames.REVISION.getValue(),
                NumberUtils.pad(3));
        addTokenizedField(doc, FieldNames.CONTENTS.getValue(), text);
        addUnTokenizedField(doc, FieldNames.FILENAME.getValue(),
                "/tags/docs/SCMPlan.doc");
        iwriter.addDocument(doc);

        doc = new Document();
        text = "This is the third text.";
        addUnTokenizedField(doc, FieldNames.REVISION.getValue(),
                NumberUtils.pad(4));
        addTokenizedField(doc, FieldNames.CONTENTS.getValue(), text);
        addUnTokenizedField(doc, FieldNames.FILENAME.getValue(),
                "/trunk/subdir/elviraXML.doc");
        iwriter.addDocument(doc);

        iwriter.optimize();
        iwriter.close();

        isearcher = new IndexSearcher(directory);
    }

    private void printOut(Query query, String msg, TopDocs result)
            throws CorruptIndexException, IOException {
        // System.out.println("Message: " + msg);
        // System.out.println(" -> Query: " + query.toString());
        for (int i = 0; i < result.scoreDocs.length; i++) {
            // System.out.println(" -> Document[" + i + "]");
            Document hit = isearcher.doc(result.scoreDocs[i].doc);
            for (Iterator<Field> iterator = hit.getFields().iterator(); iterator
                    .hasNext();) {
                Field field = (Field) iterator.next();
                // System.out.println("   --> Field: " + field.name() + " v:" +
                // field.stringValue());
            }
        }
    }

    @AfterClass
    public void afterClass() throws IOException {
        isearcher.close();
        directory.close();
    }

    @Test
    public void testSingleAsterik() throws ParseException, IOException {
        Analyzer analyzer = AnalyzerFactory.createInstance();
        // Parse a simple query that searches for "text":
        QueryParser parser = new CustomQueryParser(FieldNames.CONTENTS,
                analyzer);
        Query query = parser.parse("+filename:/*.doc");
        TopDocs result = isearcher.search(query, null, 10);
        printOut(query, "testSingleAsterik", result);
        assertEquals(result.totalHits, 3);
    }

    @Test
    public void testSingleAsterikWithPrefix() throws ParseException,
            IOException {
        Analyzer analyzer = AnalyzerFactory.createInstance();
        // Parse a simple query that searches for "text":
        QueryParser parser = new CustomQueryParser(FieldNames.CONTENTS,
                analyzer);
        Query query = parser.parse("+filename:/trunk/*.doc");
        TopDocs result = isearcher.search(query, null, 10);
        // Iterate through the results:
        printOut(query, "testSingleAsterikWithPrefix", result);
        assertEquals(result.totalHits, 2);
    }

    @Test
    public void testMultipleAsterik() throws ParseException, IOException {
        Analyzer analyzer = AnalyzerFactory.createInstance();
        // Parse a simple query that searches for "text":
        QueryParser parser = new CustomQueryParser(FieldNames.CONTENTS,
                analyzer);
        Query query = parser.parse("+filename:/*te*.doc");
        TopDocs result = isearcher.search(query, null, 10);
        printOut(query, "testMultipleAsterik", result);
        assertEquals(result.totalHits, 1);
    }

    @Test
    public void testMultipleAsterikUppercase() throws ParseException,
            IOException {
        Analyzer analyzer = AnalyzerFactory.createInstance();
        // Parse a simple query that searches for "text":
        QueryParser parser = new CustomQueryParser(FieldNames.CONTENTS,
                analyzer);
        parser.setLowercaseExpandedTerms(false);
        Query query = parser.parse("+filename:/*SCM*.doc");
        TopDocs result = isearcher.search(query, null, 10);
        printOut(query, "testMultipleAsterikUppercase", result);
        assertTrue(result.totalHits == 1,
                "Expected to get at least one element.");
    }

    @Test(enabled = false)
    public void testMultipleAsterikLowerCase() throws ParseException,
            IOException {
        Analyzer analyzer = AnalyzerFactory.createInstance();
        // Parse a simple query that searches for "text":
        QueryParser parser = new CustomQueryParser(FieldNames.CONTENTS,
                analyzer);
        parser.setLowercaseExpandedTerms(false);
        Query query = parser.parse("+filename:/*scm*.doc");
        TopDocs result = isearcher.search(query, null, 10);
        printOut(query, "testMultipleAsterikLowerCase", result);
        assertTrue(result.totalHits == 1,
                "Expected to get at least one element.");
    }

    @Test
    public void testSingleAsterikRestrictionToRevisionRange()
            throws ParseException, IOException {
        Analyzer analyzer = AnalyzerFactory.createInstance();
        // Parse a simple query that searches for "text":
        QueryParser parser = new CustomQueryParser(FieldNames.CONTENTS,
                analyzer);
        parser.setLowercaseExpandedTerms(true);
        Query query = parser.parse("+filename:/*.doc +revision:[1 TO 3]");
        TopDocs result = isearcher.search(query, null, 10);
        printOut(query, "testSingleAsterikRestrictionToRevisionRange", result);
        assertTrue(result.totalHits == 2, "Expected to get two elements.");
    }

    @Test
    public void testSingleAsterikRestrictionToDifferentRevisionRange()
            throws ParseException, IOException {
        Analyzer analyzer = AnalyzerFactory.createInstance();
        // Parse a simple query that searches for "text":
        QueryParser parser = new CustomQueryParser(FieldNames.CONTENTS,
                analyzer);
        parser.setLowercaseExpandedTerms(true);
        Query query = parser.parse("+filename:/*.doc +revision:[1 TO 2]");
        TopDocs result = isearcher.search(query, null, 10);
        printOut(query, "testSingleAsterikRestrictionToDifferentRevisionRange",
                result);
        assertTrue(result.totalHits == 1, "Expected to get two elements.");
    }

    @Test
    public void testSingleRevision() throws ParseException, IOException {
        Analyzer analyzer = AnalyzerFactory.createInstance();
        // Parse a simple query that searches for "text":
        QueryParser parser = new CustomQueryParser(FieldNames.CONTENTS,
                analyzer);
        parser.setLowercaseExpandedTerms(true);
        Query query = parser.parse("+revision:1");
        TopDocs result = isearcher.search(query, null, 10);
        printOut(query, "testTheFifthSearch]", result);
        assertTrue(result.totalHits == 1, "Expected to get two elements.");
    }

}
