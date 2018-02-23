package com.und.job

import org.slf4j.LoggerFactory
import org.springframework.messaging.handler.annotation.SendTo
import org.springframework.stereotype.Service

@Service
class MessageJobService {

    private val logger = LoggerFactory.getLogger(javaClass)

    @SendTo("campaignTrigger")
    fun executeJob(campaignId: String) : String{

        logger.info("The job has begun...")
        try {
            //Thread.sleep(5000)

        } catch (e: InterruptedException) {
            logger.error("Error while executing job", e)
        } finally {
            logger.info("job has finished...")
        }
        return campaignId
    }
}
