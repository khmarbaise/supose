package com.soebes.supose.scan;

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.soebes.supose.FieldNames;

public class RevisionDocument {

	private Document doc;

	public RevisionDocument() {
		setDoc(new Document());
	}

	public void addTokenizedField(FieldNames fieldName, String value) {
		getDoc().add(new Field(fieldName.getValue(),  value, Field.Store.YES, Field.Index.ANALYZED));
	}
	public void addTokenizedField(String fieldName, String value) {
		getDoc().add(new Field(fieldName,  value, Field.Store.YES, Field.Index.ANALYZED));
	}
	public void addUnTokenizedField(FieldNames fieldName, String value) {
		getDoc().add(new Field(fieldName.getValue(),  value, Field.Store.YES, Field.Index.NOT_ANALYZED));
	}
	public void addUnTokenizedFieldNoStore(FieldNames fieldName, String value) {
		getDoc().add(new Field(fieldName.getValue(),  value, Field.Store.NO, Field.Index.NOT_ANALYZED));
	}
	public void addUnTokenizedFieldNoStore(String fieldName, String value) {
		getDoc().add(new Field(fieldName,  value, Field.Store.NO, Field.Index.NOT_ANALYZED));
	}
	public void addUnTokenizedField(String fieldName, String value) {
		getDoc().add(new Field(fieldName,  value, Field.Store.YES, Field.Index.NOT_ANALYZED));
	}
	public void addUnTokenizedField(FieldNames fieldName, Long value) {
		getDoc().add(new Field(fieldName.getValue(),  value.toString(), Field.Store.YES, Field.Index.NOT_ANALYZED));
	}
	public void addUnTokenizedField(FieldNames fieldName, Date value) {
		SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss.SSS");
		getDoc().add(new Field(fieldName.getValue(),  sdf.format(value), Field.Store.YES, Field.Index.NOT_ANALYZED));
	}

	public void setDoc(Document doc) {
		this.doc = doc;
	}

	public Document getDoc() {
		return doc;
	}

}
