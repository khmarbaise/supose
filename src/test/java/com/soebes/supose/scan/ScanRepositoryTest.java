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

import static org.testng.Assert.assertEquals;
import static org.testng.Assert.*;

import java.io.IOException;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.search.TopDocs;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.soebes.supose.FieldNames;
import com.soebes.supose.TestBase;
import com.soebes.supose.search.SearchRepository;

public class ScanRepositoryTest extends TestBase {
	private static Logger LOGGER = Logger.getLogger(ScanRepositoryTest.class);

	private static SearchRepository searchRepository = new SearchRepository();
	
	@BeforeClass
	public void beforeClass() {
		searchRepository.setIndexDirectory(getIndexDirectory());
	}
	@AfterClass
	public void afterClass() throws IOException {
		IndexReader reader = searchRepository.getReader();
		reader.close();
	}

	@Test
	public void testQueryForFilenameOnly() {
		TopDocs result = searchRepository.getQueryResult("+filename:f1.txt");
	    assertEquals(result.totalHits, 4);
	}

	@Test
	public void testQueryForFilenameOnlyUppercase() {
		TopDocs result = searchRepository.getQueryResult("+filename:F1.txt");
	    assertEquals(result.totalHits, 4);
	}
	
	@Test
	public void testQueryForFilenameMixedCaseTestPPT() {
		TopDocs result = searchRepository.getQueryResult("+filename:testPPT.*");
	    assertEquals(result.totalHits, 2);
	}

	@Test
	public void testQueryForFilenameLowercaseTestPPT() {
		TopDocs result = searchRepository.getQueryResult("+filename:testppt.*");
	    assertEquals(result.totalHits, 2);
	}


	@Test
	public void testQueryForFilenameWithPrefixedWildcardTextFiles() {
		TopDocs result = searchRepository.getQueryResult("+filename:*.txt");
	    assertEquals(result.totalHits, 8);
	}
	
	@Test
	public void testQueryForFilenameWithPrefixedWildcardExcelFiles() {
		TopDocs result = searchRepository.getQueryResult("+filename:*.xls");
	    assertEquals(result.totalHits, 2);
	}

	@Test
	public void testQueryForFilenameWithPrefixedWildcardExcel2007Files() {
		TopDocs result = searchRepository.getQueryResult("+filename:*.xlsx");
	    assertEquals(result.totalHits, 2);
	}
	
	@Test
	public void testQueryForPathMixedCase() {
		TopDocs result = searchRepository.getQueryResult("+path:/*/B_*");
	    assertEquals(result.totalHits, 6);
	}
	@Test
	public void testQueryForPathLowerCase() {
		TopDocs result = searchRepository.getQueryResult("+path:/*/b_*");
	    assertEquals(result.totalHits, 6);
	}

	@Test
	public void testQueryForTermForExcelWorksheet() {
		TopDocs result = searchRepository.getQueryResult("+contents:\"Sample Excel Worksheet\"");
	    assertEquals(result.totalHits, 2);
	}
	
	@Test
	public void testQueryForTermForExcelWorksheetCombination() {
		TopDocs result = searchRepository.getQueryResult("+contents:\"Sample Excel Worksheet\" +filename:*.xls");
	    assertEquals(result.totalHits, 1);
	}
	
	@Test
	public void testQueryForTermFromWord() {
		TopDocs result = searchRepository.getQueryResult("+contents:\"Sample Word\"");
	    assertEquals(result.totalHits, 2);
	}
	
	@Test
	public void testQueryForTermFromWordCombination() {
		TopDocs result = searchRepository.getQueryResult("+contents:\"Sample Word\" +filename:*.doc");
	    assertEquals(result.totalHits, 1);
	}

	@Test
	public void testQueryForTermOfPowerPoint() {
		TopDocs result = searchRepository.getQueryResult("+contents:\"Sample Powerpoint\"");
	    assertEquals(result.totalHits, 2);
	}
	
	@Test
	public void testQueryOpenOfficeODP() {
		//Das ist ein Test mit OpenOffice 3.0 auf Windows XP
		TopDocs result = searchRepository.getQueryResult("+contents:\"OpenOffice 3.0 auf Windows XP\"");
	    assertEquals(result.totalHits, 1);
	}

	@Test
	public void testQueryOpenOfficeODS() {
		//Test Mit OpenOffice
		//3.0
		//Windows XP
		TopDocs result = searchRepository.getQueryResult("+contents:\"Test Mit OpenOffice 3.0 Windows XP\"");
	    assertEquals(result.totalHits, 1);
	}

	@Test
	public void testQueryOpenOfficeODT() {
		//This is a Test
		//In OpenOffice
		//3.0
		//Windows XP
		TopDocs result = searchRepository.getQueryResult("+contents:\"This is a Test In OpenOffice 3.0 Windows XP\"");
	    assertEquals(result.totalHits, 1);
	}

	@Test
	public void testQueryArchiveContentsTAR() {
		TopDocs result = searchRepository.getQueryResult("+contents:\"This file is contined in a archive\"");
	    assertEquals(result.totalHits, 1);
	}

	@Test
	public void testQueryArchiveContentsZIP() {
		TopDocs result = searchRepository.getQueryResult("+contents:\"This file is contents of a zip archive\"");
	    assertEquals(result.totalHits, 1);
	}

	@Test
	public void testQueryForTagsOfAllKind() {
		TopDocs result = searchRepository.getQueryResult("+tag:*");
		//This will be 4 entries which are coming from the tag entry
		//and one entry which is coming from the maventag.
		assertEquals(result.totalHits, 7);
	}

	@Test
	public void testQueryForMavenTags() {
		TopDocs result = searchRepository.getQueryResult("+maventag:*");
		assertEquals(result.totalHits, 4);
	}

	@Test
	public void testQueryForTagsOnly() {
		TopDocs result = searchRepository.getQueryResult("+tag:* -maventag:* -subversiontag:*");
		//This has to be result of the tags only.
		assertEquals(result.totalHits, 1);
	}

	@Test
	public void testQueryForSubversionTagsOnly() {
		TopDocs result = searchRepository.getQueryResult("+subversiontag:*");
		//This has to be result into a single entry for the tag.
		assertEquals(result.totalHits, 2);
	}

	@Test
	public void testQueryForBranchPath() {
		TopDocs result = searchRepository.getQueryResult("+path:*/branches/*");
		assertEquals(result.totalHits, 7);
	}

	@Test
	public void testQueryForBranches() {
		TopDocs result = searchRepository.getQueryResult("+branch:*");
		//We have only a single entry here
		assertEquals(result.totalHits, 1);
	}
	
	@Test
	public void testQueryForKind() {
		TopDocs result = searchRepository.getQueryResult("+kind:D");
		//We have only a single entry here
		assertEquals(result.totalHits, 3);
	}

	@Test
	public void testQueryForNode() {
		TopDocs result = searchRepository.getQueryResult("+node:dir");
		//We have only a single entry here
		assertEquals(result.totalHits, 12);
	}
	
	private Field searchForField (Document hit, String name) {
		Field result = null;
		List<Field> fieldList = hit.getFields();
		for (Field field : fieldList) {
			if (field.name().equals(name)) {
				result = field;
			}
		}
		return result;
	}
	@Test
	public void testQueryForDeletedTag() throws CorruptIndexException, IOException {
		TopDocs result = searchRepository.getQueryResult("+path:*/tags/* +kind:d");
		assertEquals(result.totalHits, 1);

		Document hit = searchRepository.getSearcher().doc(result.scoreDocs[0].doc);
		List<Field> fieldList = hit.getFields();

		//This entry is not allowed to have a filename entry!!!
		Field fileNameField  = searchForField(hit, FieldNames.FILENAME);
		assertNotNull(fileNameField, "We have expected to find the " + FieldNames.FILENAME + " field.");
		assertEquals(fileNameField.stringValue().length(), 0, "We have expected to get an empty filename field for a tag which is a directory.");

		Field pathField  = searchForField(hit, FieldNames.PATH);
		assertNotNull(pathField, "We have expected to find the " + FieldNames.PATH + " field.");
		assertEquals(pathField.stringValue(), "/project1/tags/release-0.0.1/", "We have expected to get an particular path value");
	}
	
}
