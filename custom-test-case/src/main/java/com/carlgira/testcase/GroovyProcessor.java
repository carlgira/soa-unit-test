package com.carlgira.testcase;

import groovy.lang.Binding;
import groovy.lang.GroovyClassLoader;
import groovy.lang.GroovyShell;
import org.apache.xmlbeans.XmlObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Evaluate groovy expressions inside of XMLs request and responses of a TestSuite. This enables the creation of dynamic request and responses.
 * If configured the System.property "custom.test.case.groovy.script.path" with a groovy script, the file is loaded for initialization in every GroovyShell (common functions, for data generation, dates,  xml, and sql)
 */
public class GroovyProcessor {

    /**
     * GroovyShell to evaluate all the code
     */
    private GroovyShell groovyShell;

    /**
     * Shell id
     */
    private String shellId;

    /**
     * Static variable to maintain the saved GroovyShells
     */
    private static Map<String,GroovyShell> groovyShells;

    /**
     * Custom classloader for the GroovyShell (load groovy-sql ans groovy-xml)
     */
    private static GroovyClassLoader classLoader;

    private static String groovyFile;

    static{
        try {
            init();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * The GroovyShell needs to load groovy libs like groovy-sql and groovy-xml manually to be visible on the Classloader (different from the app classloader)
     * Used a trick to find the needed jar, looking for a very specific file inside both jars.
     * @throws IOException
     */
    private static void init() throws IOException {
        groovyShells = new HashMap<String, GroovyShell>();
        groovyFile = System.getProperty("custom.testcase.groovy.script");
        if(groovyFile != null){

            ClassLoader loader = GroovyProcessor.class.getClassLoader();
            Enumeration<URL> enumeration =  loader.getResources("META-INF/services/org.codehaus.groovy.runtime.ExtensionModule");

            classLoader = new GroovyClassLoader();

            while(enumeration.hasMoreElements()){
                String jarFile = enumeration.nextElement().getFile();
                if(jarFile.contains("groovy-sql")){
                    jarFile = jarFile.substring(0, jarFile.indexOf("!"));
                    classLoader.addURL(new File(jarFile).toURI().toURL());
                }

                if(jarFile.contains("groovy-xml")){
                    jarFile = jarFile.substring(0, jarFile.indexOf("!"));
                    classLoader.addURL(new File(jarFile).toURI().toURL());
                }
            }
        }
    }

    /**
     * Class constructor. The constructor checks if there is available an existing GroovyShell with the same id, if it exist, that console is used, if not, a new GroovyShell is created, initialized and saved for later use.
     * @param testId
     */
    public GroovyProcessor(String testId){

        this.shellId = testId;
        if(!groovyShells.containsKey(testId)){

            GroovyShell shell = null;
            if(classLoader != null){
                try {
                    shell = new GroovyShell(classLoader);
                    shell.evaluate(new FileReader(new File(groovyFile)), "Util.groovy");
                    shell.evaluate("util = new Util(); util.delShell = false; util.savePayloadFlag = false; util.payloadName = ''; payloads = new java.util.HashMap<String,String>();");
                    shell.evaluate("util.namedQueries = new java.util.HashMap<String,String>(); util.initNamedQueries();");
                } catch (Exception e) {
                    e.printStackTrace();
                    Binding binding = new Binding();
                    shell = new GroovyShell(binding);
                }

            }
            else{
                Binding binding = new Binding();
                shell = new GroovyShell(binding);
            }

            groovyShells.put(testId, shell);
        }

        this.groovyShell = groovyShells.get(testId);
    }

    /**
     * Checks if the XmlDocument has groovy code
     * @param xmlObject
     * @return
     */
    public boolean hasGroovy(XmlObject xmlObject){
        String xmlString = xmlObject.toString();
        Pattern r = Pattern.compile("\\$\\{([^\\$]+)\\}", Pattern.DOTALL);
        Matcher m = r.matcher(xmlString);
        if(m.find()) {
            return true;
        }
        return false;
    }

    /**
     * Evaluates all the groovy code inside of the XML
     * @param xmlString
     * @return
     */
    public String processXML(String xmlString){
        String result = xmlString;
        try{

            Pattern r = Pattern.compile("\\$\\{([^\\$]+)\\}", Pattern.DOTALL);
            Matcher m = r.matcher(xmlString);

            while (m.find()) {
                String value = m.group(0);
                String expr =  evaluate(m.group(1));
                result = result.replace(value, expr);
            }

            savePayload(result);
            deleteShell();
        }catch (Exception e){
            e.printStackTrace();
            groovyShells.remove(this.shellId);
        }

        return result;
    }

    /**
     * Auxiliary function to save payload so in can be retrieved for later use
     * @param payload
     */
    private void savePayload(String payload){
        try {
            if(Boolean.parseBoolean(this.groovyShell.evaluate("util.savePayloadFlag").toString())) {
                this.groovyShell.evaluate("payloads.put(util.payloadName, '"  +  payload.replace("\n","\\n' + '").replace("\r","\\r' + '") + "' )");
                this.groovyShell.evaluate("util.savePayloadFlag = false");
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Auxiliary function to delete the used groovy shell
     */
    private void deleteShell(){
        try {
            if(Boolean.parseBoolean(this.groovyShell.evaluate("util.delShell").toString())){
                groovyShells.remove(this.shellId);
            }
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }

    /**
     * Evaluates all the groovy code inside of the XML
     * @param xmlObject
     * @return
     */
    public String processXML(XmlObject xmlObject){
        return this.processXML(xmlObject.toString());
    }

    /**
     *
     * @param expr
     * @return
     */
    public String evaluate(String expr){
         Object result = groovyShell.evaluate(expr);
        return result == null?  "": result.toString();
    }
}
