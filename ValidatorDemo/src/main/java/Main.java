import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import javax.xml.validation.Validator;
import java.io.File;
import java.io.IOException;

public class Main {
    public static void main(String[] args) throws IOException, SAXException {
        String xml =  "/Users/momo/IdeaProjects/xxeDemo/src/main/java/data.xml";
        SchemaFactory factory =
                SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema");

        //解析作为模式的指定 File 并以 Schema 的形式返回它
        //此对象表示可以根据  XML 文档检查/实施的约束集
        File file = new File(xml);
        Schema schema = factory.newSchema();

        //验证器实施/检查此对象表示的约束集。Validator -> 根据 Schema 检查 XML 文档的处理器。
        Validator validator = schema.newValidator();
        validator.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, "");
        validator.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");

        //验证指定的输入。 Source -> 实现此接口的对象包含充当源输入（XML 源或转换指令）所需的信息
        Source source = new StreamSource(xml);
        validator.validate(source);

    }
}
