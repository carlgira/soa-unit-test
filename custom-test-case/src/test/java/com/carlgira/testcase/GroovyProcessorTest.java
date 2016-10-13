package com.carlgira.testcase;

import groovy.lang.GroovyClassLoader;
import org.junit.*;
import static org.junit.Assert.*;
import java.io.*;
import java.net.URL;
import java.util.Enumeration;


/**
 * Created by cgiraldo on 26/09/2016.
 */
public class GroovyProcessorTest {


    @Test
    public void testGroovyShell() throws Exception {

        System.setProperty("custom.testcase.groovy.script", this.getClass().getClassLoader().getResources("Util.groovy").nextElement().getFile());

        GroovyProcessor groovyProcessor = new GroovyProcessor("shellId");
        String xmlString1 = "<h><xml>\n" +
                "${util.savePayload('bpel_input')" +
                "}"+
                "\t<integer>${util.randomInteger(10)}</integer>\n" +
                "\t<alpha>${util.randomAlpha(10)}</alpha>\n" +
                "\t<alphanumeric>${util.randomAlphaNumeric(10)}</alphanumeric>\n" +
                "\t<somevalue>${num='value'}</somevalue>\n" +
                "\t<formatedOffsetDate>${util.formatedDateWithOffset(3, 'yyyy-mm-dd')}</formatedOffsetDate>\n" +
                "\t<savedPreviousValue>${num}</savedPreviousValue>\n" +
                "\t<randomNumber>${Math.random()}</randomNumber>\n" +
                "\t<guid>${java.util.UUID.randomUUID()}</guid>\n" +
                "</xml></h>";


        String r = groovyProcessor.processXML(xmlString1);
        System.out.println(r);

        String xmlString2 = "<otherxml>\n" +
                "  ${util.deleteShell()\n" +
                "  util.xmlXpathOfString('/*/*',payloads.get('bpel_input')) \n" +
                "  }" +
                "${util.xmlXpathOfString('/*/*/randomNumber',payloads.get('bpel_input'))}" +
                "</otherxml>\n";


        r = groovyProcessor.processXML(xmlString2);
        System.out.println(r);
    }

    @Test
    public void testClassLoader() throws IOException {
        ClassLoader loader = GroovyProcessor.class.getClassLoader();
        Enumeration<URL> enumeration =  loader.getResources("META-INF/services/org.codehaus.groovy.runtime.ExtensionModule");

        GroovyClassLoader classLoader = new GroovyClassLoader();

        boolean groovy_sql = false;
        boolean groovy_xml = false;

        while(enumeration.hasMoreElements()){
            String jarFile = enumeration.nextElement().getFile();
            System.out.println(jarFile);
            if(jarFile.contains("groovy-sql")){
                jarFile = jarFile.substring(0, jarFile.indexOf("!"));
                classLoader.addURL(new File(jarFile).toURI().toURL());
                groovy_sql = true;
            }

            if(jarFile.contains("groovy-xml")){
                jarFile = jarFile.substring(0, jarFile.indexOf("!"));
                classLoader.addURL(new File(jarFile).toURI().toURL());
                groovy_xml = true;
            }
        }
        assertTrue(groovy_sql && groovy_xml);
    }
}
