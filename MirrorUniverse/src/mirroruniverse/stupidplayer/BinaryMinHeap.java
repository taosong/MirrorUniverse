package mirroruniverse.stupidplayer;

import java.util.ArrayList;

public class BinaryMinHeap {
	public static final int INF = Integer.MAX_VALUE;
	
	public ArrayList<Element> a = new ArrayList<Element>();
	
	public void insert(Node node, int value){
		int p = a.size(); 
		a.add(new Element(node, INF));
		decreaseKey(p, value);
	}
	
	public void decreaseKey(int index, int value){
		assert(a.get(index).value < value);
		a.get(index).value = value;
		while(parent(index) != -1 && (a.get(parent(index)).value > value)){
			Element temp = a.get(parent(index));
			a.set(parent(index), a.get(index));
			a.set(index, temp);
		}
	}
	
	public int parent(int index){
		if(index > 0) return index/2;
		return -1;
	}
	
	public Node extractMin(){
		return a.remove(0).getNode();
	}
	
	public static final class Element{
		
		private Node node;
		
		private int value;
		
		public Element(Node node, int value) {
			super();
			this.node = node;
			this.value = value;
		}
		
		public Node getNode() {
			return node;
		}
		
		public void setNode(Node node) {
			this.node = node;
		}
		
		public int getValue() {
			return value;
		}
		
		public void setValue(int value) {
			this.value = value;
		}
		
	}

}
