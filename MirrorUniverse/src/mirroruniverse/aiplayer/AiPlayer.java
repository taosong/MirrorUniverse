package mirroruniverse.aiplayer;

import java.util.*;

import mirroruniverse.sim.MUMap;
import mirroruniverse.sim.Player;

public class AiPlayer implements Player
{
	boolean blnLOver = false;
	boolean blnROver = false;
	int round=0;
	int call=0;

	int lPlayer_X_Exit;
	int lPlayer_Y_Exit;
	int rPlayer_X_Exit;
	int rPlayer_Y_Exit;


	int lPlayer_X_Position;
	int lPlayer_Y_Position;
	int rPlayer_X_Position;
	int rPlayer_Y_Position;

	ArrayList<Integer> movesList;

	boolean leftExitFound=false;
	boolean rightExitFound=false;

	int[][] leftMap;
	int[][] rightMap;

	int leftSightRadius;
	int rightSightRadius;

	int maxMapSideLength=11;
	// in the problem this is 100 but currently we are working just for max size to be 50
	int maxVirtualMapsize=maxMapSideLength*2+1;

	int xShiftLeftCumulative=0, yShiftLeftCumulative=0;
	int xShiftRightCumulative=0,yShiftRightCumulative=0;

	int lastMove=0;

	public static final int[][] movesArray = { { 0, 0 }, { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, -1 },  { -1, 0 }, { -1, 1 }, { 0, 1 }, { 1, 1 } };
	public int[][][][] state;
	//public int[][][][] stateDistance;
	/* these states will store an integer which will have the value of the move through which this state was attained
	 * with the value of the move we can retrace the previous state
	 * (non-Javadoc)
	 * @see mirroruniverse.sim.Player#lookAndMove(int[][], int[][])
	 */

	public int lookAndMove( int[][] aintViewL, int[][] aintViewR )
	{
		round++;
		//System.out.println(java.lang.Runtime.getRuntime().maxMemory()); 
		if( round ==1) {
			setLeftRightMap(aintViewL,aintViewR);

			//state = new int[leftMap[0].length][leftMap.length][rightMap[0].length][rightMap.length];
			state = new int[maxVirtualMapsize][maxVirtualMapsize][maxVirtualMapsize][maxVirtualMapsize];
			//stateDistance = new int[leftMap[0].length][leftMap.length][rightMap[0].length][rightMap.length];
		}
		updateLeftRightMap(aintViewL,aintViewR);
//		setMoves(aintViewL,aintViewR);
		setMovesRandom(aintViewL,aintViewR);
		System.out.println("round "+round);
		System.out.println("lastMove "+lastMove);
		lastMove=movesList.remove(0);
		System.out.println("movesList "+movesList);
		updateCurrentPosition(lastMove);
		return lastMove;
	}


	public void updateCurrentPosition(int moveActuallyMade) {

		int xShiftLeft=movesArray[moveActuallyMade][0];
		int yShiftLeft=movesArray[moveActuallyMade][1];
		int xShiftRight=xShiftLeft;
		int yShiftRight=yShiftLeft;

		int lPlayer_X_Position_New=lPlayer_X_Position+xShiftLeft;
		int lPlayer_Y_Position_New=lPlayer_Y_Position+yShiftLeft;
		int rPlayer_X_Position_New=rPlayer_X_Position+xShiftRight;
		int rPlayer_Y_Position_New=rPlayer_Y_Position+yShiftRight;
		System.out.println("\trightMap "+rPlayer_Y_Position+"-"+rPlayer_X_Position+"="+rightMap[rPlayer_Y_Position][rPlayer_X_Position]);

		if(leftMap[lPlayer_Y_Position_New][lPlayer_X_Position_New]==1) {
			xShiftLeft=0;
			yShiftLeft=0;
		} else {
			lPlayer_X_Position=lPlayer_X_Position_New;
			lPlayer_Y_Position=lPlayer_Y_Position_New;
		}

		if(rightMap[rPlayer_Y_Position_New][rPlayer_X_Position_New]==1) {
			xShiftRight=0;
			yShiftRight=0;
		} else {
			rPlayer_X_Position=rPlayer_X_Position_New;
			rPlayer_Y_Position=rPlayer_Y_Position_New;
		}
		System.out.println("\trightMap "+rPlayer_Y_Position_New+"-"+rPlayer_X_Position_New+"="+rightMap[rPlayer_Y_Position_New][rPlayer_X_Position_New]);
		
		xShiftRightCumulative+=xShiftRight;
		yShiftLeftCumulative+=yShiftLeft;
		xShiftLeftCumulative+=xShiftLeft;
		yShiftRightCumulative+=yShiftRight;
	}


	public void setLeftRightMap (int[][] aintViewL, int[][] aintViewR) {
		leftSightRadius=(aintViewL.length-1)/2;
		rightSightRadius=(aintViewR.length-1)/2;
		System.out.println("leftSightRadius= "+leftSightRadius);
		System.out.println("rightSightRadius= "+rightSightRadius);

		leftMap = new int[maxVirtualMapsize][maxVirtualMapsize];
		rightMap = new int[maxVirtualMapsize][maxVirtualMapsize];
		for(int i=0;i<maxVirtualMapsize;i++)
			for(int j=0;j<maxVirtualMapsize;j++) {
				leftMap[i][j]=4;
				rightMap[i][j]=4;
			}
		// 4 means unseen
		lPlayer_X_Position=(leftMap[0].length-1)/2;
		lPlayer_Y_Position=(leftMap.length-1)/2;
		rPlayer_X_Position=(rightMap[0].length-1)/2;
		rPlayer_Y_Position=(rightMap.length-1)/2;

	}


	public void updateLeftRightMap(int[][] aintViewL, int[][] aintViewR) {
		int deltaX,deltaY;
		deltaX=(leftMap[0].length-1)/2-(aintViewL[0].length-1)/2;
		deltaY=(leftMap.length-1)/2 -(aintViewL.length-1)/2;
		for(int y=0;y<aintViewL.length;y++) {
			for(int x=0;x<aintViewL[0].length;x++)  {
				leftMap[y+deltaY+yShiftLeftCumulative][x+deltaX+xShiftLeftCumulative]=aintViewL[y][x];
				//System.out.print(" "+leftMap[y+deltaY][x+deltaX]);
			}
			//System.out.println("");
		}
		System.out.println("\n\n");
		deltaX=(rightMap[0].length-1)/2-(aintViewR[0].length-1)/2;
		deltaY=(rightMap.length-1)/2 -(aintViewR.length-1)/2;
		for(int y=0;y<aintViewR.length;y++) {
			for(int x=0;x<aintViewR[0].length;x++)  {
				int yIndex=y+deltaY+yShiftRightCumulative;
				int xIndex=x+deltaX+xShiftRightCumulative;
				rightMap[yIndex][xIndex]=aintViewR[y][x];
				System.out.print(" "+aintViewR[y][x]);
			}
			System.out.println("");
		}
		System.out.println("deltaX="+deltaX);
		System.out.println("deltaY="+deltaY);
		System.out.println("xShiftRightCumulative="+xShiftRightCumulative);
		System.out.println("yShiftRightCumulative="+yShiftRightCumulative);
		/*
		for(int y=0;y<leftMap.length;y++) {
			for(int x=0;x<leftMap[0].length;x++)  {
				System.out.print(" "+leftMap[y][x]);
			}
			System.out.println("");
		}
		*/
		System.out.println("\n\n");
		for(int y=0;y<rightMap.length;y++) {
			for(int x=0;x<rightMap[0].length;x++)  {
				System.out.print(" "+rightMap[y][x]);
			}
			System.out.println("");
		}
		System.out.println("maps printed");


	}

	public void setMovesRandom(int[][] aintViewL, int[][] aintViewR ) {
		movesList=new ArrayList<Integer>();
		movesList.add((round%8)+1);
	}

	public void setMoves(int[][] aintViewL, int[][] aintViewR )
	{

		/*lPlayer_X_Position=(leftMap[0].length-1)/2;
		lPlayer_Y_Position=(leftMap.length-1)/2;
		rPlayer_X_Position=(rightMap[0].length-1)/2;
		rPlayer_Y_Position=(rightMap.length-1)/2;
		 */
		/*
		for(int y=0;y<leftMap.length;y++) {
			for(int x=0;x<leftMap[0].length;x++)  {
				if (leftMap[y][x]==2) {
					lPlayer_X_Exit=x;
					lPlayer_Y_Exit=y;
					System.out.println("left exit found  "+x+","+y);
					x=leftMap[0].length;
					y=leftMap.length;
				}
			}
		}
		for(int y=0;y<rightMap.length;y++) {
			for(int x=0;x<rightMap[0].length;x++)  {
				if (rightMap[y][x]==2) {
					rPlayer_X_Exit=x;
					rPlayer_Y_Exit=y;
					System.out.println("right exit found "+x+","+y);
					x=rightMap[0].length;
					y=rightMap.length;
				}
			}
		}*/
		//state=new ;

		state[lPlayer_X_Position][lPlayer_Y_Position][rPlayer_X_Position][rPlayer_Y_Position]=900;
		//stateDistance[lPlayer_X_Position][lPlayer_Y_Position][rPlayer_X_Position][rPlayer_Y_Position]=0;
		browse(lPlayer_X_Position,lPlayer_Y_Position,rPlayer_X_Position,rPlayer_Y_Position,0);

		movesList=new ArrayList<Integer>();
		if (exitReached()) {
			updateMovesList(lPlayer_X_Exit,lPlayer_Y_Exit,rPlayer_X_Exit,rPlayer_Y_Exit);

		} else {

			movesList.add((round%8)+1);
			if (leftExitFound==true && rightExitFound==true) {
				// exits known
				/*
				 * if no 4's to be seen  movelist should allow unsynchronized exit else browse to max 4's
				 */

			} else {
				// exits unknown
				/*
				 * if one of exits is known find the max 4's in unknown
				 * else something else  
				 */


			}

		}


	}

	public void browse(int xx1,int yy1,int xx2,int yy2,int distanceFromStart) {
		int x1,y1,x2,y2,xm1,ym1,xm2,ym2,deltaX,deltaY,leftSame,rightSame,distance;
		distance=distanceFromStart+1;
		// here distance is measured in moves
		//stateDistance
		// these define the positions after move
		ArrayList<String> queueElements=new ArrayList<String>();
		queueElements.add(xx1+","+yy1+","+xx2+","+yy2);
		//ListIterator<String> litr = queueElements.listIterator();
		//while (litr.hasNext()) {
		while(!queueElements.isEmpty()){
			if (exitReached())
				return;
			//stateName=litr.next();
			String stateName=queueElements.remove(0);
			String[] arrayA=stateName.split(",");
			x1=Integer.parseInt(arrayA[0]); y1=Integer.parseInt(arrayA[1]); 
			x2=Integer.parseInt(arrayA[2]); y2=Integer.parseInt(arrayA[3]);
			System.out.println("\t\t\tbrowsing "+x1+","+y1+","+x2+","+x2+"="+distanceFromStart);
			for(int moves=1;moves<=8;moves++) {
				deltaX=movesArray[moves][0];
				deltaY=movesArray[moves][1];
				xm1=x1+deltaX;ym1=y1+deltaY;
				xm2=x2+deltaX;ym2=y2+deltaY;
				leftSame=0;
				rightSame=0;

				// currently assuming that its possible that both exit together
				if(leftMap[ym1][xm1]==2 || rightMap[ym2][xm2]==2) {
					if(leftMap[ym1][xm1]==2 && leftExitFound==false) {
						lPlayer_X_Exit=xm1;lPlayer_Y_Exit=ym1;
						System.out.println("\t\t\t\t\t\tleftExitFound "+xm1+","+ym1);
						leftExitFound=true;
					}
					if(rightMap[ym2][xm2]==2 && rightExitFound==false) {
						rPlayer_X_Exit=xm2;rPlayer_Y_Exit=ym2;
						System.out.println("\t\t\t\t\t\trightExitFound "+xm2+","+ym2);
						rightExitFound=true;
					}
					//if(!(leftMap[ym1][xm1]==2 && rightMap[ym2][xm2]==2)) {
					//continue;
					//}
				}
				if(leftMap[ym1][xm1]==1) {
					xm1=x1;ym1=y1;
					leftSame++;
				}
				if(rightMap[ym2][xm2]==1) {
					xm2=x2;ym2=y2;
					rightSame++;
				}
				if(leftSame==1 && rightSame==1)
					continue;
				//if (stateDistance[xm1][ym1][xm2][ym2]>distance || state[xm1][ym1][xm2][ym2]==0 ) { 
				if (state[xm1][ym1][xm2][ym2]==0 ) { 
					state[xm1][ym1][xm2][ym2]=moves*100+leftSame*10+rightSame;
					//stateDistance[xm1][ym1][xm2][ym2]=distance;
				}
				else 
					continue;
				System.out.println("state "+xm1+","+ym1+","+xm2+","+ym2+"="+state[xm1][ym1][xm2][ym2]);
				if (exitReached())
					return;
				// check if exit has been reached =>break loop empty ArrayList
				//(better set a global variable to tell that the exit has been reached

				stateName=xm1+","+ym1+","+xm2+","+ym2;
				//litr.add(stateName);
				queueElements.add(stateName);
				//enqueue state[xm1][ym1][xm2][ym2]
			}
		}
	}

	public void updateMovesList(int x1,int y1,int x2,int y2 ) {
		int moveState = state[x1][y1][x2][y2];
		int moves;
		int xm1=x1,ym1=y1,xm2=x2,ym2=y2,deltaX,deltaY;
		if (moveState!=900) {
			moves=moveState/100;
			movesList.add(0,moves );
			deltaX=movesArray[moves][0];
			deltaY=movesArray[moves][1];
			int leftBit=(moveState%100)/10;
			int rightBit=moveState%10;
			if (leftBit==0) {
				xm1=x1-deltaX;
				ym1=y1-deltaY;
			}
			if (rightBit==0) {
				xm2=x2-deltaX;
				ym2=y2-deltaY;
			}

			updateMovesList(xm1,ym1,xm2,ym2);
		} 
	}

	public boolean exitReached() {
		if(leftExitFound==true && rightExitFound==true)
			if (state[lPlayer_X_Exit][lPlayer_Y_Exit][rPlayer_X_Exit][rPlayer_Y_Exit]!=0)
				return true;
		return false;
	}


}


