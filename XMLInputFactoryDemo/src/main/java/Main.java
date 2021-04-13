import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, XMLStreamException {
        FileInputStream file =  new FileInputStream("/Users/momo/IdeaProjects/xxeDemo/src/main/java/data.xml");
        XMLInputFactory xmlFactory = XMLInputFactory.newInstance();
        //xmlFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false);
        xmlFactory.setProperty("javax.xml.stream.isSupportingExternalEntities", false);
        XMLStreamReader reader  = xmlFactory.createXMLStreamReader(file);
        try{
            while(reader.hasNext()){
                int point = reader.next();
                switch (point){
                    case XMLStreamReader.START_ELEMENT:
                        String eleName = reader.getName().toString();
                        System.out.println(eleName);
                        if("users".equals(eleName)){
                            int count = reader.getAttributeCount();
                            for(int i = 0;i<count; i++){
                                System.out.printf("%s:%s\t", reader.getAttributeName(i), reader.getAttributeValue(i));
                            }
                        }
                    default:
                }

            }
        }catch (Exception  e){
            e.printStackTrace();
        }

    }
}
