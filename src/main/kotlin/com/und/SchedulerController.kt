package com.und


import com.und.model.JobDescriptor
import com.und.service.JobService
import com.und.util.JobUtil
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.http.HttpStatus.CREATED
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import javax.validation.Valid

@RestController
@RequestMapping("/schedule/v1")
class SchedulerController {

    @Autowired
    lateinit var jobService: JobService

    /**
     * POST /jobs
     * @param descriptor
     * @return
     */
    @PostMapping(path = ["/jobs"])
    fun createJob(@Valid @RequestBody descriptor: JobDescriptor): ResponseEntity<JobDescriptor> {
        return ResponseEntity(jobService.createJob(descriptor), CREATED)
    }


    /**
     * GET /jobs/clientId/campaignId/campaignName
     *
     * @param clientId
     * @param campaignId
     * @param campaignName
     * @return
     */
    @GetMapping(path = ["/jobs/{clientId}/{campaignId}/{campaignName}"])
    fun findJob(@PathVariable clientId: String, @PathVariable campaignId: String, @PathVariable campaignName: String): ResponseEntity<JobDescriptor> {
        val group: String = JobUtil.getGroupName(clientId)
        val name: String = JobUtil.getJobName(campaignId, campaignName)
        return jobService.findJob(group, name).map { ResponseEntity.ok(it) }.orElse(ResponseEntity.notFound().build())
    }

    /**
     * PUT /jobs
     *
     * @param descriptor
     * @return
     */
    @PutMapping(path = ["/jobs/"])
    fun updateJob(@Valid @RequestBody descriptor: JobDescriptor): ResponseEntity<Void> {
        val group: String = JobUtil.getGroupName(descriptor)
        val name: String = JobUtil.getJobName(descriptor)
        jobService.updateJob(group, name, descriptor)
        return ResponseEntity.noContent().build()
    }

    /**
     * DELETE /jobs/clientId/campaignId/campaignName
     *
     * @param clientId
     * @param campaignId
     * @param campaignName
     * @return
     */
    @DeleteMapping(path = ["/jobs/delete/{clientId}/{campaignId}/{campaignName}"])
    fun deleteJob(@PathVariable clientId: String, @PathVariable campaignId: String, @PathVariable campaignName: String): ResponseEntity<Void> {
        val group: String = JobUtil.getGroupName(clientId)
        val name: String = JobUtil.getJobName(campaignId, campaignName)
        jobService.deleteJob(group, name)
        return ResponseEntity.noContent().build()
    }

    /**
     * PATCH /jobs/pause/clientId/campaignId/campaignName
     *
     * @param clientId
     * @param campaignId
     * @param campaignName
     * @return
     */
    @PatchMapping(path = ["/jobs/pause/{clientId}/{campaignId}/{campaignName}"])
    fun pauseJob(@PathVariable clientId: String, @PathVariable campaignId: String, @PathVariable campaignName: String): ResponseEntity<Void> {
        val group: String = JobUtil.getGroupName(clientId)
        val name: String = JobUtil.getJobName(campaignId, campaignName)
        jobService.pauseJob(group, name)
        return ResponseEntity.noContent().build()
    }

    /**
     * PATCH /jobs/resume/clientId/campaignId/campaignName
     *
     * @param clientId
     * @param campaignId
     * @param campaignName
     * @return
     */
    @PatchMapping(path = ["/jobs/resume/{clientId}/{campaignId}/{campaignName}"])
    fun resumeJob(@PathVariable clientId: String, @PathVariable campaignId: String, @PathVariable campaignName: String): ResponseEntity<Void> {
        val group: String = JobUtil.getGroupName(clientId)
        val name: String = JobUtil.getJobName(campaignId, campaignName)
        jobService.resumeJob(group, name)
        return ResponseEntity.noContent().build()
    }

}
