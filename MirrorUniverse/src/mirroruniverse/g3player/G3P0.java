package mirroruniverse.g3player;

import mirroruniverse.sim.Player;
import mirroruniverse.stupidplayer.Graph;
import mirroruniverse.stupidplayer.Node;

public class G3P0 implements Player {
	
	private final Graph graph = new Graph();
	
	private byte transLtX = 0;
	private byte transLtY = 0;
	private byte transRtX = 0;
	private byte transRtY = 0;
	
	private byte offlx=0, offly=0, offrx=0, offry=0;
	private static final int[][] dirs = {{4,5,6},{3,0,7},{2,1,8}}; 
	
	Node exit = null;

	@Override
	public int lookAndMove(int[][] leftView, int[][] rightView) {
		
		if(offlx==0){
			setOffsets(leftView, rightView);
		}
		
		buildGraph(leftView, rightView);
		
		
		return 0;
	}
	
	private void buildGraph(int[][] leftView, int[][] rightView){
		// add all vertices and set edges
		for (int i = 0; i < leftView.length; i++)
			for (int j = 0; j < leftView[0].length; j++)
				for (int k = 0; k < rightView.length; k++)
					for (int l = 0; l < rightView[0].length; l++) {
						
						
						if(leftView[i][j] == 2 && rightView[k][l] == 2){
							// do something special here
							
						}
						
						if (leftView[i][j] != 1 && rightView[k][l] != 1) {
							Node current = graph.getNode(
									getGlobalCoord(j, offly, transLtX),
									getGlobalCoord(i, offlx, transLtY),
									getGlobalCoord(l, offrx, transRtX),
									getGlobalCoord(k, offry, transRtY));
							setEdges(current, leftView, rightView, i, j, k ,l);
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
						target = graph.getNode( (byte)(node.getLx() + deltaX), (byte)(node.getLy() + deltaY),
								(byte)(node.getRx() + deltaX), (byte)(node.getRy() + deltaY));
					}else if( leftView[iprime][jprime] == 0 && rightView[kprime][lprime] == 1){
						target = graph.getNode( (byte) (node.getLx() + deltaX), (byte)(node.getLy() + deltaY),
								node.getRx(), node.getRy());
					}else if( leftView[iprime][jprime] == 1 && rightView[kprime][lprime] == 0){
						target = graph.getNode( node.getLx(), node.getLy(),
								(byte)(node.getRx() + deltaX), (byte)(node.getRy() + deltaY));
					}else 
						// we found the exit!!
						if( leftView[iprime][jprime] == 2 && rightView[kprime][lprime] == 2){
						target = graph.getNode( (byte)(node.getLx() + deltaX), (byte)(node.getLy() + deltaY),
								(byte)(node.getRx() + deltaX), (byte)(node.getRy() + deltaY));
						if(exit == null) exit = target;
					}
					
					node.addEdge(dirs[deltaX+1][deltaY+1], target.hashCode());
				}
			}
		}
	}
	
	private void setOffsets(int[][] leftView, int[][] rightView){
		offlx = (byte) ( leftView[0].length/  2);
		offly = (byte) ( leftView.length/  2);
		offrx = (byte) ( rightView[0].length/  2);
		offry = (byte) ( rightView.length/  2);
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
