package mirroruniverse.gxplayer;

import graph.test.PointPair;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jgrapht.alg.*;
import org.jgrapht.*;
import org.jgrapht.graph.*;

import mirroruniverse.sim.Player;

public class GxPlayer implements Player {

	public static final int U=3,D=7,R=1,L=5;
	private DirectedGraph<PointPair, DefaultEdge> graph;
	private int round;

	private PointPair exit;
	private PointPair start;
	private PointPair[][][][] pc;
	private PointPair[][][][] bfsPath;
	List<DefaultEdge> path;

	public GxPlayer() {
		graph = new DefaultDirectedGraph<PointPair, DefaultEdge>
        (DefaultEdge.class);
		round = 0;
	}

	@Override
	public int lookAndMove(int[][] aintViewL, int[][] aintViewR) {
		round++;
		if (round == 1) {
			pc = new PointPair[aintViewL.length][aintViewL[0].length][aintViewR.length][aintViewR[0].length];
			bfsPath = new PointPair[aintViewL.length][aintViewL[0].length][aintViewR.length][aintViewR[0].length];
			buildGraph(aintViewL, aintViewR);
			System.out.println(graph);
		}
		for(DefaultEdge e : graph.edgeSet()){
			System.out.println(e);
			break;
		}
		
		path = DijkstraShortestPath.findPathBetween(graph, start, exit);//just for demo, not efficient
		System.out.println("=====path=========="+path);
		//so far, the path is found
		//but the getSource() function is protected
		
//		PointPair source =(PointPair) path.get(round-1).getSource();
//		PointPair target = (PointPair) path.get(round-1).getTarget();
//		int dx = target.getLeftx()-source.getLeftx();
//		dx = dx==0?target.getRightx()-source.getRightx():dx;
//		if(dx==1){
//			return D;
//		}
//		if(dx==-1){
//			return U;
//		}
//		int dy = target.getLefty()-source.getLefty();
//		dy = dy==0?target.getRighty()-source.getRighty():dx;
//		
//		if(dy==1){
//			return R;
//		}
//		if(dy==-1){
//			return L;
//		}
//
		return -1;
	}


	private void buildGraph(int[][] aintViewL, int[][] aintViewR) {
		int lx, ly, rx, ry;
		// add vertex
		for (lx = 0; lx < aintViewL.length; lx++) {
			for (ly = 0; ly < aintViewL[0].length; ly++) {
				for (rx = 0; rx < aintViewR.length; rx++) {
					for (ry = 0; ry < aintViewR[0].length; ry++) {
						pc[lx][ly][rx][ry] = new PointPair(lx, ly, rx, ry);
						this.graph.addVertex(pc[lx][ly][rx][ry]);
						if (aintViewL[lx][ly] == 2 && aintViewR[rx][ry] == 2) {
							this.exit = pc[lx][ly][rx][ry];
							System.out.println("=====exit========"+this.exit);
						}
					}
				}
			}
		}
		this.start = pc[aintViewL.length/2][aintViewL[0].length/2][aintViewR.length/2][aintViewR[0].length/2];
		System.out.println("=====start========"+this.start);
		// add edges
		for (lx = 0; lx < aintViewL.length; lx++) {
			for (ly = 0; ly < aintViewL[0].length; ly++) {
				for (rx = 0; rx < aintViewR.length; rx++) {
					for (ry = 0; ry < aintViewR[0].length; ry++) {
						// Go Right
						if (lx + 1 < aintViewL.length
								&& aintViewL[lx + 1][ly] != 1) {
							if (rx + 1 < aintViewR.length
									&& aintViewR[rx + 1][ry] != 1)
								this.graph.addEdge(pc[lx][ly][rx][ry],
										pc[lx + 1][ly][rx + 1][ry]);
							else
								this.graph.addEdge(pc[lx][ly][rx][ry],
										pc[lx + 1][ly][rx][ry]);
						} else {
							if (rx + 1 < aintViewR.length
									&& aintViewR[rx + 1][ry] != 1)
								this.graph.addEdge(pc[lx][ly][rx][ry],
										pc[lx][ly][rx + 1][ry]);
							else
								;// can't move, stay put
						}

						// Go Left
						if (lx - 1 >= 0 && aintViewL[lx - 1][ly] != 1) {
							if (rx - 1 >= 0 && aintViewR[rx - 1][ry] != 1)
								this.graph.addEdge(
										pc[lx][ly][rx][ry],
										pc[lx-1][ly][rx-1][ry]);
							else
								this.graph.addEdge(
										pc[lx][ly][rx][ry],
										pc[lx-1][ly][rx][ry]);
						} else {
							if (rx - 1 >= 0 && aintViewR[rx - 1][ry] != 1)
								this.graph.addEdge(
										pc[lx][ly][rx][ry],
										pc[lx][ly][rx-1][ry]);
							else
								;// can't move, stay put
						}

						// Go down
						if (ly - 1 >= 0 && aintViewL[lx][ly - 1] != 1) {
							if (ry - 1 >= 0 && aintViewR[rx][ry - 1] != 1)
								this.graph.addEdge(
										pc[lx][ly][rx][ry],
										pc[lx][ly-1][rx][ry-1]);
							else
								this.graph.addEdge(
										pc[lx][ly][rx][ry],
										pc[lx][ly-1][rx][ry]);
						} else {
							if (ry - 1 >= 0 && aintViewR[rx][ry - 1] != 1)
								this.graph.addEdge(
										pc[lx][ly][rx][ry],
										pc[lx][ly][rx][ry-1]);
							else
								;// can't move, stay put
						}

						// Go Up
						if (ly + 1 < aintViewL[0].length
								&& aintViewL[lx][ly + 1] != 1) {
							if (ry + 1 < aintViewR[0].length
									&& aintViewR[rx][ry + 1] != 1)
								this.graph.addEdge(
										pc[lx][ly][rx][ry],
										pc[lx][ly+1][rx][ry+1]);
							else
								this.graph.addEdge(
										pc[lx][ly][rx][ry],
										pc[lx][ly+1][rx][ry]);
						} else {
							if (ry + 1 < aintViewR[0].length
									&& aintViewR[rx][ry + 1] != 1)
								this.graph.addEdge(
										pc[lx][ly][rx][ry],
										pc[lx][ly][rx][ry+1]);
							else
								;// can't move, stay put
						}

					}
				}
			}
		}

	}

}
