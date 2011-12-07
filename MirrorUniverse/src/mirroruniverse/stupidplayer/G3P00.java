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
	
	public int bfs(final int[][] leftView, final int[][] rightView,
			final int startly, final int startlx, final int startry, final int startrx,
			final int exitlx, final int exitly, final int exitrx, final int exitry, List<Integer> path){
		
		path.clear();
		TIntList queue = new TIntLinkedList();
		printMaps(leftView, rightView);
		
		int source = Node.getHash((byte)(startlx-100), (byte)(startly-100),
				(byte)(startrx-100), (byte)(startry-100));
		System.out.println("Source: {("+startly+","+startlx+")("+startry+","+startrx+")}");
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
					
					System.out.println("Opening node: {("+iprime+","+jprime+")("+kprime+","+lprime+")} - ("+
							leftView[iprime][jprime]+","+rightView[kprime][lprime]+")");
					
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
			Stack<Integer> stk = new Stack<Integer>();
			while(parent.get(v) != Integer.MIN_VALUE){
				System.out.println("Adding dir: "+getDir(parent.get(v), v));
				stk.push(getDir(parent.get(v), v));
				v = parent.get(v);
			}
			while(!stk.isEmpty()){
				path.add(stk.pop());
			}
			return retVal;
		}
		
		System.out.println("No perfect solution found");
		
		List<Integer> rightExitFirst = bfs2d(leftView, startlx, startly, exitrx, exitry, true);
		List<Integer> leftExitFirst = bfs2d(rightView, startrx, startry, exitlx, exitly, false);
		
		assert(rightExitFirst != null && leftExitFirst != null);
		
		if(leftExitFirst.size() < rightExitFirst.size()){
			path.addAll(leftExitFirst);
			retVal = leftExitFirst.size();
		}
		else{
			path.addAll(rightExitFirst);
			retVal = rightExitFirst.size();
		}
		
		return retVal;
				
	}
	
	public List<Integer> bfs2d(int[][] view, int x, int y, int ex, int ey, boolean isLeft){
		
		TIntList queue = new TIntLinkedList();
		TIntIntHashMap pi = new TIntIntHashMap();
		
		int exit = Integer.MIN_VALUE;
		
		int source = pack(x,y);
		queue.add(source);
		pi.put(source, Integer.MIN_VALUE);
		
		breakLabel:
		while(!queue.isEmpty()){
			int u = queue.removeAt(0);
			short[] ushorts = unpack(u);
			int j = ushorts[0];
			int i = ushorts[1];
			
			for (int deltaX = -1; deltaX < 2; deltaX++)
				for (int deltaY = -1; deltaY < 2; deltaY++) {
					
					int iprime = -1, jprime = -1, kprime = -1, lprime = -1;
					
					if (deltaX == 0 && deltaY == 0)continue;
					
					iprime = incr(view.length, i, deltaY, iprime);
					jprime = incr(view[0].length, j, deltaX, jprime);
					
					// If no increment is possible, continue
					if (iprime == -1 || jprime == -1) continue;

					// If you have hit an obstacle then continue
					if (view[iprime][jprime] == 1) continue;
					
					// If this move takes us into unexplored territory, then continue
					if(view[iprime][jprime] == 4) continue;

					int v = pack(jprime, iprime);
					
					// If we have visited this node earlier, continue
					if(pi.containsKey(v)) continue;
					
					System.out.println("Opening node: ("+iprime+","+jprime+") - ("+view[iprime][jprime]+")");
					
					// Make u the parent of v and add v to the queue - classic BFS
					pi.put(v, u);
					queue.add(v);
					
					int state;
					
					if(isLeft){
						state = Node.getHash((byte)(jprime - 100), (byte)(iprime - 100), (byte)(ex-100), (byte)(ey-100));
					}else{
						state = Node.getHash((byte)(ex-100), (byte)(ey-100), (byte)(jprime - 100), (byte)(iprime - 100));
					}
					
					if (parent.containsKey(state)){
						// You have reached the exit state!!
						exit = v;
						break breakLabel;
					}
					
				}	
		}
		
		if(exit != Integer.MIN_VALUE){
			int v = exit;
			List<Integer> path = new LinkedList<Integer>();
			while(pi.get(v) != Integer.MIN_VALUE){
				int u = pi.get(v);
				path.add(getDir2d(u, v));
				v = u;
			}
			return path;
		}
		
		return null;
	}
	
	private int pack(int x, int y){
		return (x & 0xFFFF)+((y & 0xFFFF)<<16);
	}
	
	private short[] unpack(int x){
		return new short[]{(short) x, (short)(x >>> 16)};
	}
	
	private int getDir2d(int u, int v){
		short[] uS = unpack(u);
		short[] vS = unpack(v);
		int dx = uS[0] - vS[0];
		int dy = vS[1] - vS[1];
		return dirs[dx+1][dy+1];
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
			return x + deltaX >= 0 ? x + deltaX : xprime;
		}
	}
	
	public void printMaps(int[][] leftView, int[][] rightView){
		System.out.println("Left View: ");
		printView(leftView);
		System.out.println("Right View: ");
		printView(rightView);
	}
	
	public void printView(int[][] leftView){
		for(int i=0; i<leftView.length; i++){
			for(int j=0; j<leftView[0].length; j++){
				System.out.print(leftView[i][j]+" ");
			}
			System.out.println();
		}
	}
	
	public static void main(String[] args) {
		int[][] leftView = {{1,1,0},{0,0,0},{2,0,0}};
		int[][] rightView = {{1,1,0},{0,0,0},{2,0,0}};
		G3P00 g3p00 = new G3P00();
		List<Integer> path = new LinkedList<Integer>(); 
		g3p00.bfs(leftView, rightView, 0,2,0,2,0,2,0,2, path);
		System.out.println(path);
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
