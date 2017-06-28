import java.io.File;
import java.util.ArrayList;

import org.w3c.dom.Document;

public class TestReadXMLFiles {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		XMLFileUtil xmlUtil = new XMLFileUtil();
		String sourceDir = "//home//yifang//Documents//ead_file//";
		String targetDir = "//home//yifang//Documents//test//";
		
		ArrayList<File> xmlFiles = xmlUtil.getXMLFiles(sourceDir);
		File xmlFile;
		Document doc;
		for (int i = 0; i < xmlFiles.size(); i++) {
			xmlFile = xmlFiles.get(i);
			String fileName = xmlFile.getName().trim();
			doc = xmlUtil.replaceHTMLEntity(xmlFile, sourceDir + fileName);
			XMLFileUtil.outputXMLFile(doc, targetDir + fileName);
		}
	}

}