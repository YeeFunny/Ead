import java.io.File;
import java.util.ArrayList;

import org.w3c.dom.Document;

public class TestReadXMLFiles {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		
		XMLFileUtil xmlUtil = new XMLFileUtil();
		String sourceDir = "C:\\Users\\yliu40\\Project\\Ead\\";
		String targetDir = "C:\\Users\\yliu40\\Project\\finish\\";
		
		ArrayList<File> xmlFiles = xmlUtil.getXMLFiles(sourceDir);
		Document doc;
		for (int i = 0; i < xmlFiles.size(); i++) {
			File xmlFile = xmlFiles.get(i);
			String fileName = xmlFile.getName().trim();
		}
	}

}