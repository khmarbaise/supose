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
import static org.testng.Assert.assertNotNull;

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
import com.soebes.supose.search.ResultEntry;
import com.soebes.supose.search.SearchRepository;

public class SearchRepositoryTest extends TestBase {
	private static Logger LOGGER = Logger.getLogger(SearchRepositoryTest.class);

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
		List<ResultEntry> result = searchRepository.getResult("+filename:f1.txt");
	    assertEquals(result.size(), 4);
	}

	@Test
	public void testQueryForFilenameOnlyUppercase() {
		List<ResultEntry> result = searchRepository.getResult("+filename:F1.txt");
	    assertEquals(result.size(), 4);
	}
	
	@Test
	public void testQueryForFilenameMixedCaseTestPPT() {
		List<ResultEntry> result = searchRepository.getResult("+filename:testPPT.*");
	    assertEquals(result.size(), 2);
	}

	@Test
	public void testQueryForFilenameLowercaseTestPPT() {
		List<ResultEntry> result = searchRepository.getResult("+filename:testppt.*");
	    assertEquals(result.size(), 2);
	}

	@Test
	public void testQueryForFilenameWithPrefixedWildcardTextFiles() {
		List<ResultEntry> result = searchRepository.getResult("+filename:*.txt");
	    assertEquals(result.size(), 8);
	}
	
	@Test
	public void testQueryForFilenameWithPrefixedWildcardExcelFiles() {
		List<ResultEntry> result = searchRepository.getResult("+filename:*.xls");
	    assertEquals(result.size(), 2);
	}

	@Test
	public void testQueryForFilenameWithPrefixedWildcardExcel2007Files() {
		List<ResultEntry> result = searchRepository.getResult("+filename:*.xlsx");
	    assertEquals(result.size(), 2);
	}
	
	@Test
	public void testQueryForPathMixedCase() {
		List<ResultEntry> result = searchRepository.getResult("+path:/*/B_*");
	    assertEquals(result.size(), 6);
	}
	@Test
	public void testQueryForPathLowerCase() {
		List<ResultEntry> result = searchRepository.getResult("+path:/*/b_*");
	    assertEquals(result.size(), 6);
	}

	@Test
	public void testQueryForTermForExcelWorksheet() {
		List<ResultEntry> result = searchRepository.getResult("+contents:\"Sample Excel Worksheet\"");
	    assertEquals(result.size(), 2);
	}
	
	@Test
	public void testQueryForTermForExcelWorksheetCombination() {
		List<ResultEntry> result = searchRepository.getResult("+contents:\"Sample Excel Worksheet\" +filename:*.xls");
	    assertEquals(result.size(), 1);
	}
	
	@Test
	public void testQueryForTermFromWord() {
		List<ResultEntry> result = searchRepository.getResult("+contents:\"Sample Word\"");
	    assertEquals(result.size(), 2);
	}
	
	@Test
	public void testQueryForTermFromWordCombination() {
		List<ResultEntry> result = searchRepository.getResult("+contents:\"Sample Word\" +filename:*.doc");
	    assertEquals(result.size(), 1);
	}

	@Test
	public void testQueryForTermOfPowerPoint() {
		List<ResultEntry> result = searchRepository.getResult("+contents:\"Sample Powerpoint\"");
	    assertEquals(result.size(), 2);
	}
	
	@Test
	public void testQueryOpenOfficeODP() {
		//Das ist ein Test mit OpenOffice 3.0 auf Windows XP
		List<ResultEntry> result = searchRepository.getResult("+contents:\"OpenOffice 3.0 auf Windows XP\"");
	    assertEquals(result.size(), 1);
	}

	@Test
	public void testQueryOpenOfficeODS() {
		//Test Mit OpenOffice
		//3.0
		//Windows XP
		List<ResultEntry> result = searchRepository.getResult("+contents:\"Test Mit OpenOffice 3.0 Windows XP\"");
	    assertEquals(result.size(), 1);
	}

	@Test
	public void testQueryOpenOfficeODT() {
		//This is a Test
		//In OpenOffice
		//3.0
		//Windows XP
		List<ResultEntry> result = searchRepository.getResult("+contents:\"This is a Test In OpenOffice 3.0 Windows XP\"");
	    assertEquals(result.size(), 1);
	}

	@Test
	public void testQueryArchiveContentsTAR() {
		List<ResultEntry> result = searchRepository.getResult("+contents:\"This file is contined in a archive\"");
	    assertEquals(result.size(), 1);
	}

	@Test
	public void testQueryArchiveContentsZIP() {
		List<ResultEntry> result = searchRepository.getResult("+contents:\"This file is contents of a zip archive\"");
	    assertEquals(result.size(), 1);
	}

	@Test
	public void testQueryForTagsOfAllKind() {
		List<ResultEntry> result = searchRepository.getResult("+tag:*");
		//This will be 4 entries which are coming from the tag entry
		//and one entry which is coming from the maventag.
		assertEquals(result.size(), 7);
	}

	@Test
	public void testQueryForMavenTags() {
		List<ResultEntry> result = searchRepository.getResult("+maventag:*");
		assertEquals(result.size(), 4);
	}

	@Test
	public void testQueryForTagsOnly() {
		List<ResultEntry> result = searchRepository.getResult("+tag:* -maventag:* -subversiontag:*");
		//This has to be result of the tags only.
		assertEquals(result.size(), 1);
	}

	@Test
	public void testQueryForSubversionTagsOnly() {
		List<ResultEntry> result = searchRepository.getResult("+subversiontag:*");
		//This has to be result into a single entry for the tag.
		assertEquals(result.size(), 2);
	}

	@Test
	public void testQueryForBranchPath() {
		List<ResultEntry> result = searchRepository.getResult("+path:*/branches/*");
		assertEquals(result.size(), 7);
	}

	@Test
	public void testQueryForBranches() {
		List<ResultEntry> result = searchRepository.getResult("+branch:*");
		//We have only a single entry here
		assertEquals(result.size(), 1);
	}
	
	@Test
	public void testQueryForKind() {
		List<ResultEntry> result = searchRepository.getResult("+kind:D");
		//We have only a single entry here
		assertEquals(result.size(), 3);
	}

	@Test
	public void testQueryForNode() {
		List<ResultEntry> result = searchRepository.getResult("+node:dir");
		//We have only a single entry here
		assertEquals(result.size(), 12);
	}
	
	@Test
	public void testQueryForDeletedTag() throws CorruptIndexException, IOException {
		List<ResultEntry> result = searchRepository.getResult("+path:*/tags/* +kind:d");
		assertEquals(result.size(), 1);

		assertEquals(result.get(0).getField(FieldNames.FILENAME).length(), 0, "We have expected to get an empty filename field for a tag which is a directory.");

		assertNotNull(result.get(0).getField(FieldNames.PATH), "We have expected to find the " + FieldNames.PATH + " field.");
		assertEquals(result.get(0).getField(FieldNames.PATH), "/project1/tags/release-0.0.1/", "We have expected to get an particular path value");
	}
	
}
