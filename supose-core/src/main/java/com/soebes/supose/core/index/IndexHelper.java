/**
 * The (Su)bversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007-2016 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (c) 2007-2016 by Karl Heinz Marbaise
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
package com.soebes.supose.core.index;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.store.FSDirectory;

/**
 * @author Karl Heinz Marbaise
 *
 */
public class IndexHelper {
    private static Logger LOGGER = Logger.getLogger(IndexHelper.class);

    /**
     * Merge a given index area to a single destination index.
     *
     * @param destination
     *            The destination to which the source index will be merged.
     * @param source
     *            The index which will be merged to the destination index.
     */
    public static void mergeIndex(File destination, File source) {
        ArrayList<File> sourceList = new ArrayList<File>();
        sourceList.add(source);
        mergeIndex(destination, sourceList);
    }

    /**
     * Merge all given indexes together to a single index.
     *
     * @param destination
     *            This will define the destination directory of the index where
     *            all other indexes will be merged to.
     * @param indexList
     *            This is the list of indexes which are merged into the
     *            destination index.
     */
    public static void mergeIndex(File destination, List<File> indexList) {
        LOGGER.debug("We are trying to merge indexes to the destination: "
                + destination);
        Index index = new Index();
        // We assume an existing index...
        index.setCreate(false);
        IndexWriter indexWriter = index.createIndexWriter(destination.getAbsolutePath());

        try {
            LOGGER.info("Merging of indexes started.");
            FSDirectory[] fsDirs = new FSDirectory[indexList.size()];
            for (int i = 0; i < indexList.size(); i++) {
                fsDirs[i] = FSDirectory.getDirectory(indexList.get(i));
            }
            indexWriter.addIndexesNoOptimize(fsDirs);
            indexWriter.optimize();
            indexWriter.close();
            LOGGER.info("Merging of indexes succesfull.");
        } catch (Exception e) {
            LOGGER.error("Something wrong during merge of index: ", e);
        }
    }

}
