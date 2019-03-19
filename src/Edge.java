public class Edge {
	final boolean NEXCLUDED = false;
	final boolean EXCLUDED = true;
	final int LCASES=256;
	final int UCASES=257;
	final int NUM=258;
	final int EPSILON=259;
	final int ANY=260;
	final int WS=261;
	
	State start;
	State end;
	int type;
	int exclude;
	
	public Edge(State start, State end, int type, int exclude) {
		super();
		this.start = start;
		this.end = end;
		this.type = type;
		this.exclude = exclude;
	}
	
	boolean match(char p)
	{
		switch(type)
		{
		case LCASES:
			if(p>='a'&&p<='z') 
				return this.exclude==0;
		case UCASES:
			if (p >= 'A' && p <= 'Z') 
				return this.exclude==0;
		case NUM:
			if (p >= '0' && p <= '9') 
				return this.exclude==0;
		case ANY:
			if (p >= -1 && p <= 127) 
				return this.exclude==0;
		case WS:
			if(p=='\t' || p=='\n' || p=='\f' || p=='\r' || p==0x0B)
				return this.exclude==0;
			break;
		default:
			if(type==p)
				return this.exclude==0;
		}
		return this.exclude!=0;
	}
}
