package org.uengine.five.service;

import org.springframework.web.bind.annotation.*;
import org.uengine.five.framework.ProcessTransactional;

import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.NotAuthorizedException;

/**
 * Created by uengine on 2018. 11. 15..
 */
@RestController
public class TestService {

    @RequestMapping(value = "/tests/service", method = RequestMethod.POST, produces = "application/json; charset=UTF-8")
    public void service(HttpServletRequest request) throws Exception {

        String accessToken = request.getHeader("access_token");

        if(!"AAA".equals(accessToken))
            throw new NotAuthorizedException("Unauthorized");


    }

    @RequestMapping(value = "/tests/service", method = RequestMethod.GET, produces = "application/json; charset=UTF-8")
    public String getService(HttpServletRequest request) throws Exception {

        String accessToken = request.getHeader("access_token");

//        if(!"AAA".equals(accessToken))
//            throw new NotAuthorizedException("Unauthorized");

        return "OK:"+ accessToken;

    }

}
