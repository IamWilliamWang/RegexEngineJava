import java.util.LinkedList;

public class State {

	Status status;
	LinkedList<Edge> InEdges = new LinkedList<>();
	LinkedList<Edge> OutEdges = new LinkedList<>();

	public State() {
		this.status = Status.READY;
	}

	public State(Edge inEdges, Edge outEdges) {
		this(inEdges, outEdges, Status.READY);
	}

	public State(Edge inEdges, Edge outEdges, Status status) {
		this.status = status;
		this.InEdges.add(inEdges);
		addOutEdge(outEdges);
	}

	public void addOutEdge(Edge e) {
		if (OutEdges.contains(e))
			return;
		OutEdges.add(e);
	}

	public void merge(State s) {
		while (!s.InEdges.isEmpty()) {
			patch(s.InEdges.getLast(), this);
			s.InEdges.removeLast();
		}
		while (!s.OutEdges.isEmpty()) {
			patch(this, s.InEdges.getLast());
			s.OutEdges.removeLast();
		}
	}

	public void patch(Edge e, State s) {
		e.end = s;
		s.InEdges.add(e);
	}

	public void patch(State s, Edge e) {
		e.start = s;
		s.addOutEdge(e);
	}
	
	// Getters and setters
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public LinkedList<Edge> getInEdges() {
		return InEdges;
	}

	public void setInEdges(LinkedList<Edge> inEdges) {
		InEdges = inEdges;
	}

	public LinkedList<Edge> getOutEdges() {
		return OutEdges;
	}

	public void setOutEdges(LinkedList<Edge> outEdges) {
		OutEdges = outEdges;
	}

//	@Override
//	public String toString() {
//		return "stat=" + status + ",In=" + InEdges.size() + ",Out=" + OutEdges.size();
//	}
//
//	@Override
//	public boolean equals(Object obj) {
//		// TODO Auto-generated method stub
//		State o = (State) obj;
//		return this.status == o.status && this.InEdges.size() == o.InEdges.size()
//				&& this.OutEdges.size() == o.OutEdges.size();
//	}

}
