package mirroruniverse.stupidplayer;

import java.util.ArrayList;

public class BinaryMinHeap {
	
	public ArrayList<Element> a = new ArrayList<Element>();
	
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
