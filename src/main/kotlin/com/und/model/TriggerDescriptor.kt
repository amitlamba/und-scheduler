package com.und.model

import com.und.util.JobUtil
import org.quartz.CronExpression.isValidExpression
import org.quartz.CronScheduleBuilder.cronSchedule
import org.quartz.JobDataMap
import org.quartz.SimpleScheduleBuilder.simpleSchedule
import org.quartz.CalendarIntervalScheduleBuilder.calendarIntervalSchedule
import org.quartz.Trigger
import org.quartz.TriggerBuilder.newTrigger
import org.quartz.TriggerUtils
import org.quartz.impl.calendar.BaseCalendar
import org.quartz.spi.OperableTrigger
import org.springframework.util.StringUtils.isEmpty
import java.sql.Date
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZoneId.systemDefault
import java.time.ZoneOffset
import java.util.*


class TriggerDescriptor {

    lateinit var name: String
    lateinit var group: String
    var startDate: LocalDate? = null
    var endDate: LocalDate? = null
    var countTimes: Int = 0
    //@JsonSerialize(using = LocalDateTimeSerializer::class)
    var fireTime: LocalDateTime? = null
    var fireTimes : List<LocalDateTime>? = null
    var cron: String? = null


    private fun buildName(jobDescriptor: JobDescriptor): String {
        return JobUtil.getJobName(jobDescriptor)
    }

    private fun buildGroupName(jobDescriptor: JobDescriptor): String {
        return JobUtil.getGroupName(jobDescriptor)
    }


    /**
     * Convenience method for building a Trigger
     *
     * @return the Trigger associated with this descriptor
     */
    fun buildTrigger(jobDescriptor: JobDescriptor): Trigger {
        name = buildName(jobDescriptor)
        group = buildGroupName(jobDescriptor)

        val triggerBuilder = when {
            !isEmpty(cron) && !isValidExpression(cron) -> {
                throw IllegalArgumentException("Provided expression '$cron' is not a valid cron expression")
            }
            !isEmpty(cron) && isValidExpression(cron) -> {
                val jobDataMap = JobDataMap()
                jobDataMap["cron"] = cron
                //FIXME handle timezone to UTC, as of now it is set to systemDefault
                newTrigger()
                        .withIdentity(name, group)
                        .withSchedule(cronSchedule(cron)
                                .withMisfireHandlingInstructionFireAndProceed()
                                .inTimeZone(TimeZone.getTimeZone(systemDefault())))
                        .usingJobData("cron", cron)
                        .usingJobData("startDate", startDate?.toEpochDay()?.toString())
                        .usingJobData("endDate", endDate?.toEpochDay()?.toString())
                        .usingJobData("countTimes", countTimes.toString())

            }
            !isEmpty(fireTime) -> {
                val jobDataMap = JobDataMap()
                jobDataMap["fireTime"] = fireTime
                if(startDate!=null) jobDataMap["startDate"] = startDate
                if(endDate!=null) jobDataMap["endDate"] = endDate
                if(countTimes > 0) jobDataMap["countTimes"] = countTimes.toString()
                newTrigger()
                        .withIdentity(name, group)
                        .withSchedule(simpleSchedule()
                                .withMisfireHandlingInstructionNextWithExistingCount())
                        .startAt(Date.from(fireTime?.atZone(systemDefault())?.toInstant()))
                        .usingJobData("fireTime", fireTime?.toEpochSecond(ZoneOffset.UTC)?.toString())
                        .usingJobData("startDate", startDate?.toEpochDay()?.toString())
                        .usingJobData("endDate", endDate?.toEpochDay()?.toString())
                        .usingJobData("countTimes", countTimes.toString())

            }
            else -> {
                newTrigger()
                        .withIdentity(name, group)
                        .withSchedule(calendarIntervalSchedule()

                        )
                throw IllegalStateException("Specify either one of 'cron' or 'fireTime'")
            }

        }
        if (startDate != null) {
            triggerBuilder.startAt(java.sql.Date.valueOf(startDate))
        }
        if (endDate != null) {
            triggerBuilder.endAt(java.sql.Date.valueOf(endDate))
        }
        if (endDate == null && countTimes > 0) {
            val trigger = triggerBuilder.build()
            val endDate = TriggerUtils.computeEndTimeToAllowParticularNumberOfFirings(trigger as OperableTrigger,
                    BaseCalendar(Calendar.getInstance().timeZone), countTimes)
            triggerBuilder.endAt(endDate)
        }

        return triggerBuilder.build()


    }

    companion object {
        /**
         *
         * @param trigger
         * the Trigger used to build this descriptor
         * @return the TriggerDescriptor
         */
        fun buildDescriptor(trigger: Trigger): TriggerDescriptor {
            val triggerDescriptor = TriggerDescriptor()
            with(triggerDescriptor) {
                name = trigger.key.name
                group = trigger.key.group
                val fireTimeString = trigger.jobDataMap["fireTime"] as String?
                if(!fireTimeString.isNullOrBlank()) fireTime = LocalDateTime.ofEpochSecond(fireTimeString?.toLong()!!,0, ZoneOffset.UTC)
                cron = trigger.jobDataMap["cron"] as String?
                val startDateString = trigger.jobDataMap["startDate"] as String?
                if(startDateString != null) startDate = LocalDate.ofEpochDay(startDateString.toLong())
                val endDateString = trigger.jobDataMap["endDate"] as String?
                if(endDateString != null) endDate = LocalDate.ofEpochDay(endDateString.toLong())
                val count = (trigger.jobDataMap["countTimes"] as String?)
                if(count!=null) countTimes = count.toInt()
            }
            return triggerDescriptor
        }

    }

}
