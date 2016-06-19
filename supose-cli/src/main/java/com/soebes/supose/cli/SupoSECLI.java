/**
 * The (Su)bversion Re(po)sitory (S)earch (E)ngine (SupoSE for short).
 *
 * Copyright (c) 2007-2016 by SoftwareEntwicklung Beratung Schulung (SoEBeS)
 * Copyright (c) 2007-2016 by Karl Heinz Marbaise
 *
 * This program is free software; you can redistribute it and/or modify it under
 * the terms of the GNU General Public License as published by the Free Software
 * Foundation; either version 2 of the License, or (at your option) any later
 * version.
 *
 * This program is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or FITNESS
 * FOR A PARTICULAR PURPOSE. See the GNU General Public License for more
 * details.
 *
 * You should have received a copy of the GNU General Public License along with
 * this program; if not, write to the Free Software Foundation, Inc., 51
 * Franklin St, Fifth Floor, Boston, MA 02110-1301 USA
 *
 * The License can viewed online under http://www.gnu.org/licenses/gpl.html If
 * you have any questions about the Software or about the license just write an
 * email to license@soebes.de
 */

package com.soebes.supose.cli;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import org.apache.log4j.Logger;
import org.apache.lucene.index.IndexReader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.quartz.CronTrigger;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.quartz.impl.StdSchedulerFactory;
import org.tmatesoft.svn.core.SVNException;
import org.tmatesoft.svn.core.auth.ISVNAuthenticationManager;
import org.tmatesoft.svn.core.wc.SVNWCUtil;

import com.beust.jcommander.MissingCommandException;
import com.beust.jcommander.ParameterException;
import com.soebes.supose.cli.SupoSECommandLine.SupoSECommands;
import com.soebes.supose.config.filter.FilterFile;
import com.soebes.supose.config.filter.Filtering;
import com.soebes.supose.config.filter.model.Filter;
import com.soebes.supose.core.FieldNames;
import com.soebes.supose.core.config.ConfigurationRepositories;
import com.soebes.supose.core.config.RepositoryConfiguration;
import com.soebes.supose.core.config.RepositoryFactory;
import com.soebes.supose.core.index.IndexHelper;
import com.soebes.supose.core.jobs.JobDataNames;
import com.soebes.supose.core.jobs.JobSchedulerListener;
import com.soebes.supose.core.jobs.RepositoryScanJob;
import com.soebes.supose.core.repository.Repository;
import com.soebes.supose.core.scan.ScanRepository;
import com.soebes.supose.core.scan.ScanSingleRepository;
import com.soebes.supose.core.search.ResultEntry;
import com.soebes.supose.core.search.SearchRepository;
import com.thoughtworks.xstream.XStream;

/**
 * This will define the Command Line Version of SupoSE.
 *
 * @author Karl Heinz Marbaise
 *
 */
public class SupoSECLI {
    private static Logger LOGGER = Logger.getLogger(SupoSECLI.class);

    private int returnCode = 0;

    public SupoSECLI() {
    }

    /**
     * This is the <code>supose --version</code>
     */
    private void printVersion() {
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getResourceAsStream("/version.properties"));
        } catch (IOException e) {
            LOGGER.error("Can't read the version.properties file.", e);
            return;
        }
        String version = properties.getProperty("version");
        String buildNumber = properties.getProperty("buildNumber");
        System.out.println("Version:" + version);
        System.out.println("Build Number:" + buildNumber);
    }

    public void run(String[] args) throws SVNException {
        setReturnCode(0);

        SupoSECommandLine commands = null;
        try {
            commands = new SupoSECommandLine(args);
        } catch (MissingCommandException e) {
            LOGGER.warn("");
            LOGGER.warn("It looks like you used a wrong command .");
            LOGGER.warn("");
            LOGGER.warn("Message: " + e.getMessage());
            LOGGER.warn("");
            LOGGER.warn("To get help about all existing commands please type:");
            LOGGER.warn("");
            LOGGER.warn("    supose --help");
            LOGGER.warn("");
            LOGGER.warn("If you like to get help about a particular command:");
            LOGGER.warn("");
            LOGGER.warn("    supose command --help");
            LOGGER.warn("");
            return;

        } catch (ParameterException e) {
            LOGGER.warn("");
            LOGGER.warn("It looks like you used a wrong command or used wrong options or a combination of this.");
            LOGGER.warn("");
            LOGGER.warn("Message: " + e.getMessage());
            LOGGER.warn("");
            LOGGER.warn("To get help about all existing commands please type:");
            LOGGER.warn("");
            LOGGER.warn("    supose --help");
            LOGGER.warn("");
            LOGGER.warn("If you like to get help about a particular command:");
            LOGGER.warn("");
            LOGGER.warn("    supose command --help");
            LOGGER.warn("");
            return;
        }

        SupoSECommands command = commands.getCommand();
        if (commands.isHelpForCommand()) {
            commands.getCommander().usage(command.getCommandName());
            return;
        }

        if (commands.getMainCommand().isVersion()) {
            printVersion();
            return;
        }

        if (command == null || commands.getMainCommand().isHelp() || (args.length == 0)) {
            commands.getCommander().usage();
            return;
        }

        switch (command) {
        case MERGE:
            runMerge(commands.getMergeCommand());
            break;
        case SCAN:
            runScan(commands.getScanCommand());
            break;
        case SEARCH:
            runSearch(commands.getSearchCommand());
            break;
        case SCHEDULE:
            runSchedule(commands.getScheduleCommand());
            break;
        default:
            LOGGER.error("Unknown command in switch.");
            setReturnCode(1);
            break;
        }

    }

    /**
     * This will do the command argument extraction and give the parameter to
     * the scanRepository class which will do the real repository scan.
     *
     * @param scanCommand
     *            The command line.
     * @throws SVNException
     */
    private void runScan(ScanCommand scanCommand) throws SVNException {
        if (scanCommand.getUri() == null) {
            LOGGER.error("You have to give an url!");
            return;
        }
        long fromRev = scanCommand.getFromRev();
        long toRev = scanCommand.getToRev();
        String indexDirectory = scanCommand.getIndexName();
        boolean create = scanCommand.isCreateIndex();
        String username = scanCommand.getUsername();
        String password = scanCommand.getPassword();

        ISVNAuthenticationManager authManager = SVNWCUtil.createDefaultAuthenticationManager(username, password);

        LOGGER.info("Start with scanning of revisions.");

        ScanRepository scanRepository = new ScanRepository();

        CLIInterceptor interceptor = new CLIInterceptor();
        scanRepository.registerScanInterceptor(interceptor);

        CLILogEntryInterceptor logEntryInterceptor = new CLILogEntryInterceptor();
        scanRepository.registerLogEntryInterceptor(logEntryInterceptor);

        CLIChangeSetInterceptor changeSetInterceptor = new CLIChangeSetInterceptor();
        scanRepository.registerChangeSetInterceptor(changeSetInterceptor);

        InputStream filter = SupoSECLI.class.getResourceAsStream("/filter.xml");
        Filter filterConfiguration = null;
        try {
            filterConfiguration = FilterFile.getFilter(filter);
        } catch (FileNotFoundException e) {
            LOGGER.error("FileNotFoundException", e);
        } catch (IOException e) {
            LOGGER.error("IOException", e);
        } catch (XmlPullParserException e) {
            LOGGER.error("XmlPullParserException", e);
        }

        Filtering filtering = new Filtering(filterConfiguration);
        long blockNumber = ScanSingleRepository.scanFullRepository(scanRepository, scanCommand.getUri().toString(),
                fromRev, indexDirectory, create, authManager, filtering);

        LOGGER.info("Scanning of revisions done");

        ScanSingleRepository.mergeIndexesAndCleanUp(indexDirectory, blockNumber);
    }

    private void runSchedule(ScheduleCommand scheduleCommand) {
        File configurationFile = scheduleCommand.getConfigurationFile();
        File configurationBaseDir = scheduleCommand.getConfigurationBase();

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

            ConfigurationRepositories confRepos = new ConfigurationRepositories(configurationFile.getAbsolutePath());
            LOGGER.info("We have " + confRepos.getNames().length + " repositories.");
            for (int i = 0; i < confRepos.getNames().length; i++) {
                String repositoryName = confRepos.getNames()[i];
                RepositoryConfiguration reposConfig = confRepos.getRepositoryConfiguration(repositoryName);
                Repository repository = RepositoryFactory.createInstance(reposConfig);
                if (!repository.validConnection()) {
                    // Connection has failed.
                    System.err.println("The repository " + repository.getUrl() + " can't be connected!");
                    // Do not make a job from a non connectable repository.
                    continue;
                }

                LOGGER.info("The repository " + repositoryName + " is ready for scanning.");
                JobDetail jobDetail = new JobDetail(repositoryName, null, RepositoryScanJob.class);

                jobDetail.getJobDataMap().put(JobDataNames.REPOSITORY, repository);
                jobDetail.getJobDataMap().put(JobDataNames.REPOSITORYCONFIGURATION, reposConfig);
                jobDetail.getJobDataMap().put(JobDataNames.BASEDIR, configurationBaseDir);

                CronTrigger cronTrigger1 = null;
                String cronExpression = "";
                // If the use would like to configure different scheduling
                // trigger
                if (reposConfig.existCron()) {
                    cronExpression = reposConfig.getCron();
                } else {
                    // The default every minue...
                    cronExpression = "0 * * ? * *";
                }

                try {
                    cronTrigger1 = new CronTrigger("SupoSE." + repositoryName, Scheduler.DEFAULT_GROUP, cronExpression);
                } catch (Exception e) {
                    System.err.println("Error for cronTrigger wrong expression for cron: " + e);
                    System.exit(1);
                }

                scheduler.scheduleJob(jobDetail, cronTrigger1);
                scheduledJobs++;
            }

            // If we haven't started any job we shutdown...
            if (scheduledJobs == 0) {
                System.err.println("We couldn't start any scan job so we are dying...");
                scheduler.shutdown();
            }
            System.out.println("End with CTRL-C");
        } catch (SchedulerException se) {
            se.printStackTrace();
        } finally {
            // try {
            // if (scheduler != null) {
            // scheduler.shutdown();
            // }
            // } catch (Exception e) {
            // System.err.println("Shutdown has failed during finally block: " +
            // e);
            // }
        }
    }

    /**
     * The merge command is used to merge two or more indexes together.
     *
     * @param mergeCommand
     */
    private void runMerge(MergeCommand mergeCommand) {
        List<File> indexList = mergeCommand.getIndexes();
        File destination = mergeCommand.getDestinationIndex();

        for (int i = 0; i < indexList.size(); i++) {
            System.out.print("Index[" + i + "]=" + indexList.get(i) + " ");
        }
        System.out.println("");
        System.out.println("Destination: " + destination);

        long startTime = System.currentTimeMillis();
        IndexHelper.mergeIndex(destination, indexList);
        long stopTime = System.currentTimeMillis();
        long ms = (stopTime - startTime);
        long seconds = ms / 1000;
        System.out.println("This has taken " + seconds + " seconds.");
    }

    /**
     * The search command.
     *
     * @param searchCommand
     */
    private void runSearch(SearchCommand searchCommand) {
        LOGGER.info("Searching started...");
        String indexDirectory = searchCommand.getIndexName();
        String queryLine = searchCommand.getQuery();
        boolean xml = searchCommand.isXML();
        List<FieldNames> cliFields = searchCommand.getFields();

        SearchRepository searchRepository = new SearchRepository(indexDirectory);

        List<ResultEntry> result = searchRepository.getResult(queryLine);

        if (xml) {
            XStream xstream = new XStream();
            xstream.alias("entry", ResultEntry.class);
            System.out.println(xstream.toXML(result));
        } else {
            System.out.println("Query: '" + queryLine + "'");

            if (cliFields.size() > 0) {
                for (int i = 0; i < cliFields.size(); i++) {
                    System.out.print("Field[" + i + "]=" + cliFields.get(i) + " ");
                }
            } else {
                cliFields = new ArrayList<FieldNames>();
                // If nothings is given on command line we have to define
                // default fields which will be printed
                cliFields.add(FieldNames.REVISION);
                cliFields.add(FieldNames.FILENAME);
                cliFields.add(FieldNames.PATH);
                cliFields.add(FieldNames.KIND);
            }

            if (result == null) {
                System.out.println("Somethings has gone wrong. Check the log file output!");
                return;
            }
            System.out.println("Total Hits: " + result.size());

            long count = 1;
            for (ResultEntry item : result) {
                System.out.printf("%6d: ", count);
                for (FieldNames fn : cliFields) {
                    if (fn.equals(FieldNames.PROPERTIES)) {
                        // Properties will be put into separate lines
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

    public void setReturnCode(int returnCode) {
        this.returnCode = returnCode;
    }

    public int getReturnCode() {
        return returnCode;
    }

    public static void main(String[] args) throws SVNException {
        SupoSECLI cli = new SupoSECLI();
        cli.run(args);
        System.exit(cli.getReturnCode());
    }
}
