import java.io.File;
import java.util.ArrayList;

public class XMLFile {
	
	public ArrayList<File> getXMLFiles(String directoryPath) {
		
		ArrayList<File> fileList = new ArrayList<File>();
		File file = new File(directoryPath);
		
		if (file.isDirectory()) {
			File[] files = file.listFiles();
			
			for (File xmlFile : files) {
				fileList.add(xmlFile);
			}
		}
		
		System.out.println("Number of xmlFiles: " + fileList.size());
		return fileList;
	}
}
