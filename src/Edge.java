public class Edge {
	State start;
	State end;
	int type;
	boolean exclude;
	
	public Edge(State start, State end, int type, boolean exclude) {
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
		case Resource.LCASES:
			if(p>='a'&&p<='z') 
				return this.exclude;
		case Resource.UCASES:
			if (p >= 'A' && p <= 'Z') 
				return this.exclude;
		case Resource.NUM:
			if (p >= '0' && p <= '9') 
				return this.exclude;
		case Resource.ANY:
			if (p >= -1 && p <= 127) 
				return this.exclude;
		case Resource.WS:
			if(p=='\t' || p=='\n' || p=='\f' || p=='\r' || p==0x0B)
				return this.exclude;
			break;
		default:
			if(type==p)
				return this.exclude;
		}
		return this.exclude;
	}
}
