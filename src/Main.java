import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
	//方括号转圆括号。方括号出bug了
	private static String converter(String regex) {
		if(regex.indexOf('[')==-1)
			return regex;
		StringBuilder str=new StringBuilder(regex.replace('[', '(').replace(']', ')'));
		int 左括号Index=regex.indexOf('[');
		int 右括号Index=regex.indexOf(']');
		for(int insertIndex=右括号Index-1;insertIndex>左括号Index+1;insertIndex--) {
			str.insert(insertIndex, '|');
		}
		return str.toString();
	}
	private static String[] ReadAllLines(String filename) throws IOException {
		FileReader reader = new FileReader(filename);
		BufferedReader buffer = new BufferedReader(reader);
		ArrayList<String> lines = new ArrayList<>();
		String lineStr;
		while(null!=(lineStr=buffer.readLine())) {
			lines.add(lineStr);
		}
		return lines.toArray(new String[] {});
	}
	
	public static void main(String[] args) {
		String myRegularExpression = "a*[01]";
		Nfa nfa = new Nfa(converter(myRegularExpression));
		try {
			String[] textLines = ReadAllLines("InputFile.txt");
			for(int i=0;i<textLines.length;i++) {
				if(nfa.match(textLines[i])!=0) {
					String matchedStr = nfa.matchedList.get(nfa.matchedList.size()-1);
					int startPosition = textLines[i].indexOf(matchedStr);
					System.out.println("Match found on line "+(i+1)+", starting at position "
					+(startPosition+1)+" and ending at position "+(startPosition+matchedStr.length())
					+": "+matchedStr);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
