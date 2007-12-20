package com.soebes.supose.jobs;

import org.apache.log4j.Logger;
import org.quartz.SchedulerException;
import org.quartz.SchedulerListener;
import org.quartz.Trigger;

public class JobSchedulerListener implements SchedulerListener {
	private static Logger LOGGER = Logger.getLogger(JobSchedulerListener.class);

	public void jobScheduled(Trigger trigger) {
		LOGGER.info("jobScheduled()");
	}

	public void jobUnscheduled(String triggerName, String triggerGroup) {
		LOGGER.info("jobUnscheduled()");
	}

	public void jobsPaused(String jobName, String jobGroup) {
		LOGGER.info("jobsPaused()");
	}

	public void jobsResumed(String jobName, String jobGroup) {
		LOGGER.info("jobsResumed()");
	}

	public void schedulerError(String msg, SchedulerException cause) {
		LOGGER.info("schedulerError()");
	}

	public void schedulerShutdown() {
		LOGGER.info("schedulerShutdown()");
	}

	public void triggerFinalized(Trigger trigger) {
		LOGGER.info("triggerFinalized()");
	}

	public void triggersPaused(String triggerName, String triggerGroup) {
		LOGGER.info("triggersPaused()");
	}

	public void triggersResumed(String triggerName, String triggerGroup) {
		LOGGER.info("triggersResumed()");
	}

}
