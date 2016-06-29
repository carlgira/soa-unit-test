package com.carlgira.oracle;

import com.carlgira.oracle.test.GroovyShellService;
import com.carlgira.oracle.test.UnitTestManager;
import org.junit.After;
import org.junit.Test;

import java.io.IOException;

public class SoaUnitTest {

    private String host, user, password;
    private Integer port;
    private UnitTestManager unitTestManager;
    private GroovyShellService groovyShellService;

    public SoaUnitTest() throws Exception {
        this.host = "192.168.100.228";
        this.user = "weblogic";
        this.password = "weblogic11prb";
        this.port = 8001;
        this.unitTestManager = new UnitTestManager(host,port,user,password);
        this.groovyShellService = new GroovyShellService(this.host, this.port);
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
        String compositeDN = "default/soa-test-project!1.0";
        String testSuite = "test_suite2";
        String testCase = "test_case1";

        String testId = compositeDN + "/" + testSuite + "/" + testCase;

        // Creation of groovyShell
        this.groovyShellService.createNewGroovyShell(testId);

        // Load data before the execution of the test
        this.groovyShellService.executeGroovy(testId, "name='outName'");

        // Execution of test case
        this.unitTestManager.executeTestCase(compositeDN,testSuite,testCase);

        // Retrieve a variable after the test execution
        String token = this.groovyShellService.executeGroovy(testId, "token");
        System.out.println("SavedValue token = " + token);
    }

    @After
    public void cleanShells() throws IOException {
        // Clean the server so no groovy shell gets on weblogic
        this.groovyShellService.clearAllGroovyShells();
    }
}
