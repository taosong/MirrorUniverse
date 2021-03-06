package mirroruniverse.g3pathfinder;

import gnu.trove.list.TIntList;
import gnu.trove.list.linked.TIntLinkedList;
import gnu.trove.map.hash.TIntIntHashMap;

import java.util.LinkedList;
import java.util.List;
import java.util.Random;
import java.util.Stack;

public class BFS {
	int leftExitAlone = Integer.MIN_VALUE;
	int rightExitAlone = Integer.MIN_VALUE;
	
	boolean debug = false;
	
	private static final int[][] dirs = {{4,5,6},{3,0,7},{2,1,8}};
	private static final int MAXSIZE = 25*1000*1000;
	int exit = Integer.MIN_VALUE;
	private boolean printMaps = false;
	
	public int bfs(final int[][] leftView, final int[][] rightView,
			final int startly, final int startlx, final int startry, final int startrx,
			final int exitlx, final int exitly, final int exitrx, final int exitry, List<Integer> path){
		TIntIntHashMap parent = new TIntIntHashMap();
		path.clear();
		TIntList queue = new TIntLinkedList();
		printMaps(leftView, rightView);
		
		int source = Node.getHash((byte)(startlx-100), (byte)(startly-100),
				(byte)(startrx-100), (byte)(startry-100));
		if(debug)System.out.println("Source: {("+startly+","+startlx+")("+startry+","+startrx+")}");
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
			
			if(!(isInBound(leftView.length, i) && isInBound(leftView[0].length, j)
					&& isInBound(rightView.length, k) && isInBound(rightView[0].length, l))) continue;
			
			if(leftView[i][j] == 2 || rightView[k][l] == 2) continue;
			
			label:
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
					
					if(debug)System.out.println("Opening node: {("+iprime+","+jprime+")("+kprime+","+lprime+")} - ("+
							leftView[iprime][jprime]+","+rightView[kprime][lprime]+")"+
							" ----- {("+i+","+j+")("+k+","+l+"");
					
					try {
						// Make u the parent of v and add v to the queue - classic BFS
						parent.put(v, u);
						queue.add(v);
					} catch (Throwable e) {
						System.out.println("Parent size: "+parent.size());
						System.out.println("Queue size: "+queue.size());
						e.printStackTrace();
						
					}
					// If you find that both are able to exit, add parent pointers and mark as visited and break
					// out of loop
					if (leftView[iprime][jprime] == 2 && rightView[kprime][lprime] == 2){
						//System.out.println("Perfect Solution found!!");
						assert(iprime == exitlx && jprime == exitly && kprime == exitrx && lprime == exitry);
						retVal = 0;
						break breakLabel;
					}
					if (parent.size() > MAXSIZE){
						//System.out.println("MAXSIZE REACHED");
						//break breakLabel;
					}
					
				}
		}	
		
		int v = Node.getHash((byte)(exitlx-100), (byte)(exitly-100),
				(byte)(exitrx-100), (byte)(exitry-100));
		
		if(parent.containsKey(v)){
			if(debug)System.out.println("parent" + parent);
			if(debug)System.out.println("key" + v);
			getPathToRoot(parent, v, path);
			if(debug)
				System.out.println(":::::::::::::return perfect = " + retVal);
			return retVal;
		}
		
		if(debug)System.out.println("No perfect solution found");
		
		List<Integer> rightExitFirst = bfs2d(leftView, exitlx, exitly, exitrx, exitry, true, parent);
		if(debug)System.out.println("left");
		List<Integer> leftExitFirst = bfs2d(rightView, exitrx, exitry, exitlx, exitly, false, parent);
		

		//assert(rightExitFirst != null && leftExitFirst != null);
		
		if(rightExitFirst == null || leftExitFirst == null){
			return -1;
		}
		
		// bookmark magic change
		if(leftExitFirst.size() < rightExitFirst.size()){
			getPathToRoot(parent, rightExitAlone, path);
			path.addAll(leftExitFirst);
			retVal = leftExitFirst.size();
		}
		else{
			getPathToRoot(parent, leftExitAlone, path);
			path.addAll(rightExitFirst);
			retVal = rightExitFirst.size();
		}
		
		if(debug)
			System.out.println(":::::::::::::return = " + retVal);
		return retVal;
				
	}
	
	
	private boolean isInBound(int len, int index){
		return index >= 0  && index < len;
	}
	
	
	private void getPathToRoot(TIntIntHashMap pi, int v, List<Integer> path ){
		
		if(pi.containsKey(v)){
			if(debug)System.out.println("pi" + pi);
			if(debug)System.out.println("key" + v);
			Stack<Integer> stk = new Stack<Integer>();
			while(pi.get(v) != Integer.MIN_VALUE){
				if(debug)System.out.println("Adding dir: "+getDir(pi.get(v), v));
				stk.push(getDir(pi.get(v), v));
				v = pi.get(v);
			}
			while(!stk.isEmpty()){
				path.add(stk.pop());
			}
			
		}
		
	}
	
	public List<Integer> bfs2d(int[][] view, int x, int y, int ex, int ey, boolean isLeft, TIntIntHashMap parent){
		
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
					
					int iprime = -1, jprime = -1;
					
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
					
					if(debug) System.out.println("Opening node: ("+iprime+","+jprime+") - ("+view[iprime][jprime]+")" + 
					" - " + unpack(u)[1]+","+unpack(u)[0]);
					
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
						if(isLeft){
							leftExitAlone = state;
						}else{
							rightExitAlone = state;
						}
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
		int dy = uS[1] - vS[1];
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
		if(!printMaps )
			return;
		
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

		//book
		int[][] leftView = {{1,0,0,1,1,1},
				            {1,1,1,0,0,0},
				            {1,1,0,0,0,1},
				            {1,1,0,0,0,1},
				            {1,1,0,0,1,1},
				            {1,1,0,0,1,2}};
		
		int[][] rightView = {{1,0,0,1,1,1},
				            {1,1,1,0,0,0},
				            {1,1,0,0,0,1},
				            {1,1,0,0,0,1},
				            {1,1,0,0,0,4},
				            {1,1,0,1,1,2}};
		BFS bfs = new BFS();
		List<Integer> path = new LinkedList<Integer>(); 
		int ret = bfs.bfs(leftView, rightView, 0,1,0,1,5,5,5,5, path);
		
		System.out.println(ret);
		System.out.println(path);
		
		TIntIntHashMap test = new TIntIntHashMap();
		int i = 0;
		Random rand = new Random();
		int x = 100; 
		byte[][][][] test1 = new byte[x][x][x][x];
		try {
			// Make u the parent of v and add v to the queue - classic BFS
			while(true){
				i++;
				test.put(i, rand.nextInt());
			}
		} catch (Throwable e) {
			System.out.println("test size: "+test.size());
			e.printStackTrace();
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
