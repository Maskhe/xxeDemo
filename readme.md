### 公众号：一个安全研究员
### Java常见xxe漏洞示例代码

源码中的xml文件我是用的本机的绝对地址，请修改为你的实际地址

除此之外，xml文件中的dnslog地址也是我自建的dnslog平台，请将其替换为公开的或者你自己的dnslog平台😬

### 解释

### DocumentBuilderFactory
全限定名称：javax.xml.parsers.DocumentBuilderFactory
安全使用建议：
```java
//禁用dtd
final String FEATURE1 = "http://apache.org/xml/features/disallow-doctype-decl"; // 禁用外部通用实体 
final String FEATURE2 = "http://xml.org/sax/features/external-general-entities";  
// 禁用外部参数实体
final String FEATURE3 = "http://xml.org/sax/features/external-parameter-entities";  
DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();  
dbf.setFeature(FEATURE1, true);  
// 要么disallow-doctype-decl,要么就禁用下面两个选项  
dbf.setFeature(FEATURE2, false);  
dbf.setFeature(FEATURE3, false);
```

在将disallow-doctype-decl设置为true时，会直接禁用dtd，也就不会手xxe的影响啦,如果你的xml文档中存在dtd，会直接爆出如下错误：
`[Fatal Error] :2:10: 将功能 "http://apache.org/xml/features/disallow-doctype-decl" 设置为“真”时, 不允许使用 DOCTYPE。`

将external-general-entities设置为false只会禁用掉通用实体的使用，也就是下面这个
```xml
<?xml version="1.0" encoding="utf-8" ?>  
<!DOCTYPE root [<!ENTITY url SYSTEM "http://httplog.xxx.tech:8080/httplog/xxe">  
]>  
<root>  
 <url>&url;</url>   
</root>
```

dnslog平台将不会有记录，但是此时参数实体依然是可以使用的，要禁用掉参数实体我们还得将`external-parameter-entities`属性设置为false

然后还有个禁用外部dtd的属性，我觉得这个没必要使用，也就不展开了
```java
FEATURE = "http://apache.org/xml/features/nonvalidating/load-external-dtd"; 
dbf.setFeature(FEATURE, false);
```

### XMLInputFactory
全限定名称：javax.xml.stream.XMLInputFactory

安全使用建议：

```java
// This disables DTDs entirely for that factory 
xmlInputFactory.setProperty(XMLInputFactory.SUPPORT_DTD, false); 
// disable external entities 
xmlInputFactory.setProperty("javax.xml.stream.isSupportingExternalEntities", false);

```
第一条属性设置为false可以禁用掉dtd，具体表现是什么样的呢，就是即使我xml文档里有dtd也不会被解析
```xml
<?xml version="1.0" encoding="utf-8" ?>  
<!DOCTYPE root [<!ENTITY url SYSTEM "http://httplog.sicnu.tech:8080/httplog/xxe">   
        ]>  
<root>  
 <url>&url;</url>  
</root>
```
当我将SUPPORT_DTD设置为false时，解析上面这个xml会报如下错误：
`javax.xml.stream.XMLStreamException: ParseError at [row,col]:[9,15]
Message: 引用了实体 "url", 但未声明它。`

通过报错可以推断dtd根本就没被解析

而如果将isSupportingExternalEntities这个属性设置为false的话，所有的外部的外部实体都将**不会被加载**，也就是如果使用dnslog来检测漏洞的话，根本就不会有网络请求发出，dnslog平台也就不会有记录

在针对xxe以及xee的防护上，上面两个属性任选其一就可以了

### SAXParserFactory
全限定名称：javax.xml.parsers.SAXParserFactory

安全开发建议：
```java
SAXParserFactory spf = SAXParserFactory.newInstance();  
 spf.setFeature("http://xml.org/sax/features/external-general-entities", false);  
spf.setFeature("http://xml.org/sax/features/external-parameter-entities", false);  
spf.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);  
  
 SAXParser saxParser = spf.newSAXParser();
```

这几个属性是不是看着很眼熟，和DocumentBuilderFacotory哪几个属性有点相似

将external-general-entities设置为false可以禁用外部通用实体，但是无法禁用掉外部参数实体，如果想禁用掉参数实体得将external-parameter-entities也设置为false

然后第三个属性还是用来禁用外部dtd的,如果不禁用外部dtd可能带来ssrf问题

可以加载外部dtd,那么就是可以发起外部请求，那不就是一个ssrf吗

除此之外还会引入dos问题,比如像下面这个情况
```xml
<?xml version="1.0"?>
     <!DOCTYPE lolz [
     <!ENTITY lol "lol">
     <!ENTITY lol2 "&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;">
     <!ENTITY lol3 "&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;">
     <!ENTITY lol4 "&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;">
     <!ENTITY lol5 "&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;">
     <!ENTITY lol6 "&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;">
     <!ENTITY lol7 "&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;">
     <!ENTITY lol8 "&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;">
     <!ENTITY lol9 "&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;">
     ]>
<lolz>&lol9;</lolz>
```

插入一篇文章，整理的很不错：https://xz.aliyun.com/t/3357

### TransformerFactory
全限定名称：javax.xml.transform.TransformerFactory
安全开发建议：

```java
TransformerFactory tf = TransformerFactory.newInstance(); 
// 禁用所有需要外部请求的操作，外部通用实体、外部参数实体都失效
tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); 
tf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, "");
```

`XMLConstants.ACCESS_EXTERNAL_DTD`设置为空会禁用所有需要外部请求的操作，外部通用实体、外部参数实体都失效，会报如下错误：
`ERROR:  '外部实体: 无法读取外部文档 'xxe', 因为 accessExternalDTD 属性设置的限制导致不允许 'http' 访问。'`

第二个属性没搞懂是干啥的，但是在防御xxe方面还是第一个属性管用，而且好像设置了第一个就行了？

### SchemaFactory
全限定名称：javax.xml.validation.SchemaFactory
安全开发建议：

```java
SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema"); 
factory.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, ""); 
factory.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, ""); 
Schema schema = factory.newSchema(Source);
```

第一个属性可以禁用外部dtd的使用，包括外部参数实体以及外部通用实体，当将该属性设置为空时，如果xml文档中存在外部实体，会报错：
`外部实体: 无法读取外部文档 'xxe', 因为 accessExternalDTD 属性设置的限制导致不允许 'http' 访问`
小小的截个图吧：
![image](https://user-images.githubusercontent.com/30264078/114492393-1db7d800-9c4b-11eb-84c1-3115bb801cf8.png)


第二个属性我依然没弄清楚...,诚待有缘人解决

### SAXTransformerFactory
全限定名称：javax.xml.transform.sax.SAXTransformerFactory
安全开发建议：

```java
SAXTransformerFactory sf = SAXTransformerFactory.newInstance(); 
sf.setAttribute(XMLConstants.ACCESS_EXTERNAL_DTD, ""); 
sf.setAttribute(XMLConstants.ACCESS_EXTERNAL_STYLESHEET, ""); 
sf.newXMLFilter(Source);
```

查了半天没搞懂这玩意咋用的,先搁置QAQ

### DOMParser

全限定名称：oracle.xml.parser.v2.DOMParser

根据owasp给出的修复建议是：

```java
// Extend oracle.xml.parser.v2.XMLParser 
DOMParser domParser = new DOMParser(); 
// Do not expand entity references 
domParser.setAttribute(DOMParser.EXPAND_ENTITYREF, false); 
// dtdObj is an instance of oracle.xml.parser.v2.DTD 
domParser.setAttribute(DOMParser.DTD_OBJECT, dtdObj); 
// Do not allow more than 11 levels of entity expansion 
domParser.setAttribute(DOMParser.ENTITY_EXPANSION_DEPTH, 12);
```

第一个属性是禁止引用外部实体，但是这里的禁止并不是说不能发出网络请求，而是如果响应中返回了一个实体，这个实体是不能被使用的，只是靠文字阐述不太好理解，我们直接来看现象吧

```java
public class DomParserDemo {  
    public static void main(String[] args){  
        DtdObj dtdObj = new DtdObj();  
 DOMParser domParser = new DOMParser();  
 domParser.setAttribute(DOMParser.EXPAND_ENTITYREF, false);  
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
```

其中data.xml文件内容如下：

```xml
<?xml version="1.0" encoding="utf-8" ?>  
<!DOCTYPE root [<!ENTITY url SYSTEM "http://httplog.xxx:8080/httplog/xxe">  
 <!ENTITY % param SYSTEM "/Users/momo/IdeaProjects/xxeDemo/src/main/resources/text.txt">  
	%param;]>  
<root>  
 <url>&url;</url>  
 <content>&content;</content>  
</root>
```

text.txt文件内容如下：

```xml
<!ENTITY content "123">
```

运行代码，报错如下：

`oracle.xml.parser.v2.XMLParseException; systemId: file:/Users/momo/IdeaProjects/xxeDemo/src/main/java/data.xml; lineNumber: 10; columnNumber: 23; Missing entity 'content'`

也就是说text.txt文件中的外部实体确实没有被识别了，但是我们还是可以在dnslog平台上看到下面这样一条记录：

![image](https://user-images.githubusercontent.com/30264078/114492336-ff51dc80-9c4a-11eb-8d62-d5603e460174.png)

综上，可以看到，即使设置了`DOMParser.EXPAND_ENTITYREF`为false,也无法阻止攻击者通过dnslog平台找到该漏洞，如果这个漏洞又是一个有回显的xxe,那么这个属性就没有啥用了，如果是一个无回显的xxe，倒是可以防止攻击者利用外部实体读文件。

第二个属性应该是指定xml文档的DTD用的，建议方法里说这个对象得是`oracle.xml.parser.v2.DTD`的实例，但是我操作过后，发现这玩意并没有用，应该还是使用方法不对（我猜想这个属性就是写死每个xml文档的DTD用的，这样就不怕xml实体注入了）

第三个属性就是为了防止实体膨胀用的，也就是防止xee漏洞的

### SAXReader

全限定名称：org.dom4j.io.SAXReader
安全开发建议：

```java
saxReader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); 
saxReader.setFeature("http://xml.org/sax/features/external-general-entities", false); 
saxReader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
```

这个就和DocumentBuilderFactory几个属性一样了，不展开啦


### SAXBuilder

全限定名称：org.jdom2.input.SAXBuilder
安全开发建议：
```java
SAXBuilder builder = new SAXBuilder(); 
builder.setFeature("http://apache.org/xml/features/disallow-doctype-decl",true); 
builder.setFeature("http://xml.org/sax/features/external-general-entities", false); 
builder.setFeature("http://xml.org/sax/features/external-parameter-entities", false); 
builder.setExpandEntities(false);
Document doc = builder.build(new File(fileName));
```

前面三个属性相信大家已经很熟悉了，而setExpandEntities(false)视为了防止xee攻击的

当我们设置了这个属性，如果攻击者输入了xee的payload

```xml
<?xml version="1.0"?>
     <!DOCTYPE lolz [
     <!ENTITY lol "lol">
     <!ENTITY lol2 "&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;&lol;">
     <!ENTITY lol3 "&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;&lol2;">
     <!ENTITY lol4 "&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;&lol3;">
     <!ENTITY lol5 "&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;&lol4;">
     <!ENTITY lol6 "&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;&lol5;">
     <!ENTITY lol7 "&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;&lol6;">
     <!ENTITY lol8 "&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;&lol7;">
     <!ENTITY lol9 "&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;&lol8;">
     ]>
<lolz>&lol9;</lolz>
```

则会报如下错误：

![image](https://user-images.githubusercontent.com/30264078/114492309-f06b2a00-9c4a-11eb-867f-33902543c133.png)


### XMLReaderFactory

全限定名称：org.xml.sax.XMLReader
安全开发建议：

```java
XMLReader reader \= XMLReaderFactory.createXMLReader(); 
reader.setFeature("http://apache.org/xml/features/disallow-doctype-decl", true); 
// This may not be strictly required as DTDs shouldn't be allowed at all, per previous line.
reader.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false); 
reader.setFeature("http://xml.org/sax/features/external-general-entities", false); 
reader.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
```

load-external-dtd属性仅仅可以禁用外部dtd,不能禁用外部实体，这和前面我们遇到的几个是有差异的

然后后面两个属性的作用想必大家已经很熟了

### Validator

全限定名称：javax.xml.validation.Validator
安全开发建议：

```java
SchemaFactory factory = SchemaFactory.newInstance("http://www.w3.org/2001/XMLSchema"); 
Schema schema = factory.newSchema(); 
Validator validator = schema.newValidator(); 
validator.setProperty(XMLConstants.ACCESS_EXTERNAL_DTD, ""); 
validator.setProperty(XMLConstants.ACCESS_EXTERNAL_SCHEMA, "");
```

同样的，在将ACCESS_EXTERNAL_DTD设置为空时，对于外部实体的访问会报错：

`外部实体: 无法读取外部文档 'param', 因为 accessExternalDTD 属性设置的限制导致不允许 'http' 访问。`



