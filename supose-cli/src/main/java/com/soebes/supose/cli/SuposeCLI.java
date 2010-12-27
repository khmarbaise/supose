/**
 * The (Su)bversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007, 2008, 2009, 2010 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (c) 2007, 2008, 2009, 2010 by Karl Heinz Marbaise
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
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.commons.cli2.CommandLine;
import org.apache.commons.cli2.Group;
import org.apache.commons.cli2.HelpLine;
import org.apache.commons.cli2.OptionException;
import org.apache.commons.cli2.util.HelpFormatter;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang.WordUtils;
import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.soebes.supose.FieldNames;
import com.soebes.supose.config.ConfigurationRepositories;
import com.soebes.supose.config.RepositoryConfiguration;
import com.soebes.supose.config.RepositoryFactory;
import com.soebes.supose.index.IndexHelper;
import com.soebes.supose.jobs.JobDataNames;
import com.soebes.supose.jobs.JobSchedulerListener;
import com.soebes.supose.jobs.RepositoryScanJob;
import com.soebes.supose.repository.Repository;
import com.soebes.supose.scan.ScanRepository;
import com.soebes.supose.scan.ScanSingleRepository;
import com.soebes.supose.search.ResultEntry;
import com.soebes.supose.search.SearchRepository;
import com.thoughtworks.xstream.XStream;

/**
 * This will define the Command Line Version of SupoSE.
 * 
 * @author Karl Heinz Marbaise
 *
 */
public class SuposeCLI {
	private static Logger LOGGER = Logger.getLogger(SuposeCLI.class);
	
	private static final int HELP_OPTION_DESCRIPTION_INDENT = 30;

	private static SuposeCommandLine suposecli = new SuposeCommandLine();
	private static CommandLine commandLine = null;

	public static void main(String[] args) throws SVNException {
		try {
			commandLine = suposecli.doParseArgs(args);
		} catch (OptionException e) {
			System.err.println("Error: Unexpected Option given on command line. " + e);
			System.exit(1);
		}

		if (commandLine.hasOption(suposecli.getGlobalOptionH())) {
			printHelp();
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
	
	private static void printHelp() {
		StringBuffer help = new StringBuffer();
		Group suposeOptions = suposecli.getSuposeOptions();
		List<?> helpLines = suposeOptions.helpLines(0, HelpFormatter.DEFAULT_DISPLAY_USAGE_SETTINGS, null);
		String descriptionPad = StringUtils.repeat(" ", HELP_OPTION_DESCRIPTION_INDENT);
		int descriptionIndent = HelpFormatter.DEFAULT_FULL_WIDTH - HELP_OPTION_DESCRIPTION_INDENT;
		for (Iterator<?> i = helpLines.iterator(); i.hasNext();) {
			HelpLine helpLine = (HelpLine) i.next();
			String usage = helpLine.usage(HelpFormatter.DEFAULT_LINE_USAGE_SETTINGS, null);
			String usageWrapped = WordUtils.wrap(usage, HelpFormatter.DEFAULT_FULL_WIDTH);
			help.append(usageWrapped).append("\n");
			String description = helpLine.getDescription();
			if (description != null) {
				String descriptionWrapped = WordUtils.wrap(description, descriptionIndent, "\n" + descriptionPad, true);
				help.append(descriptionPad).append(descriptionWrapped).append("\n");
			}
		}
		System.out.println(help);
	}

	/**
	 * This will do the command argument extraction and give the parameter to
	 * the scanRepository class which will do the real repository scan.
	 * @param scanCommand The command line.
	 * @throws SVNException 
	 */
	private static void runScan(ScanCommand scanCommand) throws SVNException {
		String url = scanCommand.getURL(commandLine);
		long fromRev = scanCommand.getFromRev(commandLine);
		long toRev = scanCommand.getToRev(commandLine);
		String indexDirectory = scanCommand.getIndexDir(commandLine);
		boolean create = scanCommand.getCreate(commandLine);
		String username = scanCommand.getUsername(commandLine);
		String password = scanCommand.getPassword(commandLine);

		ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(
			username, 
			password
		);

		LOGGER.info("Start with scanning of revisions.");
		
		
		ScanRepository scanRepository = new ScanRepository();

		CLIInterceptor interceptor = new CLIInterceptor();
		scanRepository.registerScanInterceptor(interceptor);
		
		CLILogEntryInterceptor logEntryInterceptor = new CLILogEntryInterceptor();
		scanRepository.registerLogEntryInterceptor(logEntryInterceptor);

		CLIChangeSetInterceptor changeSetInterceptor = new CLIChangeSetInterceptor();
		scanRepository.registerChangeSetInterceptor(changeSetInterceptor);
		
		long blockNumber = ScanSingleRepository.scanFullRepository(scanRepository, url, fromRev, indexDirectory, create, authManager);

		LOGGER.info("Scanning of revisions done");

		ScanSingleRepository.mergeIndexesAndCleanUp(indexDirectory, blockNumber);
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

		long startTime = System.currentTimeMillis();
		IndexHelper.mergeIndex(destination, indexList);
		long stopTime = System.currentTimeMillis();
		long ms = (stopTime-startTime);
		long seconds = ms / 1000;
		System.out.println("This has taken " + seconds + " seconds.");
	}

	private static List<FieldNames> getDisplayFields(List<String> cliFields) {
		//Here we translate the search fields into the fields which are displayed.
		List<FieldNames> cliDisplayFields = new ArrayList<FieldNames>();
		for (String fieldName : cliFields) {
			FieldNames fn = FieldNames.valueOf(fieldName.toUpperCase());
			cliDisplayFields.add(fn);
		}
		return cliDisplayFields;
	}

	private static void runSearch(SearchCommand searchCommand) {
		LOGGER.info("Searching started...");
		String indexDirectory = searchCommand.getIndexDir(commandLine);
		String queryLine = searchCommand.getQuery(commandLine);
		boolean xml = searchCommand.getXML(commandLine);
		List<String> cliFields = searchCommand.getFields(commandLine);
		
		SearchRepository searchRepository = new SearchRepository(indexDirectory);

		List<ResultEntry> result = searchRepository.getResult(queryLine);
		
		if (xml) {
			XStream xstream = new XStream();
			xstream.alias("entry", ResultEntry.class);
			System.out.println(xstream.toXML(result));
		} else {
			System.out.println("Query: '" + queryLine + "'");
			
			if (cliFields.size() > 0) {
				for(int i=0; i<cliFields.size(); i++) {
					System.out.print("Field[" + i + "]=" + cliFields.get(i) + " ");
				}
			} else {
				cliFields = new ArrayList<String>();
				//If nothings is given on command line we have to define 
				//default fields which will be printed
				cliFields.add(FieldNames.REVISION.getValue());
				cliFields.add(FieldNames.FILENAME.getValue());
				cliFields.add(FieldNames.PATH.getValue());
				cliFields.add(FieldNames.KIND.getValue());
			}
			List<FieldNames> cliDisplayFields = getDisplayFields(cliFields);

			if (result == null) {
				System.out.println("Somethings has gone wrong. Check the log file output!");
				return;
			}
			System.out.println("Total Hits: " + result.size());
			
			long count = 1;
			for (ResultEntry item : result) {
				System.out.printf("%6d: ", count);
				for (FieldNames fn : cliDisplayFields) {
					if (fn.equals(FieldNames.PROPERTIES)) {
						//Properties will be put into separate lines
						System.out.println("");
						Map<String, String> properties = item.getProperties();
						for (Map.Entry<String, String> prop : properties.entrySet()) {
							System.out.println(" --> K:" + prop.getKey() + " V:" + prop.getValue());
						}
					} else {
						Object attribute = searchRepository.callGetterByName(item, fn.getValue());
						System.out.print(fn.name() + ":" + attribute + " ");
					}
				}
				System.out.println("");
				count++;
			}
		}

		IndexReader reader = searchRepository.getReader();
		try {
			reader.close();
		} catch (IOException e) {
			LOGGER.error("Error during closing of the index happened: ", e);
		}
	}

}
