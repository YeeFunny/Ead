import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Test {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		String source = "fdasfdasfd&&safd;asfd";
		String expression = "&[a-zA-Z]{1,10};";
		Pattern pattern = Pattern.compile(expression);
		Matcher matcher = pattern.matcher(source);
		
		int count = 0;
		
		while (matcher.find()) {
			count++;
			System.out.println("Match number: " + count);
			System.out.println("Start: " + matcher.start());
			System.out.println("End: " + matcher.end());
		}
	}

}
