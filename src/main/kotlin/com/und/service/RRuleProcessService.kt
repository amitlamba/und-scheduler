package com.und.service

import com.und.model.JobDescriptor
import com.und.model.RecurrenceMessage
import com.und.util.loggerFor
import org.quartz.JobKey
import org.quartz.SchedulerException
import org.slf4j.Logger
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class RRuleProcessService : AbstractJobService() {

    companion object {
        protected val logger: Logger = loggerFor(RRuleProcessService::class.java)
    }



    /**
     * {@inheritDoc}
     */
    override fun createJob(descriptor: JobDescriptor): JobDescriptor {
        val jobDetail = descriptor.buildJobDetail()
        val triggersForJob = descriptor.buildTriggers()
        RRuleProcessService.logger.info("About to save job with key - ${jobDetail.key}")
        try {
            scheduler.scheduleJob(jobDetail, triggersForJob, false)
            logger.info("Job with key - ${jobDetail.key} saved successfully")
        } catch (e: SchedulerException) {
            logger.error("Could not save job with key - ${jobDetail.key} due to error - ${e.localizedMessage}")
            throw IllegalArgumentException(e.localizedMessage)
        }

        return descriptor
    }

    /**
     * {@inheritDoc}
     */
    override fun updateJob(group: String, name: String, descriptor: JobDescriptor) {
        try {
            val oldJobDetail = scheduler.getJobDetail(JobKey.jobKey(name, group))
            if (oldJobDetail != null) {
                val jobDataMap = oldJobDetail.jobDataMap
                val jb = oldJobDetail.jobBuilder
                val newJobDetail = jb.usingJobData(jobDataMap).storeDurably().build()
                scheduler.addJob(newJobDetail, true)
                logger.info("Updated job with key - {}", newJobDetail.key)
                return
            }
            logger.warn("Could not find job with key - $group.$name  to update", group, name)
        } catch (e: SchedulerException) {
            logger.error("Could not find job with key - $group.$name due to error - ${e.localizedMessage}")
        }

    }

    @StreamListener("rruleInput")
    fun processRRule(recurrenceRule: RecurrenceMessage) {
        logger.info("found recurrence rule $recurrenceRule")
        rruleHelper(recurrenceRule)

    }

    fun rruleHelper(recurrenceRule: RecurrenceMessage) {


    }

}