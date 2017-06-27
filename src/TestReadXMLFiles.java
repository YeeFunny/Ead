import org.apache.commons.text.StringEscapeUtils;

public class TestReadXMLFiles {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String xml = "&amp;lt;a&gt;";
		String unxml = StringEscapeUtils.unescapeHtml3(xml);
		System.out.print("Unescaped: " + unxml);
	}

}