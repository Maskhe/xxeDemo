import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.xml.sax.SAXException;

import java.io.File;

public class Main {
    public static void main(String[] args) throws DocumentException, SAXException {
        SAXReader reader = new SAXReader();
        // reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true);
        reader.setFeature("http://xml.org/sax/features/external-general-entities", false);
        reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        File xmlfile = new File("/Users/momo/IdeaProjects/xxeDemo/src/main/java/xee.xml");
        reader.read(xmlfile);
    }
}
