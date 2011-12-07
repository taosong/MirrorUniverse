package mirroruniverse.g3player;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.alg.*;
import org.jgrapht.*;
import org.jgrapht.graph.*;

import mirroruniverse.sim.Player;

public class CopyOfG3Player implements Player {

	public static final int U = 3, D = 7, R = 1, L = 5, LU = 4, RU = 2, LD = 6,
			RD = 8;
	public static final boolean printGraph = false;
	public static final int SENTINEL = -1;

	private DefaultDirectedWeightedGraph<PointPair, SimpleEdge> graph;
	private int round;

	private PointPair exit;
	private PointPair start;
	private PointPair source = null;
	private Point startLeft = null;
	private Point startRight = null;
	private PointPair[][][][] pc;
	
	private static final Point PSENTINEL = new Point(-1,-1);
	private static final PointPair UNEXPLORED = new PointPair(-1,-1,-1,-1);
	private PointPair[][][][] bfsPath;
	List<SimpleEdge> path;
	SimpleEdge e;

	public CopyOfG3Player() {
		graph = new DefaultDirectedWeightedGraph<PointPair, SimpleEdge>(
				SimpleEdge.class);
		round = 0;
	}

	@Override
	public int lookAndMove(int[][] aintViewL, int[][] aintViewR) {
		round++;
		if (round==1) {
			buildGraph(aintViewL, aintViewR);
//			System.out.println(graph);
		}

		path = DijkstraShortestPath.findPathBetween(graph, start, exit);// just
																		// for
																		// demo,
																		// not
																		// efficient

		System.out.println("=====path==========" + path);
		// so far, the path is found
		// but the getSource() function is protected

		PointPair source = path.get(round-1).getFrom();
		System.out.println("source "+source);
		PointPair target = path.get(round-1).getTo();
		System.out.println("target "+target);
		int dx = target.getLeftx() - source.getLeftx();
		dx = dx == 0 ? target.getRightx() - source.getRightx() : dx;
		int dy = target.getLefty() - source.getLefty();
		dy = dy == 0 ? target.getRighty() - source.getRighty() : dy;
		if (dx == 1 && dy == 1) {
			System.out.println("RD");
			return RD;
		} else if (dx == 1 && dy == -1) {
			System.out.println("LU");
			return LD;
		} else if (dx == -1 && dy == 1) {
			System.out.println("RU");
			return RU;
		} else if (dx == -1 && dy == -1) {
			System.out.println("LU");
			return LU;
		}
		if (dy == 1) {
			System.out.println("L");
			return R;
		}
		if (dy == -1) {
			System.out.println("R");
			return L;
		}
		if (dx == 1) {
			System.out.println("D");
			return D;
		}
		if (dx == -1) {
			System.out.println("U");
			return U;
		}
		//
		return -1;
	}

	private int incr(int len, int x, int deltaX) {
		if (deltaX > 0) {
			return x + deltaX < len ? x + deltaX : SENTINEL;
		} else {
			return x + deltaX > 0 ? x + deltaX : SENTINEL;
		}

	}

	private Point getNextPoint(int[][] arr, int x, int y, int deltaX, int deltaY) {
		int xprime = incr(arr.length, x, deltaX);
		int yprime = incr(arr[0].length, y, deltaY);
		if(xprime == SENTINEL || yprime == SENTINEL){
			return PSENTINEL;
		}
		return (arr[xprime][yprime] == 1) ? new Point(x, y) : new Point(xprime,
				yprime);
	}

	private PointPair getNextState(int lx, int ly, int rx, int ry, int deltaX,
			int deltaY, int[][] left, int[][] right) {
		Point leftPoint = getNextPoint(left, lx, ly, deltaX, deltaY);
		Point rightPoint = getNextPoint(right, rx, ry, deltaX, deltaY);
		
		if(leftPoint == PSENTINEL || rightPoint == PSENTINEL){
			return UNEXPLORED;
		}
		return new PointPair(leftPoint.getX(), leftPoint.getY(),
				rightPoint.getX(), rightPoint.getY());
	}

	private void buildGraph(int[][] aintViewL, int[][] aintViewR) {
		int lx, ly, rx, ry;
		// add vertex
		// flag accepting state if one exists
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
						
							if (printGraph)
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
		this.start = pc[aintViewL.length / 2][aintViewL[0].length / 2][aintViewR.length / 2][aintViewR[0].length / 2];
		System.out.println("=====start========" + this.start);

		// add edges
		for (lx = 0; lx < aintViewL.length; lx++) {
			for (ly = 0; ly < aintViewL[0].length; ly++) {
				for (rx = 0; rx < aintViewR.length; rx++) {
					for (ry = 0; ry < aintViewR[0].length; ry++) {

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
										    graph.setEdgeWeight(e, 1);
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
										    graph.setEdgeWeight(e, 1000);
										}
									}
								}
							}
						}


					}
				}
			}
		}

	}

}
