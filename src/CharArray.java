/*
 * 该类模仿char*的所有行为
 */
public class CharArray implements Cloneable {
	private int pointer;
	private int minIndex;
	private String chars;
	private boolean outOfIndex = false; //当数组越界时置为true

	public CharArray(String chars) {
		this.chars = chars;
		this.pointer = 0;
		this.minIndex = 0;
	}

	/*
	 * char指针前移
	 */
	public boolean increase() {
		if (pointer == chars.length() - 1) {
			outOfIndex = true;
			return false;
		}
		pointer++;
		return true;
	}

	/*
	 * char指针后移
	 */
	public boolean decrease() {
		if (pointer == minIndex) {
			outOfIndex = true;
			return false;
		}
		pointer--;
		return true;
	}

	/*
	 * 指针到达末尾并已越界
	 */
	public boolean reachEnding() {
		return outOfIndex;
	}

	/*
	 * 获得指向的字符
	 */
	public char getChar() {
		return chars.charAt(pointer);
	}

	/*
	 * 获得CharArray类型的子串
	 */
	public CharArray getSubString() {
		CharArray con = this;
		con.minIndex = pointer;
		return con;
	}

	/*
	 * 克隆对象
	 */
	public CharArray cloneObject() {
		try {
			return (CharArray) this.clone();
		} catch (CloneNotSupportedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return null;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		if (this.outOfIndex)
			return "\\0";
		return this.chars.substring(pointer);
	}
}
