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
