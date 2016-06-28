package com.carlgira.oracle;

import com.carlgira.oracle.test.GroovyShellService;
import com.carlgira.oracle.test.UnitTestManager;
import org.junit.Test;

public class SoaUnitTest {

    private String host, user, password;
    private Integer port;
    private UnitTestManager unitTestManager;
    private GroovyShellService groovyShellService;

    public SoaUnitTest() throws Exception {
        this.host = "localhost";
        this.user = "weblogic";
        this.password = "welcome1";
        this.port = 8001;
        this.unitTestManager = new UnitTestManager(host,port,user,password);
        this.groovyShellService = new GroovyShellService(host,port);
    }

    /**
     * Execute directly a testSuite of a composite.
     */
    @Test
    public void testTestSuite1() throws Exception {
        this.unitTestManager.executeTestSuite("default/soa-test-project!1.0", "test_suite1");
    }

    /**
     * If you need to do things more complex, you can add or get data from the test-case as pre or post conditions using the GroovyShell utility
     */
    @Test
    public void testTestSuite2() throws Exception {

        String compositeDN = "";
        String testSuite = "";
        String testCase = "";

        String testId = compositeDN + "/" + testSuite + "/" + testCase;

        // Load data or pre conditions
        this.groovyShellService.createNewGroovyShell(testId);
        this.groovyShellService.executeGroovy(testId, "name=\"OutsideValue\"");

        this.unitTestManager.executeTestCase(compositeDN,testSuite,testCase);

        String getValue = this.groovyShellService.executeGroovy(testId, "token");

    }
}
