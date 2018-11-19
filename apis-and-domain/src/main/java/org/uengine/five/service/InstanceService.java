package org.uengine.five.service;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.hateoas.ResourceSupport;
import org.springframework.web.bind.annotation.*;
import org.uengine.kernel.ProcessInstance;

import javax.ws.rs.QueryParam;
import java.util.Map;

/**
 * Created by uengine on 2017. 8. 9..
 *
 * Implementation Principles:
 *  - REST Maturity Level : 2
 *  - Not using old uEngine ProcessManagerBean, this replaces the ProcessManagerBean
 *  - ResourceManager and CachedResourceManager will be used for definition caching (Not to use the old DefinitionFactory)
 *  - json must be Typed JSON to enable object polymorphism - need to change the jackson engine. TODO: accept? typed json is sometimes hard to read
 */
@FeignClient(name = "bpm", url="http://uengine-instance:9094")
public interface InstanceService {

    @RequestMapping(value = "/instance", method = {RequestMethod.POST})
    public ResourceSupport runDefinition(@RequestParam("defPath") String filePath, @QueryParam("simulation") boolean simulation) throws Exception;

    @RequestMapping(value = "/instance/{instanceId}/start", method = RequestMethod.POST)
    public ResourceSupport start(@PathVariable("instanceId") String instanceId) throws Exception;

    @RequestMapping(value = "/instance/{instanceId}/stop", method = RequestMethod.POST)
    public ResourceSupport stop(@PathVariable("instanceId") String instanceId) throws Exception;

    @RequestMapping(value = "/instance/{instanceId}/resume", method = RequestMethod.POST)
    public ResourceSupport resume(@PathVariable("instanceId") String instanceId) throws Exception;

    @RequestMapping(value = "/instance/{instanceId}", method = RequestMethod.GET)
    public ResourceSupport getInstance(@PathVariable("instanceId") String instanceId) throws Exception;

    @RequestMapping(value = "/instance/{instanceId}/activity/{tracingTag}/backToHere", method = RequestMethod.POST)
    public ResourceSupport backToHere(@PathVariable("instanceId") String instanceId, @PathVariable("tracingTag") String tracingTag) throws Exception;


//    @RequestMapping(value = "/instance/definition/", method = RequestMethod.POST)
//    public void onDeploy() throws Exception;

}
