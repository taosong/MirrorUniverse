package mirroruniverse.stupidplayer;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.jgrapht.alg.DijkstraShortestPath;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;

public class LowerBoundDetecter {
	public static final int BOND = 8;
	public static final int MAXSIZE = (BOND+1)*2+1;
	private int[][] localLeftMap;
	private int[][] localRightMap;
	public static final boolean printGraph = true;

	private DefaultDirectedWeightedGraph<PointPair, SimpleEdge> graph;
	
	private PointPair exit;
	private PointPair[][][][] pc;
	List<SimpleEdge> path;
	SimpleEdge e;
	ArrayList<PointPair> out;
	
	public LowerBoundDetecter(){
		localLeftMap = new int[MAXSIZE][MAXSIZE];
		localRightMap = new int[MAXSIZE][MAXSIZE];
		pc = new PointPair[MAXSIZE][MAXSIZE][MAXSIZE][MAXSIZE];
		out = new ArrayList<PointPair>();
	}
	
	public int getLowerBound(int[][] leftMap, int[][] rightMap, int leftExitx, int leftExity, int rightExitx, int rightExity){
		graph = new DefaultDirectedWeightedGraph<PointPair, SimpleEdge>(
				SimpleEdge.class);
		buildLocalMap(leftMap, rightMap, leftExitx, leftExity, rightExitx, rightExity);
		buildGraph(localLeftMap,localRightMap);
		PointPair exit = pc[BOND+1][BOND+1][BOND+1][BOND+1];
		PointPair start = pc[0][0][0][0];
		path = DijkstraShortestPath.findPathBetween(graph, start, exit);

		//System.out.println("=====path==========" + path);
		int sum = 0;
		if(path!=null){
			for(SimpleEdge e : path){
				sum+=e.getWeight();
			}
		}
		else
			System.out.println("null path in Lower Bond Detecter");
		return sum;
	}
	
	public static void main(String[] args) {
		LowerBoundDetecter lb = new LowerBoundDetecter();
		int[][] leftMap = {{0,0,1},{1,2,0},{1,0,0}};

		for(int i = 0; i<leftMap.length;i++){
			for(int j = 0;j<leftMap[0].length;j++){
				System.out.print(leftMap[i][j]+" ");
			}
			System.out.println();
		}
		System.out.println(leftMap.length);
		int[][] rightMap = {{1,1,1,0},{1,1,2,1},{1,0,1,1}};
		for(int i = 0; i<rightMap.length;i++){
			for(int j = 0;j<rightMap[0].length;j++){
				System.out.print(rightMap[i][j]+" ");
			}
			System.out.println();
		}
		int diff = lb.getLowerBound(leftMap, rightMap, 1, 1, 1, 2);
		//System.out.println("diff = " + diff);
	}

	private void buildLocalMap(int[][] leftMap, int[][] rightMap,int leftExitx, int leftExity, int rightExitx, int rightExity) {
		out.clear();
		for(int x = -BOND; x<=BOND;x++){
			for(int y = -BOND;y<=BOND;y++){
				if((x+leftExitx)<leftMap.length && (y+leftExity)<leftMap[0].length && (x+leftExitx)>=0 && (y+leftExity)>=0)
					localLeftMap[x+BOND+1][y+BOND+1] = leftMap[x+leftExitx][y+leftExity]==1?1:0;
				else
					localLeftMap[x+BOND+1][y+BOND+1] = 0;
				if(x+rightExitx<rightMap.length&&y+rightExity<rightMap[0].length && (x+rightExitx)>=0 && (y+rightExity)>=0)
					localRightMap[x+BOND+1][y+BOND+1] = rightMap[x+rightExitx][y+rightExity]==1?1:0;
				else
					localRightMap[x+BOND+1][y+BOND+1] = 0;
			}
		}
		localLeftMap[BOND+1][BOND+1] = 2;
		localRightMap[BOND+1][BOND+1] = 2;
	}
	

	private int incr(int len, int x, int deltaX) {
		if (deltaX > 0) {
			return x + deltaX < len ? x + deltaX : x;
		} else {
			return x + deltaX > 0 ? x + deltaX : x;
		}

	}

	private Point getNextPoint(int[][] arr, int x, int y, int deltaX, int deltaY) {
		int xprime = incr(arr.length, x, deltaX);
		int yprime = incr(arr[0].length, y, deltaY);
		return (arr[xprime][yprime] == 1) ? new Point(x, y) : new Point(xprime,
				yprime);
	}

	private PointPair getNextState(int lx, int ly, int rx, int ry, int deltaX,
			int deltaY, int[][] left, int[][] right) {
		Point leftPoint = getNextPoint(left, lx, ly, deltaX, deltaY);
		Point rightPoint = getNextPoint(right, rx, ry, deltaX, deltaY);
		return new PointPair(leftPoint.getX(), leftPoint.getY(),
				rightPoint.getX(), rightPoint.getY());
	}

	private void buildGraph(int[][] aintViewL, int[][] aintViewR) {
		int lx, ly, rx, ry;
		if (printGraph) {
			System.out.println("Left View:");
			for (int i = 0; i < aintViewL.length; i++) {
				for (int j = 0; j < aintViewL.length; j++)
					System.out.print(aintViewL[i][j] + " ");
				System.out.println();
			}

			System.out.println("Right View:");
			for (int i = 0; i < aintViewR.length; i++) {
				for (int j = 0; j < aintViewR.length; j++)
					System.out.print(aintViewR[i][j] + " ");
				System.out.println();
			}
		}

		for (lx = 0; lx < aintViewL.length; lx++) {
			for (ly = 0; ly < aintViewL[0].length; ly++) {
				for (rx = 0; rx < aintViewR.length; rx++) {
					for (ry = 0; ry < aintViewR[0].length; ry++) {
						if(aintViewL[lx][ly]!=1&&aintViewR[rx][ry]!=1){
							pc[lx][ly][rx][ry] = new PointPair(lx, ly, rx, ry);

							if (printGraph&&false)
								System.out.println(pc[lx][ly][rx][ry] + "\t("
										+ aintViewL[lx][ly] + ","
										+ aintViewR[rx][ry] + ")");

							this.graph.addVertex(pc[lx][ly][rx][ry]);
						}
						if (aintViewL[lx][ly] == 2 && aintViewR[rx][ry] == 2) {
							this.exit = pc[lx][ly][rx][ry];
							System.out.println("=====exit========" + this.exit);
						}
					}
				}
			}
		}

		// add edges
		for (lx = 0; lx < aintViewL.length; lx++) {
			for (ly = 0; ly < aintViewL[0].length; ly++) {
				for (rx = 0; rx < aintViewR.length; rx++) {
					for (ry = 0; ry < aintViewR[0].length; ry++) {
						
						// record all the edge vertices to a set
						if(lx==0 || lx==MAXSIZE-1 || ly==0 || ly==MAXSIZE-1)
							if(rx==0 || rx==MAXSIZE-1 || ry==0 || ry==MAXSIZE-1)
								out.add(pc[lx][ly][rx][ry]);

						if (aintViewL[lx][ly] == 1 || aintViewR[rx][ry] == 1)
							continue;

						for (int deltaX = -1; deltaX < 2; deltaX++) {
							for (int deltaY = -1; deltaY < 2; deltaY++) {
								PointPair newState = getNextState(lx, ly, rx,
										ry, deltaX, deltaY, aintViewL,
										aintViewR);
								if (!newState.equals(pc[lx][ly][rx][ry])) {
									if(aintViewL[lx][ly]!=2&&aintViewR[rx][ry]!=2)
									{
										    e=graph.addEdge(pc[lx][ly][rx][ry],
											pc[newState.getLeftx()][newState
													.getLefty()][newState
													.getRightx()][newState
													.getRighty()]);
										    if(e!=null)
										    	graph.setEdgeWeight(e, 0);
									}
									else{
										if(aintViewL[lx][ly]==2){
											newState.setLeftx(lx);
											newState.setLefty(ly);
										}
										if(aintViewR[rx][ry]==2){
											newState.setRightx(rx);
											newState.setRighty(ry);
										}
										if(!newState.equals(pc[lx][ly][rx][ry])){
										    e=graph.addEdge(pc[lx][ly][rx][ry],
											pc[newState.getLeftx()][newState
													.getLefty()][newState
													.getRightx()][newState
													.getRighty()]);
										    if(e!=null)
										    	graph.setEdgeWeight(e, 1);
										}
									}
								}
							}
						}
					}
				}
			}
		}
		//find u!
		
		for(int i =0; i<out.size()-1;i++){
			e=graph.addEdge(out.get(i), out.get(i+1));
			if(e!=null)
				graph.setEdgeWeight(e, 0);
		}
		e=graph.addEdge(out.get(out.size()-1), out.get(0));
		if(e!=null)
			graph.setEdgeWeight(e, 0);

	}
	

}
