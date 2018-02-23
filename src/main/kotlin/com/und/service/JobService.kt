package com.und.service

import com.und.model.JobDescriptor
import java.util.*

/**
 * An object that defines the contract for defining dynamic jobs
 *
 * @author Shiv Pratap
 * @since February 2018
 */
interface JobService {

    /**
     * Create and schedule a job by abstracting the Job and Triggers in the
     * JobDescriptor. name and group for this job are determined by campaign id,
     * client id and campaign name uniquely @see com.und.util.JobUtil
     * which uniquely identifies the job. If you specify values that is not
     * unique, the scheduler will silently ignore the job
     *
     * @param descriptor
     * the payload containing the Job and its associated Trigger(s).
     * The name and group uniquely identifies the job.
     * @return JobDescriptor this contains the JobDetail and Triggers of the
     * newly created job
     */
    fun createJob(descriptor: JobDescriptor): JobDescriptor

    /**
     * Searches for a Job identified by the given `JobKey`
     *
     * @param group
     * the group a job belongs to
     * @param name
     * the name of the dynamically scheduled job
     * @return the jobDescriptor if found or an empty Optional
     */
    fun findJob(group: String, name: String): Optional<JobDescriptor>

    /**
     * Updates the Job that matches the given `JobKey` with new
     * information
     *
     * @param group
     * the group a job belongs to
     * @param name
     * the name of the dynamically scheduled job
     * @param descriptor
     * the payload containing the updates to the JobDetail
     */
    fun updateJob(group: String, name: String, descriptor: JobDescriptor)

    /**
     * Deletes the Job identified by the given `JobKey`
     *
     * @param group
     * the group a job belongs to
     * @param name
     * the name of the dynamically scheduled job
     */
    fun deleteJob(group: String, name: String)

    /**
     * Pauses the Job identified by the given `JobKey`
     *
     * @param group
     * the group a job belongs to
     * @param name
     * the name of the dynamically scheduled job
     */
    fun pauseJob(group: String, name: String)

    /**
     * Resumes the Job identified by the given `JobKey`
     *
     * @param group
     * the group a job belongs to
     * @param name
     * the name of the dynamically scheduled job
     */
    fun resumeJob(group: String, name: String)
}
