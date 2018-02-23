package com.und.config

import com.und.scheduler.MessageJob
import org.quartz.*
import org.quartz.JobBuilder.newJob
import org.quartz.Scheduler
import org.quartz.SimpleScheduleBuilder.simpleSchedule
import org.quartz.TriggerBuilder.newTrigger
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.condition.ConditionalOnExpression
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.scheduling.quartz.SchedulerFactoryBean
import org.springframework.scheduling.quartz.SpringBeanJobFactory
import java.io.IOException
import javax.annotation.PostConstruct
import javax.sql.DataSource


@Configuration
@ConditionalOnExpression("'\${using.spring.schedulerFactory}'=='true'")
class UndScheduler {

    internal var logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    lateinit var applicationContext: ApplicationContext

    @Autowired
    lateinit var dataSource: DataSource




    @PostConstruct
    fun init() {
        logger.info("starting Quartz...")
    }

    @Bean
    fun springBeanJobFactory(): SpringBeanJobFactory {
        val jobFactory = AutoWiringSpringBeanJobFactory()
        logger.debug("Configuring Job factory")

        jobFactory.setApplicationContext(applicationContext)
        return jobFactory
    }


    @Bean
    fun schedulerFactory(applicationContext: ApplicationContext,
                         dataSource: DataSource): SchedulerFactoryBean {
        val schedulerFactoryBean = SchedulerFactoryBean()
        schedulerFactoryBean.setDataSource(dataSource)
        schedulerFactoryBean.setConfigLocation(ClassPathResource("quartz.properties"))
        schedulerFactoryBean.setJobFactory(springBeanJobFactory())
        schedulerFactoryBean.setApplicationContextSchedulerContextKey("applicationContext")
        return schedulerFactoryBean
    }

    @Bean
    @Throws(SchedulerException::class, IOException::class)
    fun scheduler(): Scheduler {

        val factory = schedulerFactory(applicationContext,dataSource)
        //factory.initialize(ClassPathResource("quartz.properties").inputStream)

        logger.debug("Getting a handle to the Scheduler")
        val scheduler = factory.scheduler
        scheduler.setJobFactory(springBeanJobFactory())
        //scheduler.scheduleJob(job, trigger)

        logger.debug("Starting Scheduler threads")
        //scheduler.start()
        return scheduler
    }


/*
    @Bean
    fun jobDetail(): JobDetail {

        return newJob().ofType(MessageJob::class.java).storeDurably()
                .withIdentity(JobKey.jobKey("message_Job_Detail"))
                .withDescription("Invoke Job service...").build()
    }

    @Bean
    fun trigger(job: JobDetail): Trigger {

        val frequencyInSec = 10
        logger.info("Configuring trigger to fire every {} seconds", frequencyInSec)

        return newTrigger()
                .forJob(job)
                .withIdentity(TriggerKey.triggerKey("Qrtz_Trigger"))
                .withDescription("Sample trigger")
                .withSchedule(simpleSchedule().withIntervalInSeconds(frequencyInSec).repeatForever())
                .build()
    }*/
}