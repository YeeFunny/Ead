import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLFileUtil {
	
	// HTML 3.0 Entities, including &amp;
	private static final String[][] ESCAPES = {
		{"quot", "\""},			{"lt", "<"},			{"gt", ">"},
		{"amp", "&"},			{"nbsp", "\u00A0"},  	{"iexcl", "\u00A1"},
		{"cent", "\u00A2"},		{"pound", "\u00A3"}, 	{"curren", "\u00A4"},
		{"yen", "\u00A5"},		{"sect", "\u00A7"},  	{"uml", "\u00A8"},
		{"copy", "\u00A9"},		{"ordf", "\u00AA"},  	{"ordm", "\u00BA"},
		{"not", "\u00ac"},		{"shy", "\u00AD"},   	{"reg", "\u00AE"},
		{"macr", "\u00AF"},		{"deg", "\u00B0"},   	{"plusmn", "\u00B1"},
		{"acute", "\u00B4"},	{"micro", "\u00B5"}, 	{"para", "\u00B6"},
		{"middot", "\u00B7"},	{"cedil", "\u00B8"}, 	{"iquest", "\u00BF"},
		{"times", "\u00D7"},	{"divide", "\u00F7"}
	};
	private static final HashMap<String, String> lookupMap;
    static {
        lookupMap = new HashMap<String, String>();
        for (final CharSequence[] seq : ESCAPES) 
            lookupMap.put(seq[0].toString(), seq[1].toString());
    }
	
	// Filter XML files
	private FileFilter xmlFilter = new FileFilter() {
		public boolean accept(File file) {
			if (file.getName().toLowerCase().trim().endsWith(".xml"))
				return true;
			else
				return false;
		}
	};

	/**
	 * Get all XML files in the specified directory
	 * 
	 * @param sourceDir
	 * 	Path of the input directory
	 * 	
	 * @return
	 * 	An arrayList with all XML files in the directory
	 */
	public ArrayList<File> getXMLFiles(String sourceDir) {
		
		ArrayList<File> fileList = new ArrayList<File>();
		File file = new File(sourceDir);
		
		if (file.isDirectory()) {
			File[] files = file.listFiles(xmlFilter);
			for (File xmlFile : files)
				fileList.add(xmlFile);
			
		} else
			fileList.add(file);

		return fileList;
	}
	
	/**
	 * Convert XML file to String
	 * 
	 * @param sourcePath
	 * 	Path of the source XML file
	 * @return String
	 */
	public String getXMLStr(String sourcePath) {
		
		Path path = Paths.get(sourcePath);
		String xmlStr = "";
		try {
			xmlStr = new String(Files.readAllBytes(path), "utf8");
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
		return xmlStr;
	}
	
	/**
	 * Update date tag, replace year-00-00 with year-01-01
	 * 
	 * @param xmlFile
	 */
	public Document replaceDate(String xmlStr) {
		
		String dateExpression = "00-00$";
		Pattern pattern = Pattern.compile(dateExpression);
		Document doc = XMLFileUtil.strToDocument(xmlStr);
		NodeList dateList = doc.getElementsByTagName("date");
		
		for (int i = 0; i < dateList.getLength(); i++) {
			Node dateNode = dateList.item(i);
			if (dateNode.getNodeType() == Node.ELEMENT_NODE) {
				String dateValue = dateNode.getTextContent().trim();
				NamedNodeMap dateAttrs = dateNode.getAttributes();
				Node normalAttr = dateAttrs.getNamedItem("normal");
				String normalValue = normalAttr.getTextContent().trim();
				if (pattern.matcher(dateValue).find())
					dateNode.setTextContent(dateValue.replace("00-00", "01-01"));
				if (pattern.matcher(normalValue).find())
					normalAttr.setTextContent(normalValue.replace("00-00", "01-01"));
			}
		}
		return doc;
	}
	
	/**
	 * Replace HTML Entities in XML files with real symbols exclude ampersand
	 * 
	 * @param xmlStr
	 */
	public String replaceHTMLEntity(String xmlStr) {
		
		xmlStr = XMLFileUtil.fixDoubleEncoded(xmlStr);
		StringBuilder xmlStrBuilder = new StringBuilder(xmlStr);
		String htmlEntityExp = "&([a-zA-Z]{1,10});";
		Pattern pattern = Pattern.compile(htmlEntityExp);
		Matcher matcher = pattern.matcher(xmlStr);
		while (matcher.find()) {
			String htmlEntity = matcher.group(1).toLowerCase();
			if (lookupMap.containsKey(htmlEntity) && !htmlEntity.equals("amp")) {
				int start = matcher.start();
				int end = matcher.end();
				xmlStr = xmlStrBuilder.replace(start, end, lookupMap.get(htmlEntity)).toString();
				matcher.reset(xmlStr);
			}
		}
		xmlStr = xmlStrBuilder.toString();
		return xmlStr;
	}
	
	/**
	 * Fix the HTML entity double encoded problem by replace &amp; with &
	 * 
	 * @param xmlStr
	 * @return
	 * 	Decoded XML string
	 */
	private static String fixDoubleEncoded(String xmlStr) {
		
		String escapesStr = "";
		for (String htmlEntity : lookupMap.keySet()) {
			escapesStr = htmlEntity + "|" + escapesStr;
		}
		String doubleEncodedExp = "&amp;(" + escapesStr + ");";
		Pattern pattern = Pattern.compile(doubleEncodedExp);
		Matcher matcher = pattern.matcher(xmlStr);
		
		while (matcher.find()) {
			xmlStr = matcher.replaceAll("&$1;");
		}
		return xmlStr;
	} 
	
	/**
	 * Convert String to XML DOM
	 * 
	 * @param xmlStr
	 * @return Document
	 */
	private static Document strToDocument(String xmlStr) {
		
		DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
		Document doc = null;
		try {
			DocumentBuilder builder = factory.newDocumentBuilder();
			InputSource inputSource = new InputSource();
			inputSource.setCharacterStream(new StringReader(xmlStr));
			doc = builder.parse(inputSource);
			
		} catch (ParserConfigurationException pcException) {
			pcException.printStackTrace();
		} catch (SAXException saxException) {
			saxException.printStackTrace();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
		return doc;
	}
	
	/**
	 * Output XML file
	 * @param doc
	 * @param targetPath
	 */
	static void outputXMLFile(Document doc, String targetPath) {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(targetPath));
			
			transformer.transform(source, result);
			
		} catch (TransformerConfigurationException tfcException) {
			tfcException.printStackTrace();
		} catch (TransformerException tfException) {
			tfException.printStackTrace();
		}
	}
}
