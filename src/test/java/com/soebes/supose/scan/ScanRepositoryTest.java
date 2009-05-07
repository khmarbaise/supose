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
package com.soebes.supose.scan;

import static org.testng.Assert.assertEquals;

import org.apache.log4j.Logger;
import org.apache.lucene.search.TopDocs;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.soebes.supose.TestBase;
import com.soebes.supose.search.SearchRepository;

public class ScanRepositoryTest extends TestBase {
	private static Logger LOGGER = Logger.getLogger(ScanRepositoryTest.class);

	private static SearchRepository searchRepository = new SearchRepository();
	
	@BeforeClass
	public void beforeClass() {
		searchRepository.setIndexDirectory(getIndexDirectory());
	}

	
	@Test(enabled = false)
	public void testQueryForFilenameOnlyWhichCurrentlyDoesNotWork() {
		TopDocs result = searchRepository.getQueryResult("+filename:f1.txt");
	    assertEquals(result.totalHits, 0);
	}

	@Test
	public void testQueryForFilenameWithPrefixedWildcardTextFiles() {
		TopDocs result = searchRepository.getQueryResult("+filename:*.txt");
	    assertEquals(result.totalHits, 6);
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

}
