
public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String aString = "&amp;lt;&lt;";
		XMLFileUtil xmlFileUtil = new XMLFileUtil();
		aString = xmlFileUtil.replaceHTMLEntity(aString);
		System.out.println("Decoded string: " + aString);
	}

}
