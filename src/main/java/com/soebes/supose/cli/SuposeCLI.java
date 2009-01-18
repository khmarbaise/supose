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
package com.soebes.supose.cli;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.Iterator;
import java.util.List;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.util.HelpFormatter;
import org.apache.log4j.Logger;
import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.index.CorruptIndexException;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.queryParser.QueryParser;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.HitCollector;
import org.apache.lucene.search.Hits;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.Searcher;
import org.apache.lucene.search.Sort;
import org.apache.lucene.search.SortField;
import org.apache.lucene.search.TopDocs;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.soebes.supose.FieldNames;
import com.soebes.supose.config.ConfigurationRepositories;
import com.soebes.supose.config.RepositoryConfiguration;
import com.soebes.supose.config.RepositoryFactory;
import com.soebes.supose.index.Index;
import com.soebes.supose.index.IndexHelper;
import com.soebes.supose.jobs.JobDataNames;
import com.soebes.supose.jobs.JobSchedulerListener;
import com.soebes.supose.jobs.RepositoryScanJob;
import com.soebes.supose.repository.Repository;
import com.soebes.supose.scan.ScanRepository;
import com.soebes.supose.search.CustomQueryParser;

/**
 * This will define the Command Line Version of SupoSE.
 * 
 * @author Karl Heinz Marbaise
 *
 */
public class SuposeCLI {
	private static Logger LOGGER = Logger.getLogger(SuposeCLI.class);

	private static SuposeCommandLine suposecli = new SuposeCommandLine();
	private static ScanRepository scanRepository = new ScanRepository();
	private static CommandLine commandLine = null;

	public static void main(String[] args) {
		try {
			commandLine = suposecli.doParseArgs(args);
		} catch (OptionException e) {
			System.err.println("Error: Unexpected Option given on command line. " + e);
			System.exit(1);
		}

		if (commandLine.hasOption(suposecli.getGlobalOptionH())) {
	        final StringWriter out = new StringWriter();
	        final HelpFormatter helpFormatter = new HelpFormatter();
	        helpFormatter.setGroup(suposecli.getSuposeOptions());
	        helpFormatter.setPrintWriter(new PrintWriter(out));
	        helpFormatter.printHelp();
	        System.out.println(out);
	        System.exit(0);
		} else if (commandLine.hasOption(suposecli.getScanCommand())) {
			runScan(suposecli.getScliScanCommand());
		} else if (commandLine.hasOption(suposecli.getSearchCommand())) {
			runSearch(suposecli.getScliSearchCommand());
		} else if (commandLine.hasOption(suposecli.getMergeCommand())) {
			runMerge(suposecli.getScliMergeCommand());
		} else if (commandLine.hasOption(suposecli.getScheduleCommand())) {
			runSchedule(suposecli.getScliScheduleCommand());
		} else {
			System.err.println("Error: You should define either scan, search, merge or schedule as command or use -H option to get further detailed information.");
			System.exit(1);
		}
	}

	/**
	 * This will do the command argument extraction and give the parameter to
	 * the scanRepository class which will do the real repository scan.
	 * @param scanCommand The command line.
	 */
	private static void runScan(ScanCommand scanCommand) {
		String url = scanCommand.getURL(commandLine);
		long fromRev = scanCommand.getFromRev(commandLine);
		long toRev = scanCommand.getToRev(commandLine);
		String indexDirectory = scanCommand.getIndexDir(commandLine);
		boolean create = scanCommand.getCreate(commandLine);
		String username = scanCommand.getUsername(commandLine);
		String password = scanCommand.getPassword(commandLine);

		Index index = new Index ();
		//We will create a new one if --create is given on command line
		//otherwise we will append to the existing index.
		Analyzer analyzer = new StandardAnalyzer();		
		index.setAnalyzer(analyzer);
		index.setCreate(create);
		IndexWriter indexWriter = index.createIndexWriter(indexDirectory);

		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(
			username, 
			password
		);
		Repository repository = new Repository(url, authManager);

		scanRepository.setRepository(repository);

		//We start with the revision which is given on the command line.
		//If it is not given we will start with revision 1.
		scanRepository.setStartRevision(fromRev); 
		//We will scan the repository to the current HEAD of the repository.
		scanRepository.setEndRevision(toRev);

		LOGGER.info("Scanning started.");
		scanRepository.scan(indexWriter);
		LOGGER.info("Scanning ready.");

		try {
			indexWriter.optimize();
			indexWriter.close();
		} catch (CorruptIndexException e) {
			System.err.println("CorruptIndexException: Error during optimization of index: " + e);
		} catch (IOException e) {
			System.err.println("IOException: Error during optimization of index: " + e);
		}

	}


	private static void runSchedule(ScheduleCommand scheduleCommand) {
		String configurationFile = scheduleCommand.getConfiguration(commandLine);
		String configurationBaseDir = scheduleCommand.getConfBaseDir(commandLine);

		System.out.println("Configuration file: " + configurationFile);
		int scheduledJobs = 0;
		Scheduler scheduler = null;
        try {
        	// Grab the Scheduler instance from the Factory 
        	scheduler = StdSchedulerFactory.getDefaultScheduler();

            // and start it off
            scheduler.start();

            JobSchedulerListener schedulerListener = new JobSchedulerListener();

            scheduler.addSchedulerListener(schedulerListener);

            ConfigurationRepositories confRepos = new ConfigurationRepositories(configurationFile);
            LOGGER.info("We have " + confRepos.getNames().length + " repositories.");
            for(int i=0; i<confRepos.getNames().length; i++) {
            	String repositoryName = confRepos.getNames()[i];
            	RepositoryConfiguration reposConfig = confRepos.getRepositoryConfiguration(repositoryName);
            	Repository repository = RepositoryFactory.createInstance(reposConfig);
            	if (!repository.validConnection()) {
            		//Connection has failed.
            		System.err.println("The repository " + repository.getUrl() + " can't be connected!");
            		//Do not make a job from a non connectable repository.
            		continue;
            	}
            	LOGGER.info("The repository " + repositoryName + " is ready for scanning.");
            	JobDetail jobDetail = new JobDetail(repositoryName, null, RepositoryScanJob.class);
            	
            	jobDetail.getJobDataMap().put(JobDataNames.REPOSITORY, repository);
            	jobDetail.getJobDataMap().put(JobDataNames.REPOSITORYCONFIGURATION, reposConfig);
            	jobDetail.getJobDataMap().put(JobDataNames.BASEDIR, configurationBaseDir);

            	CronTrigger cronTrigger1 = null;
            	String cronExpression = "";
            	//If the use would like to configure different scheduling trigger
            	if (reposConfig.existCron()) {
            		cronExpression = reposConfig.getCron();
            	} else {
            		//The default every minue...
            		cronExpression = "0 * * ? * *";
            	}

            	try {
            		cronTrigger1 = new CronTrigger(
        				"SupoSE." + repositoryName,
        				Scheduler.DEFAULT_GROUP,
        				cronExpression
            		);
            	} catch (Exception e) {
            		System.err.println("Error for cronTrigger wrong expression for cron: " + e);
            		System.exit(1);
            	}            
            	
            	scheduler.scheduleJob(jobDetail, cronTrigger1);
            	scheduledJobs++;
            }

            //If we haven't started any job we shutdown...
            if (scheduledJobs == 0) {
            	System.err.println("We couldn't start any scan job so we are dying...");
            	scheduler.shutdown();
            }
            System.out.println("End with CTRL-C");
        } catch (SchedulerException se) {
            se.printStackTrace();
        } finally {
//        	try {
//        		if (scheduler != null) {
//        			scheduler.shutdown();
//        		}
//			} catch (Exception e) {
//				System.err.println("Shutdown has failed during finally block: " + e);
//			}
        }
	}

	/**
	 * The merge command is used to merge two or more indexes together.
	 * 
	 * @param mergeCommand
	 */
	private static void runMerge(MergeCommand mergeCommand) {
		List<String> indexList = mergeCommand.getIndex(commandLine);
		String destination = mergeCommand.getDestination(commandLine);

		for(int i=0; i<indexList.size(); i++) {
			System.out.print("Index[" + i + "]=" + indexList.get(i) + " ");
		}
		System.out.println("");
		System.out.println("Destination: " + destination);

		IndexHelper.mergeIndex(destination, indexList);
	}

	private static void runSearch(SearchCommand searchCommand) {
		LOGGER.info("Searching started...");
		String indexDirectory = searchCommand.getIndexDir(commandLine);
		String queryLine = searchCommand.getQuery(commandLine);
		List<String> cliFields = searchCommand.getFields(commandLine);

		System.out.println("Query: '" + queryLine + "'");

		for(int i=0; i<cliFields.size(); i++) {
			System.out.print("Field[" + i + "]=" + cliFields.get(i) + " ");
		}
		System.out.println("");

	    IndexReader reader = null;
	    
	    try {
	    	
	    	reader = IndexReader.open(indexDirectory);
	    	
	    	Searcher searcher = new IndexSearcher(reader);
	    	Analyzer analyzer = new StandardAnalyzer();
//	    	Analyzer analyzer = new KeywordAnalyzer();

	    	//Sort primary based on Revision
	    	//secondary by filename.
	    	SortField[] sf = {
	    		new SortField(FieldNames.REVISION),
	    		new SortField(FieldNames.FILENAME),
	    	};
	    	Sort sort = new Sort(sf);
	    	//Here we define the default field for searching.
	        QueryParser parser = new CustomQueryParser(FieldNames.CONTENTS, analyzer);
	        //We will allow using a wildcard at the beginning of the expression.
	        parser.setAllowLeadingWildcard(true);
	        //The search term will not be expanded to lowercase.
	        parser.setLowercaseExpandedTerms(false);
	        Query query = parser.parse(queryLine);

	        //That's not the best idea...but currently i have not better solution for this...
	        TopDocs tmp = searcher.search(query, null, 20, sort);
		    TopDocs result = searcher.search(query, null, tmp.totalHits, sort);

		    System.out.println("Query analyzer:" + query.toString());
			System.out.println("Total Hits: " + result.totalHits);
		    for (int i = 0; i < result.scoreDocs.length; i++) {
		    	Document hit = searcher.doc(result.scoreDocs[i].doc);
				List<Field> fieldList = hit.getFields();
				System.out.print((i+1) + ". ");
				for(int k=0; k<fieldList.size();k++) {
					Field field = (Field) fieldList.get(k);
					if ((cliFields.size() > 0) && cliFields.contains(field.name())) {
						System.out.print(field.name() + ": " + field.stringValue() + " ");
					} else {
						if (FieldNames.FILENAME.equals(field.name())) {
							System.out.print("F:" + field.stringValue() + " ");
						}
						if (FieldNames.REVISION.equals(field.name())) {
							long rev = Long.parseLong(field.stringValue());
							System.out.print("R:" + rev + " ");
						}
						if (FieldNames.KIND.equals(field.name())) {
							System.out.print("K:" + field.stringValue() + " ");
						}
					}
				}
				System.out.println("");
			}
			
	    } catch (CorruptIndexException e) {
			System.err.println("Error: The index is corrupted: " + e);
	    } catch (IOException e) {
			System.err.println("Error: IOException: " + e);
		} catch (Exception e) {
			System.err.println("Error: Something has gone wrong: " + e);
		} finally {
			try {
				reader.close();
			} catch (IOException e) {
				System.err.println("Error: IOException during close(): " + e);
			}
		}
	}

}
