package mirroruniverse.g3player;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.jgrapht.alg.*;
import org.jgrapht.*;
import org.jgrapht.graph.*;

import mirroruniverse.sim.Player;

public class G3Player implements Player {

	public static final int U = 3, D = 7, R = 1, L = 5, LU = 4, RU = 2, LD = 6,
			RD = 8;
	public static final boolean printGraph = false;

	private DefaultDirectedWeightedGraph<PointPair, SimpleEdge> graph;
	private int round;

	private PointPair exit;
	private PointPair start;
	private PointPair source = null;
	private Point startLeft = null;
	private Point startRight = null;
	private PointPair[][][][] pc;
	private PointPair offset;
	
	Random rand = new Random(); 
	List<SimpleEdge> path;
	SimpleEdge e;
	int maxlength = 40;

	public G3Player() {
		graph = new DefaultDirectedWeightedGraph<PointPair, SimpleEdge>(
				SimpleEdge.class);
		round = 0;
		pc = new PointPair[maxlength][maxlength][maxlength][maxlength];
		offset = new PointPair(0, 0, 0, 0);
	}

	@Override
	public int lookAndMove(int[][] aintViewL, int[][] aintViewR) {
		round++;
		if (true) {
			buildGraph(aintViewL, aintViewR);
		}

		if(exit==null){
			return rand.nextInt(8)+1;
		}
		start.updateLeftx(offset.getLeftx());
		start.updateLefty(offset.getLefty());
		start.updateRightx(offset.getRightx());
		start.updateRighty(offset.getRighty());
		System.out.println("=====start========" + this.start);
		
		path = DijkstraShortestPath.findPathBetween(graph, start, exit);
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
		offset.update(target,source);
		
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

	private void updateGraph(int[][] aintViewL, int[][] aintViewR) {
		// TODO Auto-generated method stub
		
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
						int nlx = lx + offset.getLeftx();
						int nly = ly + offset.getLefty();
						int nrx = rx + offset.getRightx();
						int nry = ry + offset.getRighty();
						if(aintViewL[lx][ly]!=1&&aintViewR[rx][ry]!=1){
							pc[nlx][nly][nrx][nry] = new PointPair(nlx, nly, nrx, nry);
						
							if (printGraph)
								System.out.println(pc[lx][ly][rx][ry] + "\t("
										+ aintViewL[lx][ly] + ","
										+ aintViewR[rx][ry] + ")");
						
							this.graph.addVertex(pc[nlx][nly][nrx][nry]);
						}
						if (aintViewL[lx][ly] == 2 && aintViewR[rx][ry] == 2) {
							this.exit = pc[nlx][nly][nrx][nry];
							System.out.println("=====exit========" + this.exit);
						}
					}
				}
			}
		}
		if(round==1){
			this.start = pc[aintViewL.length / 2][aintViewL[0].length / 2][aintViewR.length / 2][aintViewR[0].length / 2];
		}


		// add edges
		for (lx = 0; lx < aintViewL.length; lx++) {
			for (ly = 0; ly < aintViewL[0].length; ly++) {
				for (rx = 0; rx < aintViewR.length; rx++) {
					for (ry = 0; ry < aintViewR[0].length; ry++) {

						if (aintViewL[lx][ly] == 1 || aintViewR[rx][ry] == 1 || pc[lx][ly][rx][ry] != null)
							continue;
						int nlx = lx + offset.getLeftx();
						int nly = ly + offset.getLefty();
						int nrx = rx + offset.getRightx();
						int nry = ry + offset.getRighty();
						for (int deltaX = -1; deltaX < 2; deltaX++) {
							for (int deltaY = -1; deltaY < 2; deltaY++) {
								PointPair newState = getNextState(lx, ly, rx,
										ry, deltaX, deltaY, aintViewL,
										aintViewR);
								newState.updateLeftx(offset.getLeftx());
								newState.updateLefty(offset.getLefty());
								newState.updateRightx(offset.getRightx());
								newState.updateRighty(offset.getRighty());
								if (!newState.equals(pc[nlx][nly][nrx][nry])) {
									if(aintViewL[lx][ly]!=2&&aintViewR[rx][ry]!=2)
									{
										    e = graph.addEdge(pc[nlx][nly][nrx][nry],
											pc[newState.getLeftx()][newState
													.getLefty()][newState
													.getRightx()][newState
													.getRighty()]);
										    if(e != null) graph.setEdgeWeight(e, 1);
									}
									else{
										if(aintViewL[lx][ly]==2){
											newState.setLeftx(nlx);
											newState.setLefty(nly);
										}
										if(aintViewR[rx][ry]==2){
											newState.setRightx(nrx);
											newState.setRighty(nry);
										}
										if(!newState.equals(pc[nlx][nly][nrx][nry])){
										    e=graph.addEdge(pc[nlx][nly][nrx][nry],
											pc[newState.getLeftx()][newState
													.getLefty()][newState
													.getRightx()][newState
													.getRighty()]);
										    if(e !=  null) graph.setEdgeWeight(e, 1000);
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
