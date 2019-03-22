import java.util.LinkedList;

public class State {

	Status status; //State的状态
	LinkedList<Edge> inEdges = new LinkedList<>(); //指向该State的边
	LinkedList<Edge> outEdges = new LinkedList<>(); //从该State指向别的State的边

	public State() {
		this.status = Status.READY;
	}

	public State(Edge inEdges, Edge outEdges) {
		this(inEdges, outEdges, Status.READY);
	}

	public State(Edge inEdges, Edge outEdges, Status status) {
		this.status = status;
		this.inEdges.add(inEdges);
		this.outEdges.add(outEdges);
	}

	/*
	 * 合并this与state
	 */
	public void merge(State state) {
		while (!state.inEdges.isEmpty()) { //若state入度不为零
			patch(state.inEdges.getLast(), this);
			state.inEdges.removeLast();
		}
		while (!state.outEdges.isEmpty()) { //若state出度不为零
			patch(this, state.inEdges.getLast());
			state.outEdges.removeLast();
		}
	}

	/*
	 * 将状态和边完全连接
	 */
	public void patch(Edge edge, State state) {
		edge.end = state;
		state.inEdges.add(edge);
	}

	public void patch(State state, Edge edge) {
		edge.start = state;
		state.outEdges.add(edge);
	}
	
	// Getters and setters
	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	public LinkedList<Edge> getInEdges() {
		return inEdges;
	}

	public LinkedList<Edge> getOutEdges() {
		return outEdges;
	}

	/*
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		return "stat=" + status + ",In=" + inEdges.size() + ",Out=" + outEdges.size();
	}

}
