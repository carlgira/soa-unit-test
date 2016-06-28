package com.carlgira.oracle.test;

import java.util.ArrayList;
import java.util.Hashtable;
import java.util.List;
import javax.naming.Context;
import oracle.soa.management.facade.Locator;
import oracle.soa.management.facade.LocatorFactory;
import oracle.soa.management.util.TestSuiteFilter;
import oracle.soa.management.util.TestRunOptions;
import oracle.soa.management.facade.tst.TestSuite;
import oracle.soa.management.facade.tst.TestCase;
import oracle.soa.management.facade.tst.TestRunResults;

/**
 * Class to execute Soa Unit TestSuites or TestCases
 */
public class UnitTestManager {

    private Locator locator;

    private String host;
    private Integer port;

    public UnitTestManager(String host, Integer port, String user, String password) throws Exception {
        this.host = host;
        this.port = port;

        Hashtable jndiProps = new Hashtable();
        jndiProps.put(Context.PROVIDER_URL, "t3://" + host + ":" + port + "/soa-infra");
        jndiProps.put(Context.INITIAL_CONTEXT_FACTORY,"weblogic.jndi.WLInitialContextFactory");
        jndiProps.put(Context.SECURITY_PRINCIPAL, user);
        jndiProps.put(Context.SECURITY_CREDENTIALS, password);
        jndiProps.put("dedicated.connection", "true");
        this.locator = LocatorFactory.createLocator(jndiProps);
    }

    /**
     * Execute a specific testSuite
     * @param compositeDN
     * @param testSuiteName
     * @throws Exception
     */
    public void executeTestSuite(String compositeDN, String testSuiteName) throws Exception {
        TestSuiteFilter testFilter = new TestSuiteFilter();
        testFilter.addSuiteName(testSuiteName);

        List<TestSuite> testSuites = locator.getTestSuites(compositeDN,testFilter);

        if(testSuites.isEmpty()){
            throw new Exception("No testSuite, " + testSuiteName + "in composite " + compositeDN);
        }

        for (TestSuite testSuite : testSuites) {
            List<TestCase> testcases = testSuite.getTestCases();

            TestRunOptions testRunOptions = new TestRunOptions();

            String runId = String.valueOf ((int)Math.random()*100000);
            String runName = "runName" + runId;

            testRunOptions.setTestRunId(runId);
            testRunOptions.setTestRunName(runName);

            TestRunResults result =  locator.executeTestCases(compositeDN,testRunOptions,testcases);

            if(result.getNumErrors() > 0){
                throw new Exception("Total errors: "+ result.getNumErrors() + " Status: "+ result.getStatus());
            }
        }
    }

    /**
     * Execute all the testSuites from a composite
     * @param compositeDN
     * @throws Exception
     */
    public void executeAllTestSuites(String compositeDN)  throws Exception {
        TestSuiteFilter testFilter = new TestSuiteFilter();

        List<TestSuite> testSuites = locator.getTestSuites(compositeDN,testFilter);

        if(testSuites.isEmpty()){
            throw new Exception("No composite, "  + compositeDN);
        }

        for (TestSuite testSuite : testSuites) {
            List<TestCase> testcases = testSuite.getTestCases();

            TestRunOptions testRunOptions = new TestRunOptions();
            String runId = String.valueOf ((int)Math.random()*100000);
            String runName = "runName" + runId;
            testRunOptions.setTestRunId(runId);
            testRunOptions.setTestRunName(runName);

            TestRunResults result =  locator.executeTestCases(compositeDN,testRunOptions,testcases);

            if(result.getNumErrors() > 0){
                throw new Exception("Total errors: "+ result.getNumErrors() + " Status: "+ result.getStatus());
            }
        }
    }

    /**
     * Execute a specific testCase of a testSuite in a composite
     * @param compositeDN
     * @param testSuiteName
     * @param testCaseName The testCaseName (with the .xml)
     * @throws Exception
     */
    public void executeTestCase(String compositeDN, String testSuiteName, String testCaseName) throws Exception {
        TestSuiteFilter testFilter = new TestSuiteFilter();
        testFilter.addSuiteName(testSuiteName);

        List<TestSuite> testSuites = locator.getTestSuites(compositeDN,testFilter);

        if(testSuites.isEmpty()){
            throw new Exception("No testSuite, " + testSuiteName + "in composite " + compositeDN);
        }

        for (TestSuite testSuite : testSuites) {
            List<TestCase> tempTestcases = testSuite.getTestCases();
            List<TestCase> testcases = new ArrayList<TestCase>();

            for(TestCase testCase : tempTestcases){
                if(testCase.getName().equals(testCaseName + ".xml")){
                    testcases.add(testCase);
                }
            }

            if(!testcases.isEmpty()){
                TestRunOptions testRunOptions = new TestRunOptions();
                String runId = String.valueOf ((int)Math.random()*100000);
                String runName = "runName" + runId;
                testRunOptions.setTestRunId(runId);
                testRunOptions.setTestRunName(runName);

                TestRunResults result =  locator.executeTestCases(compositeDN,testRunOptions,testcases);

                if(result.getNumErrors() > 0){
                    throw new Exception("Total errors: "+ result.getNumErrors() + " Status: "+ result.getStatus());
                }
            }
            else{
                throw new Exception("No testCase, " + testCaseName + " in testSuite " + testSuiteName);
            }
        }
    }
}
