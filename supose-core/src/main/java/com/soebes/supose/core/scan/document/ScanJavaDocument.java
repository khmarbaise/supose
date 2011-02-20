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
package com.soebes.supose.core.scan.document;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Iterator;

import org.antlr.runtime.ANTLRInputStream;
import org.antlr.runtime.CommonTokenStream;
import org.apache.log4j.Logger;
import org.tmatesoft.svn.core.SVNDirEntry;
import org.tmatesoft.svn.core.SVNException;

import com.soebes.supose.core.FieldNames;
import com.soebes.supose.core.repository.Repository;
import com.soebes.supose.parse.java.JavaLexer;
import com.soebes.supose.parse.java.JavaParser;

public class ScanJavaDocument extends AScanDocument {
    private static Logger LOGGER = Logger.getLogger(ScanJavaDocument.class);

    @Override
    public void indexDocument(Repository repository, SVNDirEntry dirEntry,
            String path, long revision) {
        LOGGER.debug("Scanning Java file");
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            // This means we get the contents of the file only. No properties.
            repository.getRepository().getFile(path, revision, null, baos);
            ByteArrayInputStream str = new ByteArrayInputStream(
                    baos.toByteArray());

            scanJavaFile(str);
        } catch (SVNException e) {
            LOGGER.error("Exception by SVN: ", e);
        } catch (Exception e) {
            LOGGER.error("Something has gone wrong with JavaDocuments ", e);
        }
        LOGGER.debug("Scanning of Java file done.");
    }

    private void scanJavaFile(ByteArrayInputStream bais) throws Exception {
        ANTLRInputStream input = new ANTLRInputStream(bais);
        JavaLexer lexer = new JavaLexer(input);
        CommonTokenStream tokens = new CommonTokenStream(lexer);
        JavaParser parser = new JavaParser(tokens);

        try {
            LOGGER.debug("Parsing Java file");
            parser.compilationUnit();
            LOGGER.debug("Parsing Java file done.");
            HashMap<?, ?> methods = parser.getMethods();
            for (Iterator<?> iter = methods.keySet().iterator(); iter.hasNext();) {
                String key = (String) iter.next();
                // The value will give the information what kind of method we
                // had..
                // Not working yet...
                // String value = (String) methods.get(key);
                getDocument().addTokenizedField(FieldNames.METHODS, key);
                LOGGER.debug("Method: " + key);
            }

            // Get all comments in file...
            for (String item : lexer.getComments()) {
                getDocument().addTokenizedField(FieldNames.COMMENTS, item);
            }
        } catch (Exception e) {
            LOGGER.error("We had an error during the parsing process.", e);
        }
    }
}
