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
import java.io.IOException;
import java.io.InputStream;
import java.io.StringWriter;
import java.util.Calendar;

import org.apache.log4j.Logger;
import org.pdfbox.exceptions.CryptographyException;
import org.pdfbox.exceptions.InvalidPasswordException;
import org.pdfbox.pdmodel.PDDocument;
import org.pdfbox.pdmodel.PDDocumentInformation;
import org.pdfbox.util.PDFTextStripper;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;

import com.soebes.supose.FieldNames;
import com.soebes.supose.repository.Repository;

/**
 * @author Karl Heinz Marbaise
 *
 */
public class ScanPDFDocument extends AScanDocument {
	private static Logger LOGGER = Logger.getLogger(ScanPDFDocument.class);

	public ScanPDFDocument() {
	}

	@Override
	public void indexDocument(Repository repository, SVNDirEntry dirEntry, String path, long revision) {
		LOGGER.info("Scanning document");
		
		try {
			ByteArrayOutputStream baos = new ByteArrayOutputStream();
			//This means we get the contents of the file only. No properties.
			repository.getRepository().getFile(path, revision, null, baos);
			ByteArrayInputStream str = new ByteArrayInputStream(baos.toByteArray());
			addContent(str);
		} catch (SVNException e) {
			LOGGER.error("Exception by SVN: " + e);
		} catch (Exception e) {
			LOGGER.error("Exception during extraction of contents. " + e);
		}
	}
	
	
    /**
     * This will add the contents to the lucene document.
     *
     * @param document The document to add the contents to.
     * @param is The stream to get the contents from.
     * @param documentLocation The location of the document, used just for debug messages.
     *
     * @throws IOException If there is an error parsing the document.
     * 
     * !!! THIS CODE PART IS "STOLEN" from LucenePDFDocument of PDFBox!!!
     */
    private void addContent(InputStream is) throws IOException
    {
    	PDFTextStripper stripper = null;
        PDDocument pdfDocument = null;
        try
        {
            pdfDocument = PDDocument.load( is );

            if( pdfDocument.isEncrypted() )
            {
                //Just try using the default password and move on
                pdfDocument.decrypt("");
            }

            //create a writer where to append the text content.
            StringWriter writer = new StringWriter();
            if (stripper == null) {
                stripper = new PDFTextStripper();
            } else {
                stripper.resetEngine();
            }
            stripper.writeText( pdfDocument, writer );

            // Note: the buffer to string operation is costless;
            // the char array value of the writer buffer and the content string
            // is shared as long as the buffer content is not modified, which will
            // not occur here.
            String contents = writer.getBuffer().toString();

            // Add the tag-stripped contents as a Reader-valued Text field so it will
            // get tokenized and indexed.
			addTokenizedField(FieldNames.CONTENTS, contents);

            PDDocumentInformation info = pdfDocument.getDocumentInformation();
            if (info != null) {
            	//We save the supplemental fields of the PDF into special named
            	//fields, to make them searchable.
            	addUnTokenizedField(FieldNames.PDFAUTHOR, info.getAuthor() == null ? "" : info.getAuthor());
            	addUnTokenizedField(FieldNames.PDFCREATIONDATE, info.getCreationDate() == null ? Calendar.getInstance() : info.getCreationDate());
            	addUnTokenizedField(FieldNames.PDFCREATOR, info.getCreator() == null ? "" : info.getCreator());
            	addUnTokenizedField(FieldNames.PDFKEYWORDS, info.getKeywords() == null ? "" : info.getKeywords());
            	addUnTokenizedField(FieldNames.PDFMODIFICATIONDATE, info.getModificationDate() == null ? Calendar.getInstance() : info.getModificationDate());
            	addUnTokenizedField(FieldNames.PDFPRODUCER, info.getProducer() == null ? "" : info.getProducer());
            	addUnTokenizedField(FieldNames.PDFSUBJECT, info.getSubject() == null ? "" : info.getSubject());
            	addUnTokenizedField(FieldNames.PDFTITLE, info.getTitle() == null ? "" : info.getTitle());
            	addUnTokenizedField(FieldNames.PDFTRAPPED, info.getTrapped() == null ? "" : info.getTrapped());
            	LOGGER.info("PDF Document: " + info.toString());
            }
//            int summarySize = Math.min( contents.length(), 500 );
//            String summary = contents.substring( 0, summarySize );
//            // Add the summary as an UnIndexed field, so that it is stored and returned
//            // with hit documents for display.
//            addUnindexedField( document, "summary", summary );

        } catch( CryptographyException e ) {
			LOGGER.error("Error: decrypting has failed! " + e);
        } catch( InvalidPasswordException e ) {
            //they didn't supply a password and the default of "" was wrong.
			LOGGER.error("Error: The document is encrypted and can't be decrypted! We will not index this document! " + e);
        } finally {
            if (pdfDocument != null) {
                pdfDocument.close();
            }
        }
    }

	
}
