package mirroruniverse.g3player;

import gnu.trove.list.TIntList;
import gnu.trove.list.linked.TIntLinkedList;
import gnu.trove.queue.TIntQueue;
import mirroruniverse.sim.Player;
import mirroruniverse.stupidplayer.Graph;
import mirroruniverse.stupidplayer.Node;

public class G3P0 implements Player {
	
	Graph graph = new Graph();
	byte transLtX = 0;
	byte transLtY = 0;
	byte transRtX = 0;
	byte transRtY = 0;

	@Override
	public int lookAndMove(int[][] aintViewL, int[][] aintViewR) {
		byte offlx = (byte) ( aintViewL[0].length/  2);
		byte offly = (byte) ( aintViewL.length/  2);
		byte offrx = (byte) ( aintViewR[0].length/  2);
		byte offry = (byte) ( aintViewR.length/  2);
		
		/**
		 * Do a BFS from the centre of each view (player positions)
		 * using a TIntLinkedList
		 * 
		 */
		
		byte lx, ly, rx, ry;
		TIntList queue = new TIntLinkedList();
		queue.add(Node.getHash(getGlobalCoord((byte)0, (byte)0, transLtX),
				getGlobalCoord((byte)0, (byte)0, transLtY),
				getGlobalCoord((byte)0, (byte)0, transRtX),
				getGlobalCoord((byte)0, (byte)0, transRtY)));
		bfsVisit(queue, aintViewL, aintViewR, offlx, offly, offrx, offry);
		
		return 0;
	}
	
	
	private void bfsVisit(TIntList queue, int[][] aintViewL, int[][] aintViewR,
			byte offlx, byte offly, byte offrx, byte offry) {
		if(queue.isEmpty()) return;
		int src = queue.removeAt(0);
		
		byte[] globals  = Node.getBytes(src);

		byte i = getLocalCoord(globals[0], offlx, transLtX);
		byte j = getLocalCoord(globals[1], offly, transLtY);
		byte k = getLocalCoord(globals[2], offrx, transRtX);
		byte l = getLocalCoord(globals[3], offry, transRtY);
		
		
		
	}

	private int incr(int len, int x, int deltaX) {
		if (deltaX > 0) {
			return x + deltaX < len ? x + deltaX : x;
		} else {
			return x + deltaX > 0 ? x + deltaX : x;
		}
	}
	


	
	public byte getGlobalCoord(byte i, byte offset, byte translate){
		Node.checkRange(i - offset + translate);
		return (byte)(i - offset + translate);
	}
	
	public byte getLocalCoord(byte lx, byte offset, byte translate){
		Node.checkRange(lx);
		return (byte)(lx + offset - translate);
	}

}
