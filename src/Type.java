
public enum Type {
	LCASES(256), UCASES(257), NUM(258), EPSILON(259), ANY(260), WS(261);
	
	private final int value;
    //构造方法必须是private或者默认
    private Type(int value) {
        this.value = value;
    }

    public int value() {
        return this.value;
    }
    
    public static Object valueOf(char ch) {
    	if(ch==256)
    		return LCASES;
    	else if(ch==257)
    		return UCASES;
    	else if(ch==258)
    		return NUM;
    	else if(ch==259)
    		return EPSILON;
    	else if(ch==260)
    		return ANY;
    	else if(ch==261)
    		return WS;
    	return (int)ch;
    }
}
