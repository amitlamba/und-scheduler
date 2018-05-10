package com.und.job

import com.und.config.EventStream
import com.und.model.JobDescriptor
import com.und.service.JobService
import com.und.util.JobUtil
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.cloud.stream.annotation.StreamListener
import org.springframework.messaging.support.MessageBuilder
import org.springframework.stereotype.Service

@Service
class MessageJobService {

    @Autowired
    lateinit var jobService: JobService

    private val logger = LoggerFactory.getLogger(javaClass)

    @Autowired
    lateinit var eventStream: EventStream

    fun executeJob(campaignId: String): String {

        logger.info("The job has begun...")
        try {
            //eventStream.campaignTriggerEvent()
            eventStream?.let {stream ->
                stream.campaignTriggerEvent().send(MessageBuilder.withPayload(campaignId).build())
            }

        } catch (e: Exception) {
            logger.error("Error while executing job", e)
        } finally {
            logger.info("job has finished...")
        }
        return campaignId
    }


    @StreamListener("campaignTriggerReceive")
    fun trigger(campaignId: String) {
        println(campaignId)
        logger.debug("please trigger this campaign $campaignId logic ")
    }

    @StreamListener("scheduleJobReceive")
    fun save(jobDescriptor: JobDescriptor) {
        val action = jobDescriptor.action
        //FIXME handle errors and send back akcs on separate channels
        when (action) {
            JobDescriptor.Action.CREATE -> {
                jobService.createJob(jobDescriptor)
            }
            JobDescriptor.Action.PAUSE -> {
                val group: String = JobUtil.getGroupName(jobDescriptor.clientId)
                val name: String = JobUtil.getJobName(jobDescriptor.campaignId, jobDescriptor.campaignName)
                jobService.pauseJob(group, name)
            }
            JobDescriptor.Action.RESUME -> {
                val group: String = JobUtil.getGroupName(jobDescriptor.clientId)
                val name: String = JobUtil.getJobName(jobDescriptor.campaignId, jobDescriptor.campaignName)
                jobService.resumeJob(group, name)
            }
            JobDescriptor.Action.DELETE -> {
                val group: String = JobUtil.getGroupName(jobDescriptor.clientId)
                val name: String = JobUtil.getJobName(jobDescriptor.campaignId, jobDescriptor.campaignName)
                jobService.deleteJob(group, name)
            }
            JobDescriptor.Action.NOTHING -> {
            }
        }


        jobService.createJob(jobDescriptor)

    }
}
