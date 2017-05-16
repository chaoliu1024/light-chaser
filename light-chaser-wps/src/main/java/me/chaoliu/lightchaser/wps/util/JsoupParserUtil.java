package me.chaoliu.lightchaser.wps.util;

import com.sun.org.apache.xerces.internal.dom.ElementImpl;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Attribute;
import org.jsoup.select.NodeTraversor;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.namespace.QName;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;
import java.io.StringWriter;

@Slf4j
public class JsoupParserUtil {

    protected final static DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();

    private final static XPath xPath = XPathFactory.newInstance().newXPath();

    protected static TransformerFactory tf = TransformerFactory.newInstance();

    /**
     * 得到该节点的子节点个数
     *
     * @param ele
     * @param xpath
     * @return
     */
    public static int getEleChildNum(org.jsoup.nodes.Element ele, String xpath) {
        try {
            Object res = parse(ele, xpath, XPathConstants.NODESET);
            if (null != res && res instanceof NodeList) {
                NodeList nodeList = (NodeList) res;
                return nodeList == null ? 0 : nodeList.getLength();
            }
        } catch (Exception e) {
        }
        return 0;
    }

    /**
     * 判断文档中是否存在xpath节点
     *
     * @param xpath
     * @return
     */
    public static boolean exists(org.jsoup.nodes.Element ele, String xpath) {
        try {
            Object res = parse(ele, xpath, XPathConstants.BOOLEAN);
            if (null != res && res instanceof Boolean) {
                return (boolean) res;
            }
            return false;
        } catch (Exception e) {
            log.error("检查xpath:{}，是否存在时出现错误,！", xpath);
            log.debug("plugin: {}", JsoupParserUtil.class.getSimpleName());
        }
        return false;
    }

    /**
     * 根据xpath得到w3c的Element对象
     *
     * @param xpath
     * @return
     */
    public static ElementImpl getW3cElementImpl(org.jsoup.nodes.Element ele, String xpath) {
        try {
            Object res = parse(ele, xpath, XPathConstants.NODE);
            if (null != res && res instanceof ElementImpl) {
                return (ElementImpl) res;
            }
            return null;
        } catch (Exception e) {
            log.error("检查xpath:{}，是否存在时出现错误,！" + e.getMessage(), xpath);
        }
        return null;
    }

    /**
     * 根据xpath得到jsoup的Element对象
     *
     * @param xpath
     * @return
     */
    public static org.jsoup.nodes.Element getJsoupElement(org.jsoup.nodes.Element ele, String xpath) {
        try {
            Object res = parse(ele, xpath, XPathConstants.NODE);
            if (null != res && res instanceof ElementImpl) {
                ElementImpl elementImpl = (ElementImpl) res;
                return getJsoupEle(elementImpl);
            }
            return null;
        } catch (Exception e) {
            log.error("根据xpath：{}，得到jsoup的Element对象出现错误，原因：", e.getMessage(), xpath);
        }
        return null;
    }

    /**
     * 得到节点的某一个属性值
     *
     * @param xpath
     * @return
     */
    public static String getXpathString(org.jsoup.nodes.Element ele, String xpath) {
        try {
            Object res = parse(ele, xpath, XPathConstants.STRING);
            if (null != res) {
                return res.toString();
            }
            return null;
        } catch (Exception e) {
            log.error("检查xpath:{}，是否存在时出现错误,！" + e.getMessage(), xpath);
        }
        return null;
    }

    /**
     * 获取xpath解析结果
     *
     * @param doc
     * @param xPathStr
     * @param qName
     * @return
     */
    public static Object parse(org.jsoup.nodes.Element doc, String xPathStr, QName qName) {
        Document document = fromJsoup(doc);
        return parse(document, xPathStr, qName);
    }

    public static Object parse(Document doc, String xPathStr, QName qName) {
        try {
            if (doc == null) {
                log.warn("解析文档为null！");
                return null;
            }
            if (StringUtils.isBlank(xPathStr)) {
                log.warn("解析的Xpath路径为空！");
                return null;
            }
            if (null == qName) {
                log.warn("解析类型为null！");
                return null;
            }
            return xPath.evaluate(xPathStr, doc, qName);
        } catch (Exception e) {
            log.warn("解析Xpath：{}，出现错误,解析类型：{}，错误原因：{}！", xPathStr, qName, e.getMessage());
        }
        return null;
    }

    /**
     * 根据ElementImpl得到Jsoup的Element
     *
     * @param elementImpl
     * @return
     */
    public static org.jsoup.nodes.Element getJsoupEle(ElementImpl elementImpl) {
        try {
            String value = getW3cDocString(elementImpl);
            org.jsoup.nodes.Document document = Jsoup.parse(value);
            return document.body().child(0);
        } catch (Exception e) {
            // TODO: handle exception
            log.error("根据ElementImpl得到Jsoup的Element出现错误，错误原因：{}", e.getMessage());
            return null;
        }

    }

    /**
     * 将w3c的Document转为jsoup的Document
     *
     * @return
     */
    public static org.jsoup.nodes.Document fromW3C(final Document doc) throws Exception {
        String string = getW3cDocString(doc);
        org.jsoup.nodes.Document res = Jsoup.parse(string);
        return res;

    }

    /**
     * 将jsoup的Document转为w3c的Document
     *
     * @param in
     * @return
     */
    public static Document fromJsoup(final org.jsoup.nodes.Element in) {
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document out = builder.newDocument();
            org.jsoup.nodes.Element rootEl = in.child(0);
            NodeTraversor traversor = new NodeTraversor(new W3CBuilder(out));
            traversor.traverse(rootEl);
            return out;
        } catch (ParserConfigurationException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 将W3c的doc转为字符串
     *
     * @param doc
     * @return
     * @throws Exception
     */
    public static String getW3cDocString(final Node doc) throws Exception {
        try (StringWriter writer = new StringWriter()) {
            DOMSource domSource = new DOMSource(doc);
            StreamResult result = new StreamResult(writer);
            Transformer transformer = tf.newTransformer();
            transformer.transform(domSource, result);
            return writer.toString();
        } catch (TransformerException e) {
            throw new IllegalStateException(e);
        }
    }

    /**
     * 将Jsoup的node属性拷贝到w3c的Element中
     *
     * @param source
     * @param el
     */
    public static void copyAttributes(final org.jsoup.nodes.Node source, final Element el) {
        for (Attribute attribute : source.attributes()) {
            el.setAttribute(attribute.getKey(), attribute.getValue());
        }
    }

}