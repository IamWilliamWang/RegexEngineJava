import java.util.Iterator;
import java.util.LinkedList;
import java.util.ListIterator;
public class Nfa {
	public String regex = "";
	public State Start;
	public State End;
	public LinkedList<Edge> edgeList = new LinkedList<>();
	public LinkedList<State> stateList = new LinkedList<>();
	public LinkedList<Character> matchedChar = new LinkedList<>();
	public static CharsContainer regRead;
	public static CharsContainer fileRead;
	
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
		this.Start.status = Resource.SUCCESS;
		fileRead = new CharsContainer(content);
		
		while (fileRead.hasNext())
		{
			if (step(this.Start) == Resource.FAIL)
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
			return Resource.SUCCESS;

		return Resource.FAIL;
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
				newEdge(currentStart, currentEnd, Resource.ANY, Resource.NEXCLUDED);	
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
				newEdge(currentStart, currentEnd, Resource.EPSILON, Resource.NEXCLUDED);
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
				newEdge(currentStart, currentEnd, (int)regRead.getChar(), Resource.EXCLUDED);
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
				newEdge(currentStart, currentEnd, (int)regRead.getChar(), Resource.NEXCLUDED);
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
		boolean ifexclude = Resource.NEXCLUDED;
		if (regRead.getChar() == '^') {
			regRead.Increase();
			ifexclude = Resource.EXCLUDED;
		}
		for (; regRead.getChar() !=']'; regRead.Increase()) {
			switch (regRead.getChar()) {
			case '0':
			case 'a':
			case 'A':
				regRead.Increase();
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
				regRead.Increase();
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
	private void newEdge(State start,State end,int type) {
		newEdge(start,end,type,Resource.EXCLUDED);
	}
	private void newEdge(State start,State end,int type,boolean exclude) {
		Edge out = new Edge(start, end, type, exclude);
		end.patch(out, end);
		start.patch(start, out);
		edgeList.add(out);
	}
	private int step(State current) {
		ListIterator<Edge> itor;
		itor = current.OutEdges.listIterator();

		if (End.status == Resource.SUCCESS) 
			return Resource.SUCCESS;

		while(itor.hasNext()) {
			Edge edge = itor.next();
			if(edge.match(fileRead.getChar())) {
				edge.end.status=Resource.SUCCESS;
				matchedChar.add(fileRead.getChar());
				fileRead.Increase();
				if(step(edge.end)!=0)
					return Resource.SUCCESS;
				fileRead.Decrease();
				matchedChar.removeLast();
			}
			if(edge.type==Resource.EPSILON && step(edge.end)!=0)
				return Resource.SUCCESS;
		}
		return Resource.FAIL;
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
			itor.next().status=Resource.FAIL;
		}
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return "regex:"+this.regex+",Start:"+this.Start+",End:"+this.End+",\nedgeList:"+this.edgeList.size()+",stateList:"+this.stateList.size()+",matchedChar="+this.matchedChar;
	}
}
