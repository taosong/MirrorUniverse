package mirroruniverse.g3pathfinder;

import gnu.trove.list.TIntList;
import gnu.trove.list.linked.TIntLinkedList;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.ArrayList;

public class BinaryMinHeap {
	public static final int INF = Integer.MAX_VALUE;
	
	private TIntList a = new TIntLinkedList();
	private TIntIntHashMap values = new TIntIntHashMap();
	
	public boolean isEmpty(){
		return a.isEmpty();
	}
	
	public boolean isPresent(int node){
		return values.containsKey(node);
	}
	
	public void insert(int node, int value){
		int p = a.size(); 
		a.add(node);
		decreaseKey(p, value);
	}
	
	public int getRootValue(){
		return values.get(a.get(0));
	}
	
	public int getValue(int node){
		return values.get(node);
	}
	
	public int getIndex(int node){
		return a.indexOf(node);
	}
	
	public void decreaseKey(int index, int value){
		if (values.get(a.get(index)) > value) {
			values.put(a.get(index), value);
			while (parent(index) != -1
					&& (values.get(a.get(parent(index))) > value)) {
				int temp = a.get(parent(index));
				a.set(parent(index), a.get(index));
				a.set(index, temp);
			}
		}
	}
	
	public int parent(int index){
		if(index > 0) return index/2;
		return -1;
	}
	
	// Actually returns the hashcode of the node
	// If we want the actual node, we need to
	// do a graph.getNode() on this key
	public int extractMin(){
		return a.removeAt(0);
	}
	

}
