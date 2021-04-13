import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws JDOMException, IOException {
        String fileName = "/Users/momo/IdeaProjects/xxeDemo/src/main/java/data.xml";
        SAXBuilder builder = new SAXBuilder();
        builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl",true);
        builder.setFeature("http://xml.org/sax/features/external-general-entities", false);
        builder.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
        builder.setExpandEntities(false);
        Document doc = builder.build(new File(fileName));
    }
}
