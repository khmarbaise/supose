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
package com.soebes.supose.core.scan;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.tmatesoft.svn.core.SVNAuthenticationException;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNLogEntry;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;

import com.soebes.supose.config.filter.Filtering;
import com.soebes.supose.core.index.Index;
import com.soebes.supose.core.index.IndexHelper;
import com.soebes.supose.core.repository.Repository;
import com.soebes.supose.core.utility.AnalyzerFactory;

public class ScanSingleRepository {
	private static Logger LOGGER = Logger.getLogger(ScanSingleRepository.class);


	public static long scanFullRepository(
		ScanRepository scanRepository,
		String url, 
		long fromRev, 
		String indexDirectory, 
		boolean create, 
		ISVNAuthenticationManager authManager,
		Filtering filtering 
	) throws SVNException {
		Repository repository = new Repository(url, authManager);

		boolean firstTime = true;

		// Assuming we have fromRev: 1
		// toRev: HEAD (-1)
		long latestRevision = repository.getRepository().getLatestRevision();

		//Define number per round
		long deltaRevisions = 10000;
		long blockNumber = 0;

		for (long revisions = fromRev; revisions <latestRevision; revisions += deltaRevisions) {
			long startRevision = revisions;
			long endRevision = revisions + deltaRevisions - 1;
			if (endRevision > latestRevision) {
				endRevision = latestRevision;
			}

			blockNumber = revisions / deltaRevisions;

			//BLOCK BEGIN
			if (create) {
				if (firstTime) {
					firstTime = false;
				} else {
					create = false;
				}
			}

			scanRepository.setLogEntries(new ArrayList<SVNLogEntry>());
			scanRepository.setRepository(repository);
	
			//We start with the revision which is given on the command line.
			//If it is not given we will start with revision 1.
			scanRepository.setStartRevision(startRevision); 
			//We will scan the repository to the current HEAD of the repository.
			scanRepository.setEndRevision(endRevision);
			
			scanRepository.setFiltering(filtering);
	
			ScanSingleRepository.scanSingleRepos(scanRepository, indexDirectory + blockNumber, create);
			//BLOCK END
		}
		return blockNumber;
	}
	
	/**
	 * @param fromRev
	 * @param toRev
	 * @param indexDirectory
	 * @param create
	 * @param repository
	 */
	public static void scanSingleRepos(ScanRepository scanRepository, String indexDirectory, boolean create) {
		// BLOCK ANFANG

		Index index = new Index ();
		//We will create a new one if --create is given on command line
		//otherwise we will append to the existing index.
		Analyzer analyzer = AnalyzerFactory.createInstance();		
		index.setAnalyzer(analyzer);

		index.setCreate(create);
		IndexWriter indexWriter = index.createIndexWriter(indexDirectory);

		try {
			LOGGER.info("Scanning started.");
			scanRepository.scan(indexWriter);
			LOGGER.info("Scanning ready.");
			try {
				long startTime = System.currentTimeMillis();
				LOGGER.info("Index optimizing started.");
				indexWriter.optimize();
				indexWriter.close();
				long stopTime = System.currentTimeMillis();
				LOGGER.info("Index optimizing done.");
				long ms = (stopTime-startTime);
				long seconds = ms / 1000;
				LOGGER.info("The Index optimizing has taken " + seconds + " seconds.");
			} catch (CorruptIndexException e) {
				LOGGER.error("CorruptIndexException: Error during optimization of index: ", e);
			} catch (IOException e) {
				LOGGER.error("IOException: Error during optimization of index: ", e);
			}
		} catch (SVNAuthenticationException svnae) {
			LOGGER.error("Authentication has failed. ", svnae);
		} catch (Exception e) {
			LOGGER.error("Something unexpected went wrong ", e);
		}
	}

	public static void mergeIndexesAndCleanUp(String indexDirectory, long blockNumber) {
		ArrayList<String> indexList = new ArrayList<String>();
		
		//Create the list of indexes
		for (long blockCount = 0; blockCount <= blockNumber; blockCount++) {
			indexList.add(indexDirectory + blockCount);
		}


		LOGGER.info("Merging indexes togehter..");
		
		long startTime = System.currentTimeMillis();

		IndexHelper.mergeIndex(indexDirectory, indexList);

		long stopTime = System.currentTimeMillis();

		LOGGER.info("Merging indexes togehter done.");
		
		long ms = (stopTime-startTime);
		long seconds = ms / 1000;
		LOGGER.info("Merging the indexes has taken " + seconds + " seconds.");
		
		startTime = System.currentTimeMillis();
		//Delete all the created folder after the merging
		for (String directory : indexList) {
			File dir = new File(directory);
			try {
				LOGGER.info("Deleting " + directory);
				FileUtils.deleteDirectory(dir);
				LOGGER.info("Deleting " + directory + " done.");
			} catch (IOException e) {
				LOGGER.error("IOException during deletion of " + directory, e);
			}
		}
		stopTime = System.currentTimeMillis();
		LOGGER.info("The folder deleting has taken " + ((stopTime-startTime)/1000) + " seconds");
	}


}
