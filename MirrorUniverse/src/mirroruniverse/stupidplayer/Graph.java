package mirroruniverse.stupidplayer;

import gnu.trove.list.array.TIntArrayList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class Graph {
	
	private TIntObjectMap<Node> V = new TIntObjectHashMap<Node>();
	
	public Node getNode(byte lx, byte ly, byte rx, byte ry){
		int key = Node.getHash(lx, ly, rx, ry);
		if(!V.containsKey(key)){
			Node newNode = new Node(lx,ly,rx,ry);
			V.put(key, newNode);
		}
		return V.get(key);
	}
	
	public boolean hasNode(byte lx, byte ly, byte rx, byte ry){
		return V.containsKey(Node.getHash(lx, ly, rx, ry));
	}
	
	//TODO: Complete this!!
	public TIntArrayList dijkstraShortestPath(int source, int target){
		return null;
	}

}
