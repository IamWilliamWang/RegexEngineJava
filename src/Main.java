import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {
	/*
	 * 方括号转圆括号。
	 */
	private static String converter(String regex) {
		if (regex.indexOf('[') == -1)
			return regex;
		StringBuilder str = new StringBuilder(regex.replace('[', '(').replace(']', ')'));
		int leftBracketIndex = regex.indexOf('['); // 左括号所处的位置。bracket:括号
		int rightBracketIndex = regex.indexOf(']'); // 右括号所处的位置
		// 给字符和字符之间加|
		for (int insertIndex = rightBracketIndex - 1; insertIndex > leftBracketIndex + 1; insertIndex--) {
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

	@SuppressWarnings("resource")
	public static void main(String[] args) {
		System.out.print("Regex:");
		String myRegularExpression = new Scanner(System.in).nextLine(); // 正则表达式
		Nfa nfa = new Nfa(converter(myRegularExpression)); // 构建NFA
		try {
			String[] textLines = readAllLines("InputFile.txt");
			for (int i = 0; i < textLines.length; i++) {
				if (nfa.match(textLines[i]) != Status.FAIL) {
					String matchedStr = nfa.matchedList.getLast(); // 每次匹配成功后matchedList会多一个，取保存的最后一个
					int startPosition = textLines[i].indexOf(matchedStr); // 搜索开始位置
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
