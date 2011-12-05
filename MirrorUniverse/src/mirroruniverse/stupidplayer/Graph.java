package mirroruniverse.stupidplayer;

import gnu.trove.list.linked.TIntLinkedList;
import gnu.trove.map.TIntObjectMap;
import gnu.trove.map.hash.TIntIntHashMap;
import gnu.trove.map.hash.TIntObjectHashMap;

public class Graph {
	
	private TIntObjectMap<Node> V = new TIntObjectHashMap<Node>();
	
	public Node getNode(byte lx, byte ly, byte rx, byte ry){
		int key = Node.getHash(lx, ly, rx, ry);
		return V.get(key);
	}
	
	public Node addNode(byte lx, byte ly, byte rx, byte ry, byte nature){
		int key = Node.getHash(lx, ly, rx, ry);
		Node node = null;
		if(!V.containsKey(key)){
			node = new Node(lx, ly, rx, ry, nature);
			V.put(key, node);
		}else{
			node = V.get(key);
		}
		return node;
	}
	
	public Node getNode(int hash){
		return V.get(hash);
	}
	
	public boolean hasNode(byte lx, byte ly, byte rx, byte ry){
		return V.containsKey(Node.getHash(lx, ly, rx, ry));
	}
	
	public TIntLinkedList dijkstraShortestPath(int source, int target){
		BinaryMinHeap heap = new BinaryMinHeap();
		TIntLinkedList path = new TIntLinkedList();
		TIntIntHashMap pi = new TIntIntHashMap();
		
		heap.insert(source, 0);
		pi.put(source, Integer.MIN_VALUE);
		
		while(!heap.isEmpty()){
			int d = heap.getRootValue();
			int uKey = heap.extractMin();
			Node u = V.get(uKey);
			for(int direction = 1; direction < 9 ; direction++ ){
				int v = u.getEdge(direction);
				Node vNode = V.get(v);
				int weight = u.getEdgeWeight(direction, vNode);
				if( v != Node.UNEXPLORED){
					
					if(heap.isPresent(v) && heap.getValue(v) < d+weight){
							pi.put(v, uKey);
							heap.decreaseKey(heap.getIndex(v), d+weight);
						}
					else{
						pi.put(v, uKey);
						heap.insert(v, d+weight);
					}
				}
			}
		}
		
		if(pi.containsKey(target)){
			return null;
		}
		
		int i = pi.get(target);
		int ii = target;
		
		while(i != Integer.MIN_VALUE){
			path.add(V.get(i).getDir(ii));
		}
		
		path.reverse();
		
		return path;
	}

}
