/**
 * The (S)ubversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
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
package com.soebes.supose.jobs;

import java.io.File;
import java.io.IOException;

import org.apache.log4j.Logger;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexWriter;
import org.quartz.JobDataMap;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.quartz.StatefulJob;

import com.soebes.supose.config.RepositoryConfiguration;
import com.soebes.supose.config.RepositoryJobConfiguration;
import com.soebes.supose.index.Index;
import com.soebes.supose.index.IndexHelper;
import com.soebes.supose.repository.Repository;
import com.soebes.supose.scan.ScanRepository;

public class RepositoryScanJob implements StatefulJob {
	private static Logger LOGGER = Logger.getLogger(RepositoryScanJob.class);

	private ScanRepository scanRepos = null;
	private RepositoryJobConfiguration jobConfig = null;

	public RepositoryScanJob () {
		LOGGER.debug("RepositoryScanJob: ctor called.");
		scanRepos = new ScanRepository();
	}

	private void subexecute (JobExecutionContext context) throws Exception {
		LOGGER.info(
				"["
			+	context.getJobDetail().getName() + "/"
			+	context.getJobDetail().getFullName()
			+	"]"
		);
		//Ok we get the JobConfiguration information.
		JobDataMap jobDataMap = context.getJobDetail().getJobDataMap();

		//Get the Repository object which has been initialized in SuposeCLI (runSchedule)
		Repository repos = (Repository) jobDataMap.get(JobDataNames.REPOSITORY);
		RepositoryConfiguration reposConfig = (RepositoryConfiguration) jobDataMap.get(JobDataNames.REPOSITORYCONFIGURATION);

		String baseDir = (String) jobDataMap.get(JobDataNames.BASEDIR);

		LOGGER.info("baseDir:" + baseDir + " URL: " + repos.getUrl() + " Name: " + reposConfig.getRepositoryName());

		//Read the job configuration, create it if it hasn't existed before...
		jobConfig = new RepositoryJobConfiguration(baseDir + File.separator + reposConfig.getRepositoryName() + ".ini", reposConfig);

		String jobIndexName = baseDir + File.separator + "index." + reposConfig.getRepositoryName();
		String resultIndexName = baseDir + File.separator + reposConfig.getResultIndex();

		LOGGER.info("Revision: " + repos.getRepository().getLatestRevision() + " FromRev:" + reposConfig.getFromRev());
		if (repos.getRepository().getLatestRevision() > reposConfig.getFromRev()) {

			long startRev = 1;
			if (jobConfig.isNewCreated()) {
				LOGGER.info("This is the first time we scan the repository.");
				startRev = reposConfig.getFromRev();
			} else {
				LOGGER.info("This is n'th time we scan the repository.");
				startRev += reposConfig.getFromRev();
			}
			long endRev = repos.getRepository().getLatestRevision();
			scanRepos.setRepository(repos);
			scanRepos.setStartRevision(startRev);
			scanRepos.setEndRevision(endRev);
			scanRepos.setName(reposConfig.getRepositoryName());

			Index index = new Index ();
			//We will allways create a new index.
			index.setCreate(true);
			IndexWriter indexWriter = index.createIndexWriter(jobIndexName);
			
			//New revision exist 'till the last scanning...
			//scan the content
			scanRepos.scan(indexWriter);

			//The last step after scanning will be to optimize this index and close it.
			try {
				indexWriter.optimize();
				indexWriter.close();
			} catch (CorruptIndexException e) {
				LOGGER.error("Corrupted index: " + e);
			} catch (IOException e) {
				LOGGER.error("IOException during closing of index: " + e);
			}

			//Merge the created index into the target index...
			IndexHelper.mergeIndex(resultIndexName, jobIndexName);

			//save the configuration file with the new revision numbers.
			reposConfig.setFromRev(endRev);
			//store the changed configuration items.

			LOGGER.info("Revision: FromRev:" + reposConfig.getFromRev() + " ToRev:" + reposConfig.getToRev());
			jobConfig.save();
		} else {
			LOGGER.info("Nothing to do, cause no changes had been made at the repository.");
			//Nothing to do, cause no new revision are existing...
		}
		LOGGER.info("RepositoryScanJob: scanning repository done...");
	}

	
	public void execute(JobExecutionContext context) throws JobExecutionException {
		try {
			subexecute(context);
		} catch (Exception e) {
			LOGGER.error("We had an unexpected Exception: " + e);
		}
	}
	
}
