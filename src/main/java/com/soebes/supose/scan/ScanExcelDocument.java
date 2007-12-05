/*
 * The (S)ubversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (C) 2007 by Karl Heinz Marbaise

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
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA *
 *
 * The License can viewed online under http://www.gnu.org/licenses/gpl.html
 * If you have any questions about the Software or about the license
 * just write an email to license@soebes.de
 *
 */
package com.soebes.supose.scan;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.StringBufferInputStream;
import java.util.HashMap;
import java.util.Map;

import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFComment;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.io.SVNRepository;

/**
 * @author Karl Heinz Marbaise
 *
 */
public class ScanExcelDocument extends AScanDocument {
	private static Logger LOGGER = Logger.getLogger(ScanExcelDocument.class);

	public ScanExcelDocument() {
	}

	public void indexDocument(SVNRepository repository, String path, long revision) {
		LOGGER.info("Scanning document");
		ByteArrayOutputStream baos = new ByteArrayOutputStream();
		Map fileProperties  = new HashMap();

		try {
			repository.getFile(path, revision, fileProperties, baos);
		} catch (SVNException e) {
			LOGGER.error("Exception happend during getting the file from the repository. " + e);
		}
		
		ByteArrayInputStream str = new ByteArrayInputStream(baos.toByteArray());

		try {
			scan(str);
		} catch (Exception e) {
			LOGGER.error("Something has gone wrong with ExcelDocuments " + e);
		}
	}

	private void scan(ByteArrayInputStream in) {
		try {
//			POIFSFileSystem fs = new POIFSFileSystem(in);
			HSSFWorkbook workBook = new HSSFWorkbook(in);
			if (workBook != null) {
				int numberOfSheets = workBook.getNumberOfSheets();
				addUnTokenizedField("xlssheets", Integer.toString(numberOfSheets));
				if (numberOfSheets > 0) {
					for (int i=0; i<numberOfSheets; i++) {
						HSSFSheet sheet = workBook.getSheetAt(i);
						if (sheet != null)
							scanSheet(workBook.getSheetName(i), sheet);
					}
				} else {
					LOGGER.info("There are no sheets in the excel file!");
				}
			} else {
				LOGGER.error("The workBook is null!");
			}
		} catch (Exception e) {
			LOGGER.error("We had an exception: " + e);
		}
	}


	private void scanSheet(String sheetName, HSSFSheet sheet) {

		StringBuffer text = new StringBuffer();
		for (int row=sheet.getFirstRowNum (); row<sheet.getLastRowNum (); row++) {
			HSSFRow or = sheet.getRow(row);

			if (or != null) {
				for (short column=or.getFirstCellNum (); column<or.getLastCellNum (); column++) {
					HSSFCell cell = or.getCell(column);
	
					HSSFComment comment = cell.getCellComment();
					if (comment != null) {
						addUnTokenizedField("xlscommentauthor", comment.getAuthor() == null ? "" : comment.getAuthor());
						addUnTokenizedField("xlscomment", comment.getString() == null ? "" : comment.getString().toString());
					}
	
					text.append(scanCell(sheetName, row, column, cell));
				}
			}
		}
		addUnTokenizedField("xlssheetname", sheetName);
		addUnTokenizedField("contents", text.toString());
	}

	private String scanCell(String sheetName, int row, short column, HSSFCell cell) {
		int celltype = cell.getCellType ();
		String result = "";
		switch (celltype) {
			case HSSFCell.CELL_TYPE_STRING:
				LOGGER.debug("CELL_TYPE_STRING: row:" + row + " column:" + column + " read.");
				result = cell.getRichStringCellValue().toString();
				break;
			case HSSFCell.CELL_TYPE_NUMERIC:
				LOGGER.debug("CELL_TYPE_NUMERIC: row:" + row + " column:" + column + " read.");
				result = Double.toString(cell.getNumericCellValue());
				break;
			case HSSFCell.CELL_TYPE_FORMULA:
				LOGGER.debug("CELL_TYPE_FORMULA: row:" + row + " column:" + column + " read.");
				result = cell.getCellFormula();
				break;
			case HSSFCell.CELL_TYPE_BOOLEAN:
				LOGGER.debug("CELL_TYPE_BOOLEAN: row:" + row + " column:" + column + " read.");
				result = Boolean.toString(cell.getBooleanCellValue());
				break;
			case HSSFCell.CELL_TYPE_ERROR:
				LOGGER.warn("Cell in Excel Sheet: " + sheetName + " produces an CELL_TYPE_ERROR");
				break;
			default:
				LOGGER.error("We got an unknown celltype back celltype=" + celltype);
				break;
		}
		return result;
	}
	
}
