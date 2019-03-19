
public class Main {
	public static void main(String[] args) {
		Nfa nfa = new Nfa("a*(0|1)");
		System.out.println(nfa.match("daa0"));
	}
}
