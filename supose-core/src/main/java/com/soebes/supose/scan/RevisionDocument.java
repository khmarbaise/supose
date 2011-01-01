/**
 * The (Su)bversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007-2011 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (c) 2007-2011 by Karl Heinz Marbaise
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

import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;

import com.soebes.supose.FieldNames;

/**
 * @author Karl Heinz Marbaise
 * 
 */
public class RevisionDocument {

    private Document doc;

    public RevisionDocument() {
        setDoc(new Document());
    }

    public void addTokenizedField(FieldNames fieldName, String value) {
        getDoc().add(
                new Field(fieldName.getValue(), value, Field.Store.YES,
                        Field.Index.ANALYZED));
    }

    public void addTokenizedField(String fieldName, String value) {
        getDoc().add(
                new Field(fieldName, value, Field.Store.YES,
                        Field.Index.ANALYZED));
    }

    public void addUnTokenizedField(FieldNames fieldName, String value) {
        getDoc().add(
                new Field(fieldName.getValue(), value, Field.Store.YES,
                        Field.Index.NOT_ANALYZED));
    }

    public void addUnTokenizedFieldNoStore(FieldNames fieldName, String value) {
        getDoc().add(
                new Field(fieldName.getValue(), value, Field.Store.NO,
                        Field.Index.NOT_ANALYZED));
    }

    public void addUnTokenizedFieldNoStore(String fieldName, String value) {
        getDoc().add(
                new Field(fieldName, value, Field.Store.NO,
                        Field.Index.NOT_ANALYZED));
    }

    public void addUnTokenizedField(String fieldName, String value) {
        getDoc().add(
                new Field(fieldName, value, Field.Store.YES,
                        Field.Index.NOT_ANALYZED));
    }

    public void addUnTokenizedField(FieldNames fieldName, Long value) {
        getDoc().add(
                new Field(fieldName.getValue(), value.toString(),
                        Field.Store.YES, Field.Index.NOT_ANALYZED));
    }

    public void addUnTokenizedField(FieldNames fieldName, Date value) {
        SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy hh:mm:ss.SSS");
        getDoc().add(
                new Field(fieldName.getValue(), sdf.format(value),
                        Field.Store.YES, Field.Index.NOT_ANALYZED));
    }

    public void setDoc(Document doc) {
        this.doc = doc;
    }

    public Document getDoc() {
        return doc;
    }

}
