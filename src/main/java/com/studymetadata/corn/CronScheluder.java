package com.studymetadata.corn;

import org.apache.log4j.Logger;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

public class CronScheluder {

	private static final Logger LOGGER = Logger.getLogger(MyJob.class);
	
	public CronScheluder() throws Exception {
		LOGGER.info("CronScheluder.CronScheluder() :: Cron executing.");
    	JobDetail job = JobBuilder.newJob(MyJob.class).withIdentity("LOCALIVEJob", "LOCALIVE").build();
    	Trigger trigger = TriggerBuilder.newTrigger().withIdentity("LOCALIVETrigger", "LOCALIVE").withSchedule(CronScheduleBuilder.cronSchedule("0 0/1 * * * ?")).build();
    	Scheduler scheduler = new StdSchedulerFactory().getScheduler();
    	scheduler.start();
    	scheduler.scheduleJob(job, trigger);
    }
}

