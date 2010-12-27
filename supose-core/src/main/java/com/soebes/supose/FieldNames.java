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
public enum FieldNames {
	CONTENTS("contents"),
	REVISION("revision"),
	KIND("kind"),
	NODE("node"),
	PATH("path"),
	FILENAME("filename"),
	AUTHOR("author"),
	MESSAGE("message"),
	DATE("date"),
	FROM("from"),
	FROMREV("fromrev"),
	REPOSITORY("repository"),
	REPOSITORYUUID("repositoryuuid"),
	SIZE("size"),
	TAG("tag"),
	MAVENTAG("maventag"),
	SUBVERSIONTAG("subversiontag"),
	BRANCH("branch"),
	XLSSHEET("xlssheet"),
	XLSSHEETNAME("xlssheetname"),
	XLSCOMMENT("xlscomment"),
	XLSCOMMENTAUTHOR("xlscommentauthor"),
	XLSAUTHOR("xlsauthor"),
	PDFAUTHOR("pdfauthor"),
	PDFCREATIONDATE("pdfcreationdate"),
	PDFCREATOR("pdfcreator"),
	PDFKEYWORDS("pdfkeywords"),
	PDFMODIFICATIONDATE("pdfmodificationdate"),
	PDFPRODUCER("pdfproducer"),
	PDFSUBJECT("pdfsubject"),
	PDFTITLE("pdftitle"),
	PDFTRAPPED("pdftrapped"),
	METHODS("methods"),
	COMMENTS("comments"),

	PROPERTIES("properties"); //This is not a real entry only used for displaying on command line?

	private String value;

	public String getValue() {
		return this.value;
	}

	private FieldNames(String value) {
		this.value = value;
	}
	
	public String toString() {
		return this.value;
	}
}
