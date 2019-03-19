import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;

public class Nfa {
	public String regex = "";
	public State Start = new State();
	public State End = new State();
	public LinkedList<Edge> edgeList = new LinkedList<>();
	public LinkedList<State> stateList = new LinkedList<>();
	public LinkedList<Character> matchedChar = new LinkedList<>();
	public static String regRead = "";
	public static int regReadIndex=0;
	public static CharsContainer fileRead;
	public static final int MAX_SIZE=512;
	final int NEXCLUDED = 0;
	final int EXCLUDED = 1;
	final static int READY=-1;
	final static int SUCCESS=1;
	final static int FAIL=0;
	final int LCASES=256;
	final int UCASES=257;
	final int NUM=258;
	final int EPSILON=259;
	final int ANY=260;
	final int WS=261;
	public Nfa(String reg) {
		this.regex=reg;
		this.Start=new State();
		this.addState(Start);
		if((End=regex2nfa(regex,Start))!=null) 
			System.out.println("NFA has built successfully!");
		else
			System.out.println("NFA built failed, please check if the regular expression is right!");
	}
	
	public int match(String content) {
		boolean everMatched = false;
		this.Start.status = SUCCESS;
		fileRead = new CharsContainer(content);
		
		while (fileRead.hasNext())
		{
			if (step(this.Start) == FAIL)
			{
				fileRead.Increase();
				matchedChar.clear();
				refresh();
				continue;
			}
			printMatched();
			everMatched = true;
			refresh();
			matchedChar.clear();
		}	
		if (everMatched)
			return SUCCESS;

		return FAIL;
	}
	private State regex2nfa(String reg, State start) {
		return regex2nfa(new CharsContainer(reg), start);
	}
	private State regex2nfa(CharsContainer reg, State start) {
		State currentEnd, currentStart = null;
		State alternate;
		ListIterator<Edge> itor;
		if(regex==null)
			return null;
		
		currentEnd=start;
		CharsContainer regRead = reg.cloneObject();
		while(regRead.hasNext()) {
			switch(regRead.getChar()) {
			case '.':	/* any */
				currentStart = currentEnd;
				currentEnd = new State();
				newEdge(currentStart, currentEnd, ANY, NEXCLUDED);	
				this.addState(currentEnd);
				break;
			case '|':	// alternate 
				regRead.Increase();
				currentStart = start;
				alternate= regex2nfa(regRead.getSubString(), start);
				currentEnd.merge(alternate);
				stateList.remove(alternate);
				regRead.Decrease();
				break;
			case '?':	// zero or one 
				newEdge(currentStart, currentEnd, EPSILON, NEXCLUDED);
				break;
			case '*':	// zero or more 
				alternate = currentEnd;
				currentStart.merge(alternate);
				stateList.remove(alternate);
				currentEnd = currentStart;
				break;
			case '+':	/* one or more */
				itor = currentStart.OutEdges.listIterator();
				while(itor.hasNext()) {
					Edge edge = itor.next();
					newEdge(currentEnd,edge.end,edge.type,edge.exclude);
				}
				break;
			case '(':
				regRead.Increase();
				currentStart = currentEnd;
				currentEnd = regex2nfa(regRead.getSubString(), currentEnd);
				break;
			case ')':
				return currentEnd;
			case '[':
				regRead.Increase();
				currentStart = currentEnd;
				if((currentEnd = group(currentEnd)) == null) 
					return null;
				break;	
			case '^':
				regRead.Increase();
				currentStart = currentEnd;
				currentEnd = new State();
				newEdge(currentStart, currentEnd, (int)regRead.getChar(), EXCLUDED);
				this.addState(currentEnd);
				break;
			case '\\':
				regRead.Increase();
				currentStart = start;
				if ((currentEnd = preDefine(currentEnd)) == null) 
					return null;
				this.addState(currentEnd);
				break;
			case '\t':
			case '\n':
			case '\f':
			case '\r':
			case 0x0B:
				break;
			default:
				currentStart = currentEnd;
				currentEnd = new State();
				newEdge(currentStart, currentEnd, (int)regRead.getChar(), NEXCLUDED);
				this.addState(currentEnd);
				break;
			}
			regRead.Increase();
		}
		return currentEnd;
	}
	
	private void addState(State s) {
		if(this.stateList.contains(s))
			return;
		this.stateList.add(s);
	}
	private State group(State top) {
		State s = new State();
		boolean ifexclude = NEXCLUDED!=0;
		if (regRead.charAt(regReadIndex) == '^') {
			regReadIndex++;
			ifexclude = EXCLUDED!=0;
		}
		for (; regRead.charAt(regReadIndex) !=']'; regReadIndex++) {
			switch (regRead.charAt(regReadIndex)) {
			case '0':
			case 'a':
			case 'A':
				regReadIndex++;
				if (regRead.charAt(regReadIndex) != '-') {
					System.out.println("NFA built failed, please check if the regular expression is right!");
					return null;
				}
				break;
			case '9':
				newEdge(top, s, NUM, (ifexclude?1:0));
				break;
			case 'z':
				newEdge(top, s, LCASES, (ifexclude?1:0));
				break;
			case 'Z':
				newEdge(top, s, UCASES, (ifexclude?1:0));
				break;
			case '\\':
				regReadIndex++;
				if ((s = preDefine(top)) == null) 
					return null;
				break;
			default:
				System.out.println("NFA built failed, please check if the regular expression is right!");
				return null;
			}		
		}
		this.addState(s);
		return s;
	}
	private State preDefine(State top) {
		State s = new State();
		switch (regRead.charAt(regReadIndex)) {
			case 'd':
				newEdge(top, s, NUM, NEXCLUDED);
				break;
			case 'D':
				newEdge(top, s, NUM, EXCLUDED);
				break;
			case 's':
				newEdge(top, s, WS, NEXCLUDED);
				break;
			case 'S':
				newEdge(top, s, WS, EXCLUDED);
				break;
			case 'w':
				newEdge(top, s, NUM, NEXCLUDED);
				newEdge(top, s, UCASES, NEXCLUDED);
				newEdge(top, s, LCASES, NEXCLUDED);
				break;
			case 'W':
				newEdge(top, s, NUM, EXCLUDED);
				newEdge(top, s, UCASES, EXCLUDED);
				newEdge(top, s, LCASES, EXCLUDED);
				break;
			default:
				System.out.println("NFA built failed, please check if the regular expression is right!");
				return null;		
		}
		return s;
	}
	private void newEdge(State start,State end,int type,int exclude) {
		Edge out = new Edge(start, end, type, exclude);
		end.patch(out, end);
		start.patch(start, out);
		edgeList.add(out);
	}
	private int step(State current) {
		ListIterator<Edge> itor;
		itor = current.OutEdges.listIterator();

		if (End.status == SUCCESS) 
			return SUCCESS;

		while(itor.hasNext()) {
			Edge edge = itor.next();
			if(edge.match(fileRead.getChar())) {
				edge.end.status=SUCCESS;
				matchedChar.add(fileRead.getChar());
				fileRead.Increase();
				if(step(edge.end)!=0)
					return SUCCESS;
				fileRead.Decrease();
				matchedChar.removeLast();
			}
			if(edge.type==EPSILON && step(edge.end)!=0)
				return SUCCESS;
		}
		return FAIL;
	}
	private void printMatched() {
		ListIterator<Character> itor;
		itor = matchedChar.listIterator();
		System.out.println("Matced characters: ");
		while(itor.hasNext())
			System.out.print(itor.next());
		System.out.println();
	}
	private void refresh() {
		ListIterator<State> itor;   
		itor = stateList.listIterator();
		State current = itor.next();
		while(itor.hasNext()) {
			itor.next().status=FAIL;
		}
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "regex:"+this.regex+",Start:"+this.Start+",End:"+this.End+",\nedgeList:"+this.edgeList.size()+",stateList:"+this.stateList.size()+",matchedChar="+this.matchedChar;
	}
}
