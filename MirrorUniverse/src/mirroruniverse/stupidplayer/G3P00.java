package mirroruniverse.stupidplayer;

public class G3P00 {
	
	boolean debug = false;
	

	public Graph buildGraph(int[][] left, int[][] right){
		printViews(left, right);
		
		Graph graph = new Graph();
		
		// add all vertices
		for(int i=0; i<left.length; i++)
			for(int j=0; j<left[0].length; j++)
				for(int k=0; k<right.length; k++)
					for(int l=0; l<right[0].length; l++){
						if(left[i][j] != 1 && right[k][l] != 1) graph.getNode((byte)(j-100),
								(byte)(i-100), (byte)(l-100), (byte)(k-100));	
					}
				
			
		// add all edges
		// Each node can be looked up using its hashcode
		// e.g: graph.getNode(lx, ly, rx, ry) will give you the relevant node
		// if we want to add an edge from (lx,ly,rx,ry) to (lx+1,ly,rx+1,ry)
		// we just call graph.getNode(lx, ly, rx, ry).addEdge(1, graph.getNode(lx+1,ly,rx+1,ry));
		
		
		return graph;
		
			
	}
	
	
	public void buildGraphOfWorld(int[][] leftView, int[][] rightView, int currPosLX, int currPosLY, int currPosRX, int currPosRY){
		
	}
	
	
	
	public void printViews(int[][] left, int[][] right){
		if (debug) {
			System.out.println("Left View:");
			for (int i = 0; i < left.length; i++) {
				for (int j = 0; j < left.length; j++)
					System.out.print(left[i][j] + " ");
				System.out.println();
			}

			System.out.println("Right View:");
			for (int i = 0; i < right.length; i++) {
				for (int j = 0; j < right.length; j++)
					System.out.print(right[i][j] + " ");
				System.out.println();
			}
		}

	}
}
