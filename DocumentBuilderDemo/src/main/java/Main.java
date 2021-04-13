import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class Main {
    // DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
    public static void main(String[] args) throws ParserConfigurationException {
        final String FEATURE1 = "http://apache.org/xml/features/disallow-doctype-decl";
        final String FEATURE2 = "http://xml.org/sax/features/external-general-entities";
        final String FEATURE3 = "http://xml.org/sax/features/external-parameter-entities";
        final String FEATURE4 = "http://apache.org/xml/features/nonvalidating/load-external-dtd";
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
//        dbf.setFeature(FEATURE1, true);
        // 要么disallow-doctype-decl,要么就禁用下面两个选项
//        dbf.setFeature(FEATURE2, false);
//        dbf.setFeature(FEATURE3, false);
        dbf.setFeature(FEATURE4, false);
        DocumentBuilder db = dbf.newDocumentBuilder();

        try {
            InputStream input = new FileInputStream("/Users/momo/IdeaProjects/xxeDemo/resources/data.xml");
            Document doc = db.parse(input);
            NodeList nodeList = doc.getElementsByTagName("root");
            Element element = (Element) nodeList.item(0);
            System.out.println(element);
            System.out.println(element.getElementsByTagName("content").item(0).getFirstChild().getNodeValue());
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}

