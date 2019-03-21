import java.util.ArrayList;
import java.util.LinkedList;
import java.util.ListIterator;

public class Nfa {
	String regex;
	State Start;
	State End;
	LinkedList<Edge> edgeList = new LinkedList<>();
	LinkedList<State> stateList = new LinkedList<>();
	LinkedList<Character> matchedChar = new LinkedList<>();
	public static CharArray regRead;
	public static CharArray fileRead;
	
	/*
	 * 构造函数
	 */
	public Nfa(String reg) {
		this.regex = reg;
		this.Start = new State();
		this.stateList.add(Start);
		if ((End = regex2nfa(regex, Start)) != null) {
			// System.out.println("NFA has built successfully!");
		}
		else
			System.out.println("NFA built failed, please check if the regular expression is right!");
	}
	
	/*
	 * content是否符合正则表达式
	 */
	public Status match(String content) {
		boolean everMatched = false;
		this.Start.status = Status.SUCCESS;
		fileRead = new CharArray(content);

		while (!fileRead.reachEnding()) {
			if (step(this.Start) == Status.FAIL) {
				fileRead.increase();
				matchedChar.clear();
				refresh();
				continue;
			}
			saveMatched();
			// printMatched();
			everMatched = true;
			refresh();
			matchedChar.clear();
		}
		if (everMatched)
			return Status.SUCCESS;

		return Status.FAIL;
	}

	private State regex2nfa(String reg, State start) {
		return regex2nfa(new CharArray(reg), start);
	}

	private State regex2nfa(CharArray reg, State start) {
		State currentEnd, currentStart = null;
		State alternate;
		ListIterator<Edge> itor;
		if (regex == null)
			return null;

		currentEnd = start;
		CharArray regRead = reg.cloneObject();
		while (!regRead.reachEnding()) {
			switch (regRead.getChar()) {
			case '.': /* any */
				currentStart = currentEnd;
				currentEnd = new State();
				newEdge(currentStart, currentEnd, Resource.ANY, Resource.NEXCLUDED);
				this.stateList.add(currentEnd);
				break;
			case '|': // alternate
				regRead.increase();
				currentStart = start;
				alternate = regex2nfa(regRead.getSubString(), start);
				currentEnd.merge(alternate);
				stateList.remove(alternate);
				regRead.decrease();
				break;
			case '?': // zero or one
				newEdge(currentStart, currentEnd, Resource.EPSILON, Resource.NEXCLUDED);
				break;
			case '*': // zero or more
				alternate = currentEnd;
				currentStart.merge(alternate);
				stateList.remove(alternate);
				currentEnd = currentStart;
				break;
			case '+': /* one or more */
				itor = currentStart.OutEdges.listIterator();
				while (itor.hasNext()) {
					Edge edge = itor.next();
					newEdge(currentEnd, edge.end, edge.type, edge.exclude);
				}
				break;
			case '(':
				regRead.increase();
				currentStart = currentEnd;
				currentEnd = regex2nfa(regRead.getSubString(), currentEnd);
				break;
			case ')':
				return currentEnd;
			case '[':
				regRead.increase();
				currentStart = currentEnd;
				if ((currentEnd = group(currentEnd)) == null)
					return null;
				break;
			case '^':
				regRead.increase();
				currentStart = currentEnd;
				currentEnd = new State();
				newEdge(currentStart, currentEnd, (int) regRead.getChar(), Resource.EXCLUDED);
				this.stateList.add(currentEnd);
				break;
			case '\\':
				regRead.increase();
				currentStart = start;
				if ((currentEnd = preDefine(currentEnd)) == null)
					return null;
				this.stateList.add(currentEnd);
				break;
			case '\t':
			case '\n':
			case '\f':
			case '\r':
				break;
			default:
				currentStart = currentEnd;
				currentEnd = new State();
				newEdge(currentStart, currentEnd, (int) regRead.getChar(), Resource.NEXCLUDED);
				this.stateList.add(currentEnd);
				break;
			}
			regRead.increase();
		}
		return currentEnd;
	}

	private State group(State top) {
		State s = new State();
		boolean ifexclude = Resource.NEXCLUDED;
		if (regRead.getChar() == '^') {
			regRead.increase();
			ifexclude = Resource.EXCLUDED;
		}
		for (; regRead.getChar() != ']'; regRead.increase()) {
			switch (regRead.getChar()) {
			case '0':
			case 'a':
			case 'A':
				regRead.increase();
				if (regRead.getChar() != '-') {
					System.out.println("NFA built failed, please check if the regular expression is right!");
					return null;
				}
				break;
			case '9':
				newEdge(top, s, Resource.NUM, ifexclude);
				break;
			case 'z':
				newEdge(top, s, Resource.LCASES, ifexclude);
				break;
			case 'Z':
				newEdge(top, s, Resource.UCASES, ifexclude);
				break;
			case '\\':
				regRead.increase();
				if ((s = preDefine(top)) == null)
					return null;
				break;
			default:
				System.out.println("NFA built failed, please check if the regular expression is right!");
				return null;
			}
		}
		this.stateList.add(s);
		return s;
	}

	private State preDefine(State top) {
		State s = new State();
		switch (regRead.getChar()) {
		case 'd':
			newEdge(top, s, Resource.NUM, Resource.NEXCLUDED);
			break;
		case 'D':
			newEdge(top, s, Resource.NUM, Resource.EXCLUDED);
			break;
		case 's':
			newEdge(top, s, Resource.WS, Resource.NEXCLUDED);
			break;
		case 'S':
			newEdge(top, s, Resource.WS, Resource.EXCLUDED);
			break;
		case 'w':
			newEdge(top, s, Resource.NUM, Resource.NEXCLUDED);
			newEdge(top, s, Resource.UCASES, Resource.NEXCLUDED);
			newEdge(top, s, Resource.LCASES, Resource.NEXCLUDED);
			break;
		case 'W':
			newEdge(top, s, Resource.NUM, Resource.EXCLUDED);
			newEdge(top, s, Resource.UCASES, Resource.EXCLUDED);
			newEdge(top, s, Resource.LCASES, Resource.EXCLUDED);
			break;
		default:
			System.out.println("NFA built failed, please check if the regular expression is right!");
			return null;
		}
		return s;
	}

	private void newEdge(State start, State end, int type, boolean exclude) {
		Edge out = new Edge(start, end, type, exclude);
		end.patch(out, end);
		start.patch(start, out);
		edgeList.add(out);
	}

	private Status step(State current) {

		if (End.status == Status.SUCCESS)
			return Status.SUCCESS;

		for (Edge edge : current.OutEdges) {
			if (edge.match(fileRead.getChar())) {
				edge.end.status = Status.SUCCESS;
				matchedChar.add(fileRead.getChar());
				fileRead.increase();
				if (step(edge.end) !=Status.FAIL)
					return Status.SUCCESS;
				fileRead.decrease();
				matchedChar.removeLast();
			}
			if (edge.type == Resource.EPSILON && step(edge.end) != Status.FAIL)
				return Status.SUCCESS;
		}
		return Status.FAIL;
	}

	public ArrayList<String> matchedList = new ArrayList<>();

	private void saveMatched() {
		Character[] chars = this.matchedChar.toArray(new Character[] {});
		StringBuilder charsStr = new StringBuilder();
		for (char ch : chars)
			charsStr.append(ch);
		matchedList.add(charsStr.toString());
	}

	private void printMatched() {
		System.out.print("Matched characters: ");
		for (char ch : matchedChar) {
			System.out.print(ch);
		}
		System.out.println();
	}

	private void refresh() {
		for (State state : this.stateList) {
			state.status = Status.FAIL;
		}
	}

	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "regex:" + this.regex + ",Start:" + this.Start + ",End:" + this.End + ",\nedgeList:"
				+ this.edgeList.size() + ",stateList:" + this.stateList.size() + ",matchedChar=" + this.matchedChar;
	}
	// Getters and setters
	public String getRegex() {
		return regex;
	}

	public void setRegex(String regex) {
		this.regex = regex;
	}

	public State getStart() {
		return Start;
	}

	public void setStart(State start) {
		Start = start;
	}

	public State getEnd() {
		return End;
	}

	public void setEnd(State end) {
		End = end;
	}

	public LinkedList<Edge> getEdgeList() {
		return edgeList;
	}

	public void setEdgeList(LinkedList<Edge> edgeList) {
		this.edgeList = edgeList;
	}

	public LinkedList<State> getStateList() {
		return stateList;
	}

	public void setStateList(LinkedList<State> stateList) {
		this.stateList = stateList;
	}

	public LinkedList<Character> getMatchedChar() {
		return matchedChar;
	}

	public void setMatchedChar(LinkedList<Character> matchedChar) {
		this.matchedChar = matchedChar;
	}

	public ArrayList<String> getMatchedList() {
		return matchedList;
	}

	public void setMatchedList(ArrayList<String> matchedList) {
		this.matchedList = matchedList;
	}
}
