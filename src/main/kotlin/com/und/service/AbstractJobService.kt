package com.und.service


import com.und.model.JobDescriptor
import com.und.util.loggerFor
import org.quartz.JobKey.jobKey
import org.quartz.Scheduler
import org.quartz.SchedulerException
import org.slf4j.Logger
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.transaction.annotation.Transactional
import java.util.*

/**
 * Abstract implementation of JobService
 *
 * @author Shiv Pratap
 * @since February 2018
 */
abstract class AbstractJobService : JobService {

    companion object {
        protected val logger: Logger = loggerFor(AbstractJobService::class.java)
    }

    @Autowired
    protected lateinit var scheduler: Scheduler

    /**
     * {@inheritDoc}
     */
    abstract override fun createJob(descriptor: JobDescriptor): JobDescriptor

    /**
     * {@inheritDoc}
     */
    @Transactional(readOnly = true)
    override fun findJob(group: String, name: String): Optional<JobDescriptor> {
        try {
            val jobDetail = scheduler.getJobDetail(jobKey(name, group))
            if (Objects.nonNull(jobDetail))
                return Optional.of(
                        JobDescriptor.buildDescriptor(jobDetail,
                                scheduler.getTriggersOfJob(jobKey(name, group))))
        } catch (e: SchedulerException) {
            logger.error("Could not find job with key - $group.$name due to error - ${e.localizedMessage}")
        }

        logger.warn("Could not find job with key - $group.$name")
        return Optional.empty()
    }

    /**
     * {@inheritDoc}
     */
    abstract override fun updateJob(group: String, name: String, descriptor: JobDescriptor)

    /**
     * {@inheritDoc}
     */
    override fun deleteJob(group: String, name: String) {
        try {
            scheduler.deleteJob(jobKey(name, group))
            logger.info("Deleted job with key - $group.$name")
        } catch (e: SchedulerException) {
            logger.error("Could not delete job with key - $group.$name due to error - ${e.localizedMessage}")
        }

    }

    /**
     * {@inheritDoc}
     */
    override fun pauseJob(group: String, name: String) {
        try {
            scheduler.pauseJob(jobKey(name, group))
            logger.info("Paused job with key - {}.{}", group, name)
        } catch (e: SchedulerException) {
            logger.error("Could not pause job with key - $group.$name due to error - ${e.localizedMessage}")
        }

    }

    /**
     * {@inheritDoc}
     */
    override fun resumeJob(group: String, name: String) {
        try {
            scheduler.resumeJob(jobKey(name, group))
            logger.info("Resumed job with key - {}.{}", group, name)
        } catch (e: SchedulerException) {
            logger.error("Could not resume job with key - $group.$name due to error - ${e.localizedMessage}")
        }

    }
}
