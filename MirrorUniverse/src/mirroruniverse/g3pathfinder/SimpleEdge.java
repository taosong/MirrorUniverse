package mirroruniverse.g3pathfinder;

import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;

@SuppressWarnings("serial")
public class SimpleEdge extends DefaultWeightedEdge {
	
	public PointPair getFrom(){
		return (PointPair) getSource();
	}
	
	public PointPair getTo(){
		return (PointPair) getTarget();
	}
	
	public double getWeight(){
		return super.getWeight();
	}

}
