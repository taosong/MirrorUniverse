package mirroruniverse.stupidplayer;

import gnu.trove.list.linked.TIntLinkedList;

public class PathFinder {

	
	private final Graph graph = new Graph();
	
	private byte transLtX = 0;
	private byte transLtY = 0;
	private byte transRtX = 0;
	private byte transRtY = 0;
	
	private byte offlx=0, offly=0, offrx=0, offry=0;
	private static final int[][] dirs = {{4,5,6},{3,0,7},{2,1,8}};

	private static final boolean DEBUG = true; 
	protected TIntLinkedList path;
	
	Node exit = null;
	boolean debug = true;

	public int updateGraph(int[][] leftView, int[][] rightView, int startly, int startlx,int startry, int startrx) {
		
		if(offlx==0){
			setOffsets(startlx, startly, startrx, startry);
		}
		//printViews(leftView, rightView);
		
		buildGraph(leftView, rightView);
		System.out.println(graph.toString());
		int source = Node.getHash((byte)startlx, (byte)startly, (byte)startrx, (byte)startry);
		int target = Node.getHash(exit.getLx(), exit.getLy(), exit.getRx(), exit.getRy());
		path = graph.dijkstraShortestPath(source, target);
		return 0;
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
		
	private void buildGraph(int[][] leftView, int[][] rightView){
		// add all vertices and set edges
		for (int i = 0; i < leftView.length; i++)
			for (int j = 0; j < leftView[0].length; j++)
				for (int k = 0; k < rightView.length; k++)
					for (int l = 0; l < rightView[0].length; l++) {
						
						if(leftView[i][j]==4 || rightView[k][l]==4)continue;
						
						if(leftView[i][j] == 2 && rightView[k][l] == 2){
							// do something special here
							// like setting the exit
							//if(DEBUG) System.out.println("LX: "+exit.getLx()+" LY: "+exit.getLy());
							if(exit == null) exit = graph.addNode(
									getGlobalCoord(j, offly, transLtX),
									getGlobalCoord(i, offlx, transLtY),
									getGlobalCoord(l, offrx, transRtX),
									getGlobalCoord(k, offry, transRtY),
									(byte)2);
						}
						
						if (leftView[i][j] != 1 && rightView[k][l] != 1) {
							Node current = graph.addNode(
									getGlobalCoord(j, offly, transLtX),
									getGlobalCoord(i, offlx, transLtY),
									getGlobalCoord(l, offrx, transRtX),
									getGlobalCoord(k, offry, transRtY),
									(byte)0);
							//setEdges(current, leftView, rightView, i, j, k ,l);
						}
					}

	}
	
	private void setEdges(Node node, int[][] leftView, int[][] rightView,
			int i, int j, int k, int l){
		
		for(int deltaX = -1; deltaX < 2; deltaX++){
			for(int deltaY = -1; deltaY < 2; deltaY++){
				if(deltaX == 0 && deltaY == 0) continue;
				int iprime = -1, jprime = -1, kprime = -1, lprime = -1;
				{
					Node target = null;
					iprime = incr(leftView.length, i, deltaY, iprime);
					jprime = incr(leftView[0].length, j, deltaX, jprime);
					kprime = incr(leftView.length, k, deltaY, kprime);
					lprime = incr(leftView[0].length, l, deltaX, lprime);
					if(iprime == -1 || jprime == -1 || kprime == -1 || lprime ==-1) continue;
					
					// If you have hit an obstacle on both maps loop back to current state
					if(leftView[iprime][jprime] == 1 && rightView[kprime][lprime] == 1){
						target = node;
					}
					
					// both are open or one is open
					else if(leftView[iprime][jprime] == 0 && rightView[kprime][lprime] == 0){
						target = graph.addNode( (byte)(node.getLx() + deltaX), (byte)(node.getLy() + deltaY),
								(byte)(node.getRx() + deltaX), (byte)(node.getRy() + deltaY), (byte)0);
					}else if( leftView[iprime][jprime] == 0 && rightView[kprime][lprime] == 1){
						target = graph.addNode( (byte) (node.getLx() + deltaX), (byte)(node.getLy() + deltaY),
								node.getRx(), node.getRy(), (byte)0);
					}else if( leftView[iprime][jprime] == 1 && rightView[kprime][lprime] == 0){
						target = graph.addNode( node.getLx(), node.getLy(),
								(byte)(node.getRx() + deltaX), (byte)(node.getRy() + deltaY), (byte)0);
					}else 
						// we found the exit!!
						if( leftView[iprime][jprime] == 2 || rightView[kprime][lprime] == 2){
						target = graph.addNode( (byte)(node.getLx() + deltaX), (byte)(node.getLy() + deltaY),
								(byte)(node.getRx() + deltaX), (byte)(node.getRy() + deltaY), (byte)2);
						
					}
					
					node.addEdge(dirs[deltaX+1][deltaY+1], target.hashCode());
				}
			}
		}
	}
	
	private void setOffsets(int olx, int oly, int rlx, int rly){
		Node.checkRange(olx);
		Node.checkRange(oly);
		Node.checkRange(rlx);
		Node.checkRange(rly);
		offlx = (byte) olx;
		offly = (byte) oly;
		offrx = (byte) rlx;
		offry = (byte) rly;
	}
	
	private int incr(int len, int x, int deltaX, int xprime) {
		if (deltaX > 0) {
			return x + deltaX < len ? x + deltaX : xprime;
		} else {
			return x + deltaX > 0 ? x + deltaX : xprime;
		}
	}
	
	public byte getGlobalCoord(int i, byte offset, byte translate){
		Node.checkRange(i - offset + translate);
		return (byte)(i - offset + translate);
	}
	
	public byte getLocalCoord(byte lx, byte offset, byte translate){
		Node.checkRange(lx);
		return (byte)(lx + offset - translate);
	}
}
