package com.und.kafkalistener

import org.springframework.cloud.stream.annotation.Output
import org.springframework.messaging.MessageChannel

interface RecurrenceStream {

    @Output("campaignTrigger")
    fun outputEvent(): MessageChannel
}