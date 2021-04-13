import java.net.URL;
import oracle.xml.parser.v2.DOMParser;
import oracle.xml.parser.v2.DTD;

public class Main {
    public static void main(String[] args){
        DTD dtdObj = new DTD();
        DOMParser domParser = new DOMParser();
        domParser.setAttribute(DOMParser.EXPAND_ENTITYREF, false);
        domParser.setAttribute(DOMParser.DTD_OBJECT, dtdObj);
        domParser.setAttribute(DOMParser.ENTITY_EXPANSION_DEPTH, 12);
        URL url = null;
        try{
            url = new URL("file:///Users/momo/IdeaProjects/xxeDemo/src/main/java/data.xml");
        }catch(Exception e){
            e.printStackTrace();
        }
        try{
            domParser.parse(url);

        }catch(Exception e){
            e.printStackTrace();
        }
    }
}
