import java.util.LinkedList;

public class State {
	
	
	public int status;
	public LinkedList<Edge> InEdges = new LinkedList<>();
	public LinkedList<Edge> OutEdges = new LinkedList<>();
	public State() {
		this.status=Resource.READY;
	}
	public State(Edge inEdges, Edge outEdges) {
		this(inEdges,outEdges,Resource.READY);
	}
	
	public State(Edge inEdges, Edge outEdges, int status) {
		this.status = status;
		addEdge(this.InEdges,inEdges);
		addEdge(this.OutEdges,outEdges);
	}
	//除bug补丁
	public static void addEdge(LinkedList<Edge> Edges,Edge e) {
		if(Edges.contains(e))
			return;
		Edges.add(e);
	}
	
	public void merge(State s) {
		while(!s.InEdges.isEmpty()) {
			patch(s.InEdges.getLast(),this);
			s.InEdges.removeLast();
		}
		while(!s.OutEdges.isEmpty()) {
			patch(this,s.InEdges.getLast());
			s.OutEdges.removeLast();
		}
	}
	
	public void patch(Edge e, State s) {
		e.end=s;
		addEdge(s.InEdges,e);
	}
	public void patch(State s,Edge e) {
		e.start=s;
		addEdge(s.OutEdges,e);
	}

	@Override
	public String toString() {
		return "stat=" + status + ",In=" + InEdges.size() + ",Out=" + OutEdges.size() ;
	}
	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		State o = (State)obj;
		return this.status==o.status && this.InEdges.size()==o.InEdges.size()&&this.OutEdges.size()==o.OutEdges.size();
	}
	
}
