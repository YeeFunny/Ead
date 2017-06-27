import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
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
	 * @param directoryPath
	 * 	Path of the directory
	 * 	
	 * @return fileList
	 * 	An arrayList with all XML files in the directory
	 */
	public ArrayList<File> getXMLFiles(String dirPath) {
		
		ArrayList<File> fileList = new ArrayList<File>();
		File file = new File(dirPath);
		
		if (file.isDirectory()) {
			File[] files = file.listFiles(xmlFilter);
			for (File xmlFile : files)
				fileList.add(xmlFile);
			
		} else
			fileList.add(file);

		return fileList;
	}
	
	
	/**
	 * Update date tag, modify year-00-00 to year-01-01
	 * 
	 * @param xmlFile
	 */
	public void updateDate(File xmlFile, String filePath) {
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			
			String dateExpression = "00-00$";
			Pattern pattern = Pattern.compile(dateExpression);
			NodeList dateList = doc.getElementsByTagName("date");
			
			for (int i = 0; i < dateList.getLength(); i++) {
				Node dateNode = dateList.item(i);
				if (dateNode.getNodeType() == Node.ELEMENT_NODE) {
					String dateValue = dateNode.getTextContent();
					NamedNodeMap dateAttrs = dateNode.getAttributes();
					Node normalAttr = dateAttrs.getNamedItem("normal");
					String normalValue = normalAttr.getTextContent().trim();
					if (pattern.matcher(dateValue).find())
						dateNode.setTextContent(dateValue.replace("00-00", "01-01"));
					if (pattern.matcher(normalValue).find())
						normalAttr.setTextContent(normalValue.replace("00-00", "01-01"));
				}
			}
			XMLFileUtil.updateXMLFile(doc, filePath);
			
		} catch (ParserConfigurationException parserConfigurationException) {
			parserConfigurationException.printStackTrace();
		} catch (SAXException saxException) {
			saxException.printStackTrace();
		} catch (IOException ioException) {
			ioException.printStackTrace();
		}
	}
	
	
	/**
	 * Update XML file
	 * @param doc
	 * @param filePath
	 */
	private static void updateXMLFile(Document doc, String filePath) {
		try {
			TransformerFactory transformerFactory = TransformerFactory.newInstance();
			Transformer transformer = transformerFactory.newTransformer();
			DOMSource source = new DOMSource(doc);
			StreamResult result = new StreamResult(new File(filePath));
			
			transformer.transform(source, result);
			
		} catch (TransformerConfigurationException transformerConfigurationException) {
			transformerConfigurationException.printStackTrace();
		} catch (TransformerException transformerException) {
			// TODO Auto-generated catch block
			transformerException.printStackTrace();
		}
	}
}
