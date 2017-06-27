import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.io.StringReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
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

import org.apache.commons.text.StringEscapeUtils;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

public class XMLFileUtil {
	
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
	 * @param sourcePath
	 * 	Path of the input directory
	 * 	
	 * @return
	 * 	An arrayList with all XML files in the directory
	 */
	public ArrayList<File> getXMLFiles(String sourcePath) {
		
		ArrayList<File> fileList = new ArrayList<File>();
		File file = new File(sourcePath);
		
		if (file.isDirectory()) {
			File[] files = file.listFiles(xmlFilter);
			for (File xmlFile : files)
				fileList.add(xmlFile);
			
		} else
			fileList.add(file);

		return fileList;
	}
	
	
	/**
	 * Update date tag, replace year-00-00 with year-01-01
	 * 
	 * @param xmlFile
	 * @param targetPath
	 */
	public void updateDate(File xmlFile, String targetPath) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(xmlFile);
			
			String dateExpression = "00-00$";
			Pattern pattern = Pattern.compile(dateExpression);
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
			XMLFileUtil.outputXMLFile(doc, targetPath);
			
		} catch (ParserConfigurationException pcException) {
			pcException.printStackTrace();
		} catch (SAXException saxException) {
			saxException.printStackTrace();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	/**
	 * Replace HTML Entities in XML files with real symbols
	 * 
	 * @param xmlFile
	 * @param sourcePath
	 * @param targetPath
	 */
	public void replaceHTMLEntity(File xmlFile, String sourcePath, String targetPath) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			
			String xmlStr = XMLFileUtil.getXMLStr(sourcePath);
			
			while (XMLFileUtil.checkHTMLEntity(xmlStr)) {
				xmlStr = StringEscapeUtils.unescapeHtml3(xmlStr);
			}
			
			InputSource inputSource = new InputSource();
			inputSource.setCharacterStream(new StringReader(xmlStr));
			Document doc = builder.parse(inputSource);
			
			XMLFileUtil.outputXMLFile(doc, targetPath);
			
		} catch (ParserConfigurationException pcException) {
			pcException.printStackTrace();
		} catch (SAXException saxException) {
			saxException.printStackTrace();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	public void replceHTMLTag (File xmlFile) {
		
	}
	
	/**
	 * Convert XML file to String
	 * 
	 * @param sourcePath
	 * 	Path of the source XML file
	 * @return String
	 */
	private static String getXMLStr(String sourcePath) {
		
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
	 * Check if there is HTML entity in the XML file
	 * 
	 * @param sourcePath
	 * 	Path of the source XML file
	 * @return boolean
	 */
	private static boolean checkHTMLEntity(String sourcePath) {
		
		boolean hasHTMLEntity = false;
		String xmlStr = XMLFileUtil.getXMLStr(sourcePath);
		String htmlExpression = "&[a-zA-Z]{1,10};";
		Pattern pattern = Pattern.compile(htmlExpression);
		Matcher matcher = pattern.matcher(xmlStr);
		
		if (matcher.find())
			hasHTMLEntity = true;
		
		return hasHTMLEntity;
	}
	
	/**
	 * Output XML file
	 * @param doc
	 * @param filePath
	 */
	private static void outputXMLFile(Document doc, String sourcePath) {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(sourcePath));
			
			transformer.transform(source, result);
			
		} catch (TransformerConfigurationException tfcException) {
			tfcException.printStackTrace();
		} catch (TransformerException tfException) {
			tfException.printStackTrace();
		}
	}
}
