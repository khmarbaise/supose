/**
 * The (Su)bversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
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
package com.soebes.supose;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.testng.annotations.BeforeSuite;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.SVNURL;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.io.SVNRepositoryFactory;
import org.tmatesoft.svn.core.wc.SVNWCUtil;
import org.tmatesoft.svn.core.wc.admin.SVNAdminClient;

import com.soebes.supose.index.Index;
import com.soebes.supose.repository.Repository;
import com.soebes.supose.scan.ScanRepository;


/**
 * This will initialize a repository and load the contents from
 * an existing dump file which contains particular test cases.
 * 
 * The next part is to scan the repository and create an index
 * which will be used for the query tests.
 * 
 * @author Karl Heinz Marbaise
 *
 */
public class InitRepository extends TestBase {
	private static Logger LOGGER = Logger.getLogger(InitRepository.class);

	private SVNURL repositoryURL = null;

	private static ScanRepository scanRepository = new ScanRepository();

	/**
	 * The first step is to create a test repository which
	 * will be used to test the functionality of the scanning
	 * indexing and search process.
	 * @throws SVNException 
	 * @throws FileNotFoundException 
	 * @throws SVNException 
	 * @throws FileNotFoundException 
	 */
	@BeforeSuite
	public void beforeSuite() throws FileNotFoundException, SVNException {
		createRepository();
		scanRepos();
	}

	public void createRepository() throws SVNException, FileNotFoundException {
		LOGGER.info("Using the following directory: " + getRepositoryDirectory());
		repositoryURL = SVNRepositoryFactory.createLocalRepository(new File(getRepositoryDirectory()), false, true);
		LOGGER.info("Integration Test Repository created.");
		LOGGER.info("The URL:" + repositoryURL.toString());
		
		LOGGER.info("Start loading the dump file into the repository.");
		//Create the path to the repos.dump file which is located 
		//in the src/test/resources directory.
		String dumpFile = getTestResourcesDirectory() 
			+ File.separatorChar + "repos.dump";
		SVNAdminClient admin = new SVNAdminClient((ISVNAuthenticationManager)null, null);
		admin.doLoad(new File(getRepositoryDirectory()), new FileInputStream(dumpFile));
		LOGGER.info("Start verifying the repository.");
		admin.doVerify(new File(getRepositoryDirectory()));
	}
	
	public void scanRepos() throws SVNException {
		Index index = new Index ();
		//We will create a new one if --create is given on command line
		//otherwise we will append to the existing index.
		Analyzer analyzer = new StandardAnalyzer();		
		index.setAnalyzer(analyzer);
		//For the test we allways create the index.
		index.setCreate(true);
		IndexWriter indexWriter = index.createIndexWriter(getIndexDirectory());

		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(
			"", 
			""
		);
		String repositoryDir = getRepositoryDirectory();
		SVNURL url = SVNURL.fromFile(new File(repositoryDir));
		Repository repository = new Repository("file://" + url.getURIEncodedPath(), authManager);

		scanRepository.setRepository(repository);

		//We start with the revision which is given on the command line.
		//If it is not given we will start with revision 1.
		scanRepository.setStartRevision(1); 
		//We will scan the repository to the current HEAD of the repository.
		scanRepository.setEndRevision(-1);

		LOGGER.info("Scanning started.");
		scanRepository.scan(indexWriter);
		LOGGER.info("Scanning ready.");

		try {
			indexWriter.optimize();
			indexWriter.close();
		} catch (CorruptIndexException e) {
			LOGGER.error("CorruptIndexException: Error during optimization of index: ", e);
		} catch (IOException e) {
			LOGGER.error("IOException: Error during optimization of index: ", e);
		}
	}
	
}
