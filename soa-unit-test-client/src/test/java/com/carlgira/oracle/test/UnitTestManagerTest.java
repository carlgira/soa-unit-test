package com.carlgira.oracle.test;

import org.junit.Test;

/**
 * Created by emateo on 28/06/2016.
 */
public class UnitTestManagerTest {

    private UnitTestManager unitTestManager;

    public UnitTestManagerTest() throws Exception {
        this.unitTestManager = new UnitTestManager("192.168.100.228", 8001, "weblogic", "weblogic11prb");
    }

    @Test
    public void testExecuteAllTestSuites() throws Exception {
        this.unitTestManager.executeAllTestSuites("default/soa-test-project!1.0");
    }

    @Test
    public void testExecuteTestSuite() throws Exception {
        this.unitTestManager.executeTestSuite("default/soa-test-project!1.0", "test_suite1");
    }

    @Test
    public void testExecuteTestCase() throws Exception {
        this.unitTestManager.executeTestCase("default/soa-test-project!1.0", "test_suite1", "test_case1");
    }
}
