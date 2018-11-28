package org.uengine.msa;

import org.springframework.cloud.netflix.feign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.uengine.uml.model.ClassDefinition;

/**
 * Created by uengine on 2018. 2. 8..
 */
@FeignClient("java-reverse")
public interface JavaReserveService {

    @RequestMapping(value = "/java", method = RequestMethod.POST)
    public String java(@RequestParam("sourceUrl") String sourceUrl, @RequestBody ClassDefinition classDefinition) throws Exception;

}

