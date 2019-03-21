public class Edge {
	State start; //边对应的开始节点
	State end; //边对应的结束节点
	int type; //储存字符或类别信息
	boolean exclude; //用于结尾生成
	
	public Edge(State start, State end, int type, boolean exclude) {
		super();
		this.start = start;
		this.end = end;
		this.type = type;
		this.exclude = exclude;
	}
	
	/*
	 * 该字符是否满足条件
	 */
	boolean match(char ch)
	{
		switch(type)
		{
		case Resource.LCASES:
			if(Character.isLowerCase(ch)) 
				return this.exclude;
		case Resource.UCASES:
			if (Character.isUpperCase(ch)) 
				return this.exclude;
		case Resource.NUM:
			if (Character.isDigit(ch)) 
				return this.exclude;
		case Resource.ANY:
			if (ch >= -1 && ch <= 127) 
				return this.exclude;
		case Resource.WS:
			if(ch=='\t' || ch=='\n' || ch=='\f' || ch=='\r' || ch==0x0B)
				return this.exclude;
			break;
		default:
			if(ch == (char)type)
				return !this.exclude;
		}
		return this.exclude;
	}
	
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return this.type==((Edge)obj).type;
	}
	
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.type+"";
	}
}
