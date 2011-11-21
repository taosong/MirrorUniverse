package mirroruniverse.g3player;

import org.jgrapht.graph.DefaultEdge;

@SuppressWarnings("serial")
public class SimpleEdge extends DefaultEdge {
	
	public PointPair getFrom(){
		return (PointPair) getSource();
	}
	
	public PointPair getTo(){
		return (PointPair) getTarget();
	}

}
