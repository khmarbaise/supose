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
package com.soebes.supose.index;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriter.MaxFieldLength;
import org.apache.lucene.store.LockObtainFailedException;

import com.soebes.supose.utility.AnalyzerFactory;

/**
 * @author Karl Heinz Marbaise
 * 
 */
public class Index {
    private static Logger LOGGER = Logger.getLogger(Index.class);

    private String indexDirectory = null;
    private IndexWriter indexWriter = null;
    private Analyzer analyzer = null;

    private int mergeFactor = 10;
    private int maxBufferedDocs = 10;
    private boolean useCompoundFile = false;
    private boolean create = false;

    public Index() {
        setIndexDirectory(null);
        setAnalyzer(AnalyzerFactory.createInstance());
        setMergeFactor(1000);
        setMaxBufferedDocs(1000);
        setUseCompoundFile(false);
    }

    public IndexWriter createIndexWriter(String indexDirectory) {
        LOGGER.debug("createIndexWriter('" + indexDirectory + "')");
        setIndexDirectory(indexDirectory);
        File indexDir = new File(getIndexDirectory());
        IndexWriter writer = null;
        try {
            LOGGER.debug("Trying to create a new Index");
            if (isCreate()) {
                LOGGER.debug("Trying to create a new index (overwrite an exsting)");
                // This will create a new index. Independent if one existed
                // before.
                writer = new IndexWriter(indexDir, getAnalyzer(), true,
                        MaxFieldLength.UNLIMITED);
            } else {
                LOGGER.debug("Trying to create a new index (using an exsting)");
                // This will use an existing index or will create one if
                // no existed before.
                writer = new IndexWriter(indexDir, getAnalyzer(),
                        MaxFieldLength.UNLIMITED);
            }
            LOGGER.debug("Created new index.");
            writer.setUseCompoundFile(isUseCompoundFile());
            writer.setMergeFactor(getMergeFactor());
            writer.setMaxBufferedDocs(getMaxBufferedDocs());
            // writer.setInfoStream(System.out);
        } catch (CorruptIndexException e) {
            LOGGER.error("CorruptIndex: ", e);
        } catch (LockObtainFailedException e) {
            LOGGER.error("LockObtain: ", e);
        } catch (IOException e) {
            LOGGER.error("IOException: ", e);
        }
        return writer;
    }

    public IndexWriter getIndexWriter() {
        return indexWriter;
    }

    public void setIndexWriter(IndexWriter indexWriter) {
        this.indexWriter = indexWriter;
    }

    public String getIndexDirectory() {
        return indexDirectory;
    }

    public void setIndexDirectory(String indexDirectory) {
        this.indexDirectory = indexDirectory;
    }

    public Analyzer getAnalyzer() {
        return analyzer;
    }

    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    public int getMergeFactor() {
        return mergeFactor;
    }

    public void setMergeFactor(int mergeFactor) {
        this.mergeFactor = mergeFactor;
    }

    public int getMaxBufferedDocs() {
        return maxBufferedDocs;
    }

    public void setMaxBufferedDocs(int maxBufferedDocs) {
        this.maxBufferedDocs = maxBufferedDocs;
    }

    public boolean isUseCompoundFile() {
        return useCompoundFile;
    }

    public void setUseCompoundFile(boolean useCompoundFile) {
        this.useCompoundFile = useCompoundFile;
    }

    public boolean isCreate() {
        return create;
    }

    public void setCreate(boolean create) {
        this.create = create;
    }
}
