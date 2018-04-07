package com.und.config

import org.springframework.cloud.stream.annotation.Input
import org.springframework.cloud.stream.annotation.Output
import org.springframework.messaging.MessageChannel
import org.springframework.messaging.SubscribableChannel

interface EventStream {

    @Input("scheduleJob")
    fun scheduleJobRecieve(): SubscribableChannel

    @Output("campaignTrigger")
    fun campaignTriggerEvent(): MessageChannel

}