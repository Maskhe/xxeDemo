import org.xml.sax.*;
import org.xml.sax.helpers.DefaultHandler;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.transform.Source;
import javax.xml.transform.sax.SAXSource;
import java.io.IOException;
import java.io.StringReader;

public class Main {
    public static void main(String[] args) throws SAXException, ParserConfigurationException, JAXBException, IOException {
        //Disable XXE
        SAXParserFactory spf = SAXParserFactory.newInstance();
        spf.setFeature("http://xml.org/sax/features/external-general-entities", false);
        spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
//        spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);

        SAXParser saxParser = spf.newSAXParser();
        Handler handler = new Handler();
        saxParser.parse("/Users/momo/IdeaProjects/xxeDemo/src/main/java/data.xml", handler);
    }
}

class Handler extends DefaultHandler {

    private boolean isTest = false;

    public Handler() {
        // TODO Auto-generated constructor stub
    }


    /**
     * 开始节点，根据开始节点名建立相关条件或创建相关对象
     */
    public void startElement(String uri, String localName, String qName,
                             Attributes attributes) throws SAXException {
        // TODO Auto-generated method stub
        super.startElement(uri, localName, qName, attributes);

        if ("tests".equals(qName)) {
            isTest = true;
        } else if ("test".equals(qName)) {
            System.out.println("parser test start");
        }

    }

    /**
     * 结束节点，根据结束节点完成相关动作
     */
    public void endElement(String uri, String localName, String qName)
            throws SAXException {
        // TODO Auto-generated method stub
        super.endElement(uri, localName, qName);

        if ("tests".equals(qName)) {
            isTest = false;
        }
        System.out.println("parser test end");
    }

    /**
     * 节点间的文字信息，可通过stringbuffer收集每一个节点的文字信息，
     * 在endElement中清空stringbuffer，收集下一个
     */
    public void characters(char[] ch, int start, int length)
            throws SAXException {
        // TODO Auto-generated method stub
        super.characters(ch, start, length);
        String content = new String(ch, start, length);
        System.out.println("content = " + content);

    }
}