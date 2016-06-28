package com.carlgira.oracle.test;

import org.junit.Test;
import java.io.IOException;
import static org.junit.Assert.*;

public class GroovyShellServiceTest {

    private GroovyShellService unitTestManager;

    public GroovyShellServiceTest() throws Exception {
        this.unitTestManager = new GroovyShellService("192.168.100.228", 8001);
    }

    @Test
    public void testCreateGroovyShell() throws IOException {
        String testId = String.valueOf((int)(Math.random()*1000));
        boolean result = this.unitTestManager.createNewGroovyShell(testId);
        assertTrue(result);
        result = this.unitTestManager.createNewGroovyShell(testId);
        assertTrue(!result);
    }

    @Test
    public void testDeleteGroovyShell() throws IOException {
        String testId = String.valueOf((int)(Math.random()*1000));

        boolean result = this.unitTestManager.deleteGroovyShell(testId);
        assertTrue(!result);

        this.unitTestManager.createNewGroovyShell(testId);
        result = this.unitTestManager.deleteGroovyShell(testId);
        assertTrue(result);

        result = this.unitTestManager.deleteGroovyShell(testId);
        assertTrue(!result);
    }

    @Test
    public void testExecuteGroovyCode() throws IOException {
        String testId = String.valueOf((int)(Math.random()*1000));

        this.unitTestManager.createNewGroovyShell(testId);

        String result = this.unitTestManager.executeGroovy(testId, "value = 5");
        assertNotNull(result);

        result = this.unitTestManager.executeGroovy(testId, "value");
        assertEquals(result, "5");
    }

    @Test(expected = RuntimeException.class)
    public void testFailExecuteGroovyCode() throws IOException {
        String testId = String.valueOf((int)(Math.random()*1000));
        this.unitTestManager.executeGroovy(testId, "value = 5");
    }

}
