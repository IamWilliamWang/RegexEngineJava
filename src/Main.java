import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

public class Main {
	/*
	 * 方括号转圆括号。
	 */
	private static String converter(String regex) {
		if (regex.indexOf('[') == -1)
			return regex;
		StringBuilder str = new StringBuilder(regex.replace('[', '(').replace(']', ')'));
		int 左括号Index = regex.indexOf('[');
		int 右括号Index = regex.indexOf(']');
		for (int insertIndex = 右括号Index - 1; insertIndex > 左括号Index + 1; insertIndex--) {
			str.insert(insertIndex, '|');
		}
		return str.toString();
	}

	/*
	 * 读取文本所有行，返回字符串数组
	 */
	private static String[] readAllLines(String filename) throws IOException {
		FileReader reader = new FileReader(filename);
		BufferedReader buffer = new BufferedReader(reader);
		ArrayList<String> lines = new ArrayList<>();
		String lineStr;
		while (null != (lineStr = buffer.readLine())) {
			lines.add(lineStr);
		}
		return lines.toArray(new String[] {});
	}

	public static void main(String[] args) {
		String myRegularExpression = "a*[01]"; //正则表达式
		Nfa nfa = new Nfa(converter(myRegularExpression)); //构建NFA
		try {
			String[] textLines = readAllLines("InputFile.txt");
			for (int i = 0; i < textLines.length; i++) {
				if (nfa.match(textLines[i])!=Status.FAIL) {
					String matchedStr = nfa.matchedList.get(nfa.matchedList.size() - 1); //每次match成功后matchedList会多一个，取保存的最后一个
					int startPosition = textLines[i].indexOf(matchedStr); //搜索开始位置
					System.out.println("Match found on line " + (i + 1) + ", starting at position "
							+ (startPosition + 1) + " and ending at position " + (startPosition + matchedStr.length())
							+ ": " + matchedStr);
				}
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
