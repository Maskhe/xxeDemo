import org.xml.sax.InputSource;

import javax.xml.XMLConstants;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.sax.SAXSource;
import javax.xml.transform.stream.StreamResult;
import java.io.StringReader;
import java.io.StringWriter;

public class Main {
    public static void main(String[] args) throws TransformerException {
        String xml = "<?xml version='1.0'?>" +
                "<!DOCTYPE root [<!ENTITY url SYSTEM \"http://httplog.sicnu.tech:8080/httplog/xxe\">" +
                "%param;" +
                "]>\n" +
                "<root> <url>&url;</url></root>";
        // ReaderStub.used = false;

        TransformerFactory transFactory = TransformerFactory.newInstance();
        transFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        transFactory.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
        Transformer transformer = transFactory.newTransformer();
        InputSource in = new InputSource(new StringReader(xml));
        SAXSource source = new SAXSource(in);
        StreamResult result = new StreamResult(new StringWriter());

        transformer.transform(source, result);
    }
}
