package com.soebes.supose.search;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryParser.ParseException;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.PhraseQuery;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.RangeQuery;
import org.apache.lucene.search.spans.SpanNearQuery;
import org.apache.lucene.search.spans.SpanTermQuery;

public class CustomQueryParser extends QueryParser {
	public CustomQueryParser(String field, Analyzer analyzer) {
		super(field, analyzer);
	}

	@Override
	protected Query getFuzzyQuery(String field, String term, float minSimilarity)
		throws ParseException {
		throw new ParseException("Fuzzy queries not allowed");
	}

	/**
	 * Special handling for the "id" field, pads each part to match how it was
	 * indexed.
	 */
	@Override
	protected Query getRangeQuery(String field, String part1, String part2,
		boolean inclusive) throws ParseException {
		if ("id".equals(field)) {
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

		if ("special".equals(field)) {
			System.out.println(part1 + "->" + part2);
			return new RangeQuery("*".equals(part1) ? null : new Term("field",
					part1),
					"*".equals(part2) ? null : new Term("field", part2),
					inclusive);
		}

		return super.getRangeQuery(field, part1, part2, inclusive);
	}

	@Override
	protected final Query getWildcardQuery(String field, String termStr)
			throws ParseException {
		throw new ParseException("Wildcard not allowed");
	}

	/**
	 * Replace PhraseQuery with SpanNearQuery to force in-order phrase matching
	 * rather than reverse.
	 */
	@Override
	protected Query getFieldQuery(String field, String queryText, int slop)
			throws ParseException {
		// let QueryParser's implementation do the analysis
		Query orig = super.getFieldQuery(field, queryText, slop);

		if (!(orig instanceof PhraseQuery)) {
			return orig;
		}

		PhraseQuery pq = (PhraseQuery) orig;
		Term[] terms = pq.getTerms();
		SpanTermQuery[] clauses = new SpanTermQuery[terms.length];
		for (int i = 0; i < terms.length; i++) {
			clauses[i] = new SpanTermQuery(terms[i]);
		}

		SpanNearQuery query = new SpanNearQuery(clauses, slop, true);

		return query;
	}

}
