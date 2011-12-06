package mirroruniverse.stupidplayer;

import gnu.trove.list.TIntList;
import gnu.trove.list.linked.TIntLinkedList;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Stack;

public class G3P00 {
	
	boolean debug = false;
	final TIntIntHashMap parent = new TIntIntHashMap();
	private static final int[][] dirs = {{4,5,6},{3,0,7},{2,1,8}};
	int exit = Integer.MIN_VALUE;
	
	public List<Integer> bfs(final int[][] leftView, final int[][] rightView,
			final int startly, final int startlx, final int startry, final int startrx,
			final int exitlx, final int exitly, final int exitrx, final int exitry){
		
		TIntList queue = new TIntLinkedList();
		
		int source = Node.getHash((byte)(startlx-100), (byte)(startly-100),
				(byte)(startrx-100), (byte)(startry-100));
		queue.add(source);
		parent.put(source, Integer.MIN_VALUE);
		int retVal = -1;
		
		breakLabel:
		while (!queue.isEmpty()) {
			
			int u = queue.removeAt(0);
			byte[] bytes = Node.getBytes(u);
			int j = bytes[0] + 100;
			int i = bytes[1] + 100;
			int l = bytes[2] + 100;
			int k = bytes[3] + 100;
			
			for (int deltaX = -1; deltaX < 2; deltaX++)
				for (int deltaY = -1; deltaY < 2; deltaY++) {

					if (deltaX == 0 && deltaY == 0)continue;

					int iprime = -1, jprime = -1, kprime = -1, lprime = -1;

					iprime = incr(leftView.length, i, deltaY, iprime);
					jprime = incr(leftView[0].length, j, deltaX, jprime);
					kprime = incr(leftView.length, k, deltaY, kprime);
					lprime = incr(leftView[0].length, l, deltaX, lprime);

					// If no increment is possible, continue
					if (iprime == -1 || jprime == -1 || kprime == -1
							|| lprime == -1) continue;

					// If you have hit an obstacle on both maps continue
					if (leftView[iprime][jprime] == 1
							&& rightView[kprime][lprime] == 1) continue;
					
					// If this move takes us into unexplored territory, then continue
					if(leftView[iprime][jprime] == 4
							|| rightView[kprime][lprime] == 4) continue;
					
					
					// There is an obstacle on only one of the maps
					if(leftView[iprime][jprime] == 1){
						iprime = i;
						jprime = j;
					}
					
					if(rightView[kprime][lprime] == 1){
						kprime = k;
						lprime = l;
					}
					
					int v = Node.getHash((byte)(jprime-100), (byte)(iprime-100), (byte)(lprime-100), (byte)(kprime-100));
					
					// If we have visited this node, continue
					if(parent.containsKey(v)) continue;
					
					// Make u the parent of v and add v to the queue - classic BFS
					parent.put(v, u);
					queue.add(v);
					
					// If you find that both are able to exit, add parent pointers and mark as visited and break
					// out of loop
					if (leftView[iprime][jprime] == 2 && rightView[kprime][lprime] == 2){
						assert(iprime == exitlx && jprime == exitly && kprime == exitrx && lprime == exitry);
						retVal = 0;
						break breakLabel;
					}
				}
		}	
		
		int v = Node.getHash((byte)(exitlx-100), (byte)(exitly-100),
				(byte)(exitrx-100), (byte)(exitry-100));
		
		if(parent.containsKey(v)){
			Stack<Integer> path = new Stack<Integer>();
			while(parent.get(v) != Integer.MIN_VALUE){
				int u = parent.get(v);
				path.push(getDir(u, v));
			}
			List<Integer> p = new LinkedList<Integer>();
			while(!path.isEmpty()){
				p.add(path.pop());
			}
			return p;
		}
		
		
		return null;
				
	}
	
	
	
	private int getDir(int src, int dest){
		byte[] srcBytes = Node.getBytes(src);
		byte[] destBytes = Node.getBytes(dest);
		int dx = destBytes[0] - srcBytes[0];
		dx = dx == 0 ? destBytes[2] - srcBytes[2] : dx;
		int dy = destBytes[1] - srcBytes[1];
		dy = dy == 0 ? destBytes[3] - srcBytes[3] : dy;
		return dirs[dx+1][dy+1];		
	}
		
	private int incr(int len, int x, int deltaX, final int xprime) {
		if (deltaX > 0) {
			return x + deltaX < len ? x + deltaX : xprime;
		} else {
			return x + deltaX > 0 ? x + deltaX : xprime;
		}
	}	
	
	
	public static void main(String[] args) {
		Stack<Integer> stk = new Stack<Integer>();
		stk.push(1);
		stk.push(2);
		stk.push(3);
		stk.push(4);
		
		List<Integer> lst = new LinkedList<Integer>(stk);
		for(Integer i : lst){
			System.out.println(i);
		}
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
