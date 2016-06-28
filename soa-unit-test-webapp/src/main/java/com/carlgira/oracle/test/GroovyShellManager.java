package com.carlgira.oracle.test;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.HttpEntity;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.http.MediaType;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import groovy.lang.GroovyShell;

/**
 * Created by carlgira on 24/06/2016.
 */
@RestController
public class GroovyShellManager {

    private static String implementationName = "com.carlgira.testcase.GroovyProcessor";

    @RequestMapping("/groovyShell/create/{testId}")
    public Boolean createGroovyShell(@PathVariable("testId") String testId) throws Exception {

        Class implClass = Class.forName(implementationName, true, Thread.currentThread().getContextClassLoader());
        Method getServiceMethod = implClass.getMethod("addNewShell", new Class[]{String.class});
        Object result = getServiceMethod.invoke(null, new Object[]{testId});

        return (Boolean)result;
    }

    @RequestMapping("/groovyShell/delete/{testId}")
    public Boolean deleteGroovyShell(@PathVariable("testId") String testId)  throws Exception {

        Class implClass = Class.forName(implementationName, true, Thread.currentThread().getContextClassLoader());
        Method getServiceMethod = implClass.getMethod("deleteShell", new Class[]{String.class});
        Object result = getServiceMethod.invoke(null, new Object[]{testId});

        return (Boolean)result;
    }

    @RequestMapping("/groovyShell/cleanAll")
    public Boolean clearAllGroovyShells()  throws Exception {

        Class implClass = Class.forName(implementationName, true, Thread.currentThread().getContextClassLoader());
        Method getServiceMethod = implClass.getMethod("cleanAll", new Class[]{});
        Object result = getServiceMethod.invoke(null, new Object[]{});

        return (Boolean)result;
    }

    @RequestMapping(value = "/groovyShell/execute/{testId}" ,method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE )
    @ResponseBody
    public String executeGroovy(@PathVariable("testId") String testId, HttpEntity<String> httpEntity) throws Exception {
        String jsonInString = httpEntity.getBody();

        ObjectMapper mapper = new ObjectMapper();
        GroovyJsonRequest groovyJsonRequest = mapper.readValue(jsonInString, GroovyJsonRequest.class);

        Class implClass = Class.forName(implementationName, true, Thread.currentThread().getContextClassLoader());
        Method getServiceMethod = implClass.getMethod("evaluate", new Class[]{String.class, String.class});
        Object result = getServiceMethod.invoke(null, new Object[]{testId, groovyJsonRequest.getGroovy()});

        return result.toString();
    }
}
