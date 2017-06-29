import java.util.ArrayList;

import org.w3c.dom.Document;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		String sourceDir = "C:\\Users\\yliu40\\Project\\Ead\\";
		String targetDir = "C:\\Users\\yliu40\\Project\\extref\\";
		XMLFileUtil xmlFileUtil = new XMLFileUtil();
		
		ArrayList<String> xmlFileNames = xmlFileUtil.getXMLFiles(sourceDir);
		for (int i = 0; i < xmlFileNames.size(); i++) {
			String xmlFileName = xmlFileNames.get(i);
			String xmlStr = xmlFileUtil.getXMLStr(sourceDir + xmlFileName);
			
			xmlStr = xmlFileUtil.replaceHTMLEntity(xmlStr);
			xmlStr = xmlFileUtil.fixHtmlTag(xmlStr);
			xmlStr = xmlFileUtil.fixCollectionWikiURL(xmlStr);
			Document doc = xmlFileUtil.strToDocument(xmlStr);
			doc = xmlFileUtil.fixLinkAttr(doc);
			xmlFileUtil.outputXMLFile(doc, targetDir + xmlFileName);
		}
	}

}
