Instructions on http://carlgira.blogspot.com.es/2016/05/oracle-soa-suite-unit-test-with-groovy.html

1. Create the jar (Soa 11.1.1.7, 12.1.3, 12.2.1 use a custom profile for your version)

mvn -Dsoa-version=11.1.1.7 clean package
mvn -Dsoa-version=12.1.3 clean package
mvn -Dsoa-version=12.2.1 clean package

2. Copy the file custom-test-case-1.0-jar-with-dependencies.jar to the directory Middleware\Oracle_SOA1\soa\modules\oracle.soa.fabric_11.1.1 in 11g or in the Middleware/soa/soa/modules/oracle.soa.fabric_11.1.1 on 12c.

3. Add the custom-test-case-1.0-jar-with-dependencies.jar in the classpath of the MANIFEST file of the jar oracle.soa.fabric.jar. Make sure that this file appears before than the fabric-ext.jar in the classpath variable. (be careful, the size of each line of the classpath is restricted)

4. Add to the weblogic start the property -Dcustom.testcase.groovy.script=/path/to/groovy/script/Util.groovy with the path to the groovy init script.

5. Reboot your server

6. Now you can add groovy code to your test suites in the Jdeveloper.
