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
package com.soebes.supose.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;

import com.soebes.supose.FieldNames;

public class CustomQueryParser extends QueryParser {
	public CustomQueryParser(String field, Analyzer analyzer) {
		super(field, analyzer);
	}

	@Override
	protected Query getFuzzyQuery(String field, String term, float minSimilarity)
		throws ParseException {
		throw new ParseException("Fuzzy queries not allowed");
	}

	/* (non-Javadoc)
	 * @see org.apache.lucene.queryParser.QueryParser#getFieldQuery(java.lang.String, java.lang.String)
	 */
	@Override
	protected Query getFieldQuery(String field, String term) throws ParseException {
		//This will handle the situation:
		// +revision:1
		if (FieldNames.REVISION.equals(field)) {
			int revision = Integer.parseInt(term);
			term = NumberUtils.pad(revision);
		}
		return super.getFieldQuery(field, term);
	}
	
	/**
	 * Special handling for the "revision" field, pads each part to match how it was
	 * indexed.
	 */
	@Override
	protected Query getRangeQuery(String field, String part1, String part2,
		boolean inclusive) throws ParseException {
		if (FieldNames.REVISION.equals(field)) {
			System.out.println("*** The revision field A:" + part1 + " B:" + part2);
			try {
				int num1 = Integer.parseInt(part1);
				int num2 = Integer.parseInt(part2);
				return new RangeQuery(
					new Term(field, NumberUtils.pad(num1)),
					new Term(field, NumberUtils.pad(num2)), 
					inclusive
				);
			} catch (NumberFormatException e) {
				throw new ParseException(e.getMessage());
			}
		}

		return super.getRangeQuery(field, part1, part2, inclusive);
	}

}
