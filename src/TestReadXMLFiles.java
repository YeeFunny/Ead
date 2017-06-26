import java.io.*;
import java.util.regex.Pattern;

import javax.xml.parsers.*;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

public class TestReadXMLFiles {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		try {
			File xmlFile = new File("C:\\Users\\yliu40\\Project\\Ead\\Anne-Townsend-Dudley-Papers-excel-ead.xml");
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(xmlFile);
			doc.getDocumentElement().normalize();
			
			NodeList dateList = doc.getElementsByTagName("date");
			
			String expression = "00-00$";
			Pattern pattern = Pattern.compile(expression);
			
			for (int i = 0; i < dateList.getLength(); i++) {
				Node dateNode = dateList.item(i);
				if (dateNode.getNodeType() == Node.ELEMENT_NODE) {
					Element dateElement = (Element)dateNode;
					if (pattern.matcher(dateElement.getAttribute("normal").trim()).find()) {
						dateElement.getAttributeNode("normal").setNodeValue("1222-01-01");
					}
					
					if (pattern.matcher(dateElement.getTextContent().trim()).find()) {
						System.out.println("Text find 00-00");
					}
				}
			}
			
		} catch (ParserConfigurationException | SAXException | IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

}
