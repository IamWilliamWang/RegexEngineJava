
public class CharsContainer implements Cloneable{
	private int pointer = 0;
	private int minIndex=0;
	private String chars;
	private boolean outOfIndex=false;
	public CharsContainer(String chars) {
		this.chars=chars;
	}
	public boolean Increase() {
		if(pointer==chars.length()-1) {
			outOfIndex=true;
			return false;
		}
		pointer++;
		return true;
	}
	public boolean Decrease() {
		if(pointer==minIndex) {
			outOfIndex=true;
			return false;
		}
		pointer--;
		return true;
	}
	public boolean hasNext() {
		return !outOfIndex;
	}
	public char getChar() {
		return chars.charAt(pointer);
	}
	public CharsContainer getSubString() {
		CharsContainer con = this.cloneObject();
		con.minIndex=pointer;
		return con;
	}
	public CharsContainer cloneObject() {
		try {
			return (CharsContainer) this.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		if(this.outOfIndex)
			return "\\0";
		return this.chars.substring(pointer);
	}
}
