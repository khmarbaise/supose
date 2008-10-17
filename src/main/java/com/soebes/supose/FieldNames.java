/*
 * The (S)ubversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007, 2008 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (c) 2007, 2008 by Karl Heinz Marbaise

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
package com.soebes.supose;


/**
 * This class defines all available field names.
 * Only the fields which will be automatically created.
 * For every property will be created an entry too, but
 * this can't be defined here.
 * 
 * @author Karl Heinz Marbaise
 *
 */
public final class FieldNames {

	public final static String CONTENTS = "contents";
	public final static String REVISION = "revision";
	public final static String KIND = "kind";
	public final static String NODE = "node";
	public final static String PATH = "path";
	public final static String FILENAME = "filename";
	public final static String AUTHOR = "author";
	public final static String MESSAGE = "message";
	public final static String DATE = "date";
	public final static String FROM = "from";
	public final static String FROMREV = "fromrev";
	public final static String REPOSITORY = "repository";
	public final static String REPOSITORYUUID = "repositoryuuid";
	public final static String SIZE = "size";

	public final static String XLSSHEETS = "xlssheets";
	public final static String XLSSHEETNAME = "xlssheetname";
	public final static String XLSCOMMENT = "xlscomment";
	public final static String XLSCOMMENTAUTHOR = "xlscommentauthor";

	public final static String PDFAUTHOR = "pdfauthor";
	public final static String PDFCREATIONDATE = "pdfcreationdate";
	public final static String PDFCREATOR = "pdfcreator";
	public final static String PDFKEYWORDS = "pdfkeywords";
	public final static String PDFMODIFICATIONDATE = "pdfmodificationdate";
	public final static String PDFPRODUCER = "pdfproducer";
	public final static String PDFSUBJECT = "pdfsubject";
	public final static String PDFTITLE = "pdftitle";
	public final static String PDFTRAPPED = "pdftrapped";
	
	//For all kinds of programming languages (first test).
	public final static String METHODS = "methods";
	public final static String COMMENTS = "comments"; //Any kind of comment in programming languages.
	
}
