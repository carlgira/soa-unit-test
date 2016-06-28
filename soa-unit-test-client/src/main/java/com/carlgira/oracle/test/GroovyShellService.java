package com.carlgira.oracle.test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Groovy Shell client service. Adds, delete groovy shells and execute code groovy code inside those shells
 */
public class GroovyShellService {

    private String host;
    private Integer port;

    public GroovyShellService(String host, Integer port){
        this.host = host;
        this.port = port;
    }

    /**
     * Calls the REST service /soa-unit-test-webapp-1.0.0/groovyShell/create to create a groovyShell for the Soa unit test
     * @param testCaseName
     * @return
     * @throws IOException
     */
    public boolean createNewGroovyShell(String testCaseName) throws IOException {
        URL url = new URL("http://"+ host  + ":" + this.port  + "/soa-unit-test-webapp-1.0.0/groovyShell/create?testId=" + testCaseName );
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output = "";
        while (br.ready()) {
            output += br.readLine();
        }
        conn.disconnect();
        return output.equals("true");
    }

    /**
     * Calls the REST service /soa-unit-test-webapp-1.0.0/groovyShell/delete to delete a groovyShell used for the Soa unit test
     * @param testCaseName
     * @return
     * @throws IOException
     */
    public boolean deleteGroovyShell(String testCaseName) throws IOException {
        URL url = new URL("http://"+ host  + ":" + this.port  + "/soa-unit-test-webapp-1.0.0/groovyShell/delete?testId=" + testCaseName );
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output = "";
        while (br.ready()) {
            output += br.readLine();
        }
        conn.disconnect();
        return output.equals("true");
    }

    /**
     * Calls the REST service /soa-unit-test-webapp-1.0.0/groovyShell/cleanAll to clear all saved groovy Shells
     * @throws IOException
     */
    public void clearAllGroovyShells() throws IOException {
        URL url = new URL("http://"+ host  + ":" + this.port  + "/soa-unit-test-webapp-1.0.0/groovyShell/cleanAll");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.setRequestProperty("Accept", "application/json");

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }
        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output = "";
        while (br.ready()) {
            output += br.readLine();
        }
        conn.disconnect();
    }

    /**
     *
     * @param testCaseName
     * @param groovy
     * @return
     * @throws IOException
     */
    public String executeGroovy(String testCaseName, String groovy) throws IOException {
        URL url = new URL("http://"+ host  + ":" + this.port  + "/soa-unit-test-webapp-1.0.0/groovyShell/execute?testId=" + testCaseName );
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setDoOutput(true);
        conn.setRequestMethod("POST");
        conn.setRequestProperty("Content-Type", "application/json");

        String input = "{\"groovy\": \"" + groovy + " \"}";

        OutputStream os = conn.getOutputStream();
        os.write(input.getBytes());
        os.flush();

        if (conn.getResponseCode() != 200) {
            throw new RuntimeException("Failed : HTTP error code : " + conn.getResponseCode());
        }

        BufferedReader br = new BufferedReader(new InputStreamReader((conn.getInputStream())));
        String output = "";
        while (br.ready()) {
            output += br.readLine();
        }
        conn.disconnect();

        return output;
    }
}
