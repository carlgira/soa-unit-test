import groovy.sql.GroovyRowResult
import groovy.sql.Sql
import org.w3c.dom.Document
import org.w3c.dom.NodeList
import org.w3c.dom.Node
import org.xml.sax.InputSource

import javax.naming.InitialContext
import javax.sql.DataSource
import javax.xml.parsers.DocumentBuilder
import javax.xml.parsers.DocumentBuilderFactory
import javax.xml.transform.OutputKeys
import javax.xml.transform.Transformer
import javax.xml.transform.TransformerFactory
import javax.xml.transform.dom.DOMSource
import javax.xml.transform.stream.StreamResult
import javax.xml.xpath.XPath
import javax.xml.xpath.XPathConstants
import javax.xml.xpath.XPathExpression
import javax.xml.xpath.XPathFactory

/** Its recommended not to change these functions, just add new ones at the end **/

/** CORE FUNCTIONS **/


savePayloadFlag = false
payloadName = ""
delShell = false

/**
 * Function that must be used in last step of generated payload of a test-case so all the data of test-execution can be cleaned
 * @return
 */
def deleteShell(){
    delShell = true
    return ''
}

/**
 * Function to save the current payload to memory so it can be used later in test-case
 * @param payloadName
 * @return
 */
def savePayload(payloadName){
    this.savePayloadFlag = true
    this.payloadName = payloadName
    return ''
}

/** DATA GENERATORS **/

/**
 * A generic function to crate a random string using a defined 'alphabet' and a specific'size'
 * @param AB List of all characters that can be used in random string
 * @param len Size of generated String
 * @return
 */
def randomString(String AB, int len ){
    StringBuilder sb = new StringBuilder( len );
    java.security.SecureRandom rnd = new java.security.SecureRandom();
    for( int i = 0; i < len; i++ )
        sb.append( AB.charAt( rnd.nextInt(AB.length()) ) );
    return sb.toString();
}
/**
 * Generate a random sequence of digits of a specific size
 * @param size
 * @return
 */
def randomInteger(int size){
    return randomString(('0'..'9').join(), size)
}
/**
 * Generate a random sequence of letters of specific size
 * @param size
 * @return
 */
def randomAlpha(int size){
    return randomString(('A'..'Z').join(), size)
}

/**
 * Generate a random sequence of alphanumeric  of specific size
 * @param size
 * @return
 */
def randomAlphaNumeric(int size){
    return randomString((('A'..'Z')+('0'..'9')).join(), size)
}

/** DATE FUNCTIONS **/
def formatedDateWithOffset(offset, format){
    def date = new Date();
    def c = Calendar.getInstance();
    c.setTime(date);
    c.add(Calendar.DATE, offset);
    date = c.getTime();
    sdf = new java.text.SimpleDateFormat(format);
    sdf.format(date);
}

/** DATABASE FUNCTIONS **/

namedQueries = null

/**
 * Initialization of list of named-query
 * @return
 */
def initNamedQueries(){
    this.namedQueries.put("testQuery", "select 1 app from dual")
    this.namedQueries.put("getAgenteTarjetaNombreEmail", "SELECT CDTARJE,CDAGENTE, NOMBRE, APELLIDOS, EMAIL FROM (SELECT * FROM TARJETA WHERE FEBAJA > SYSDATE ORDER BY DBMS_RANDOM.VALUE) WHERE rownum = 1")
}

/**
 * Function to execute one of the saved queries
 * @param dataSourceJNDI
 * @param namedQuery
 * @return
 */
def executeNamedQuery(dataSourceJNDI, namedQuery){
    return executeQuery(dataSourceJNDI, namedQueries.get(namedQuery))
}

/**
 * Function to execute query using datasource
 * @param dataSourceJNDI
 * @param sqlQuery
 * @return
 */
def executeQuery(dataSourceJNDI, sqlQuery){
    InitialContext ctx = new InitialContext()
    DataSource dataSource = ctx.lookup(dataSourceJNDI)
    def connection = new Sql(dataSource)
    def results = connection.rows(sqlQuery)
    connection.close()
    return results
}


/** XML FUNCTIONS **/

/**
 * Function to execute a Xpath expression on a XMlString
 * @param xpathString Xpath expression
 * @param xmlString XmlString
 * @return
 */
def xmlXpathOfString(xpathString, xmlString){
    InputSource source = new InputSource(new StringReader(xmlString));

    DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    DocumentBuilder db = dbf.newDocumentBuilder();
    Document document = db.parse(source);

    XPathFactory xpathFactory = XPathFactory.newInstance();
    XPath xpath = xpathFactory.newXPath();
    XPathExpression expr = xpath.compile(xpathString);
    Object o = expr.evaluate(document, XPathConstants.NODESET);
    NodeList requiredNodeList = (NodeList)o

    Node elem = requiredNodeList.item(0);//Your Node
    StringWriter buf = new StringWriter();
    Transformer xform = TransformerFactory.newInstance().newTransformer();
    xform.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
    xform.setOutputProperty(OutputKeys.INDENT, "yes");
    xform.transform(new DOMSource(elem), new StreamResult(buf));

    return buf.toString()
}

