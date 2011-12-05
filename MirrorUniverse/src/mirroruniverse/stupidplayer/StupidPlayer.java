package mirroruniverse.stupidplayer;

import java.util.*;

import mirroruniverse.sim.MUMap;
import mirroruniverse.sim.Player;

public class StupidPlayer implements Player
{
	boolean debug =false;
	int round=0;
	LowerBoundDetecter lb = new LowerBoundDetecter();
	int justExplore=0;
	int maxJustExplore=20;
	Date start;
	Date lastStep;
	private static final long TIMELIMIT = 250 * 1000; //250s
	private int lastTimeUse;
	/*
	 * initially moveList is empty --
	 * the player selects the best place to move and stores the moves that would take it 
	 * to that best place inside movesList
	 * now the player moves as per this moves list till it empties 
	 * ( but  only if it does not discover any unknown exits)
	 *   
	 */
	ArrayList<Integer> movesList;

	/*
	 * Single moves list works in the same way as the movesList ;
	 *  buts comes into play 
	 * 				only when it knows that exits are not simultaneously reachable 
	 * 			and 
	 * 				no more area is left to explore
	 */
	ArrayList<Integer> singleMovesList;

	/*
	 * minSingleMovesList stores the moves which promise least delay in exits
	 */
	ArrayList<Integer> minSingleMovesList;


	boolean leftExitFound=false, rightExitFound=false;

	/*
	 * when somewhere we can see the exit
	 * ( 2 has been passed in the array int[][] aintViewL, int[][] aintViewR)
	 * we set them to true
	 */
	boolean leftExitPassed=false, rightExitPassed=false;

	// trivial: for debugging
	boolean leftExitPassedFlag=false, rightExitPassedFlag=false;

	/*
	 * This is the maximum sidelength of the map on which it will work
	 * it has to be hrdcoded to 100 as per the problem statement
	 */
	int maxMapSideLength=100;

	/*
	 * this is the map size which we will consider  
	 */
	int maxVirtualMapsize=maxMapSideLength*2+4;

	/*
	 * the following array are the arrays which will be used ;
	 * considering all the moves made by the player
	 */
	int[][] leftMap;
	int[][] rightMap;
	// maybe we can later switch over to byte array
	// but it will only improve the situation in limited manner

	/*
	 * these are calculated by the size of the array int[][] aintViewL, int[][] aintViewR
	 */
	int leftSightRadius;
	int rightSightRadius;


	/*
	 * these variables record the shift of the new array which we will be getting and update them 
	 * with every move to have the correct player position
	 */
	int xShiftLeftCumulative=0, yShiftLeftCumulative=0;
	int xShiftRightCumulative=0,yShiftRightCumulative=0;



	/*
	 *  once the exit positions are known and reachable simultaneously / not together
	 */
	int lPlayer_X_Exit, lPlayer_Y_Exit;
	int rPlayer_X_Exit, rPlayer_Y_Exit;

	/*
	 * current position of the player as per the 200X200 map
	 */
	int lPlayer_X_Position, lPlayer_Y_Position;
	int rPlayer_X_Position, rPlayer_Y_Position;

	// just to pop out the next element from the moves list
	int lastMove=0;

	// records if any of the player has actually exited
	boolean leftExited=false;
	boolean rightExited=false;


	//public int[][][][] state1= new int[maxVirtualMapsize][maxVirtualMapsize][maxVirtualMapsize][maxVirtualMapsize];
	//int[][] miniState;
	/*
	 * State signifies a set of reachable set of position pair reachable
	 *  with it we can have following things associated : 
	 *     - last move that will bring both the players to that state
	 *     - number of new unknown grids that will be seen once players are there (left, right & total)
	 *     - distance from the current location ( distance means moves required to reach there)
	 */
	Map <String,Integer> state ;

	/*
	 * ministate is same as state but will be used in cases when one of the players has to exit and only 
	 * one players state will be calculated
	 */
	Map <String,Integer> miniState ;
	// ministate to be used in case of one of the players have exitted

	/*
	 * These states at every decision fork calculate which state is best for left, right and both the players
	 *     on the basis of new grids which will be seen once that state is reached
	 *  bestDiffExitState - stores the state where one has exited and the least delay state is possible   
	 */
	String stateBestLeft,stateBestRight,stateBestBoth,bestDiffExitState;

	ArrayList<Integer> bestMovesOrder ;


	public static final int[][] movesArray = { { 0, 0 }, { 1, 0 }, { 1, -1 }, { 0, -1 }, { -1, -1 },  { -1, 0 }, { -1, 1 }, { 0, 1 }, { 1, 1 } };




	public int lookAndMove( int[][] aintViewL, int[][] aintViewR )
	{
		round++;
		//if(round>300 )
		//debug=false;
		//System.out.println(java.lang.Runtime.getRuntime().maxMemory()); 
		if( round ==1) {
			singleMovesList=new ArrayList<Integer>();
			minSingleMovesList=new ArrayList<Integer>();
			movesList=new ArrayList<Integer>();
			state = new HashMap<String,Integer>();
			setLeftRightMap(aintViewL,aintViewR);
			start = new Date();
			lastStep = new Date();
		}
		
		
		System.out.println(":::now it's "+new Date());

		// will map the new info in our maps
		updateLeftRightMap(aintViewL,aintViewR);

		// if the exit is seen for the first time we should recalculate our exploration
		if (rightExitPassed==true && rightExitPassedFlag==false) {
			movesList.clear();
			rightExitPassedFlag=true;
		}
		if (leftExitPassed==true && leftExitPassedFlag==false) {
			movesList.clear();
			leftExitPassedFlag=true;
		}



		if (movesList.isEmpty()) {
			//state = new HashMap<String,Integer>();

			//state = new int[maxVirtualMapsize][maxVirtualMapsize][maxVirtualMapsize][maxVirtualMapsize];
			setMoves(aintViewL,aintViewR);
		}
		//setMovesRandom(aintViewL,aintViewR);
		if(movesList.isEmpty()) { 
			//	printMaps();
		}

		lastMove=movesList.remove(0);
		if (debug) {
			System.out.println("\t\t\t*****round "+round+"lastMove "+lastMove+"  movesList="+movesList);
			System.out.println("lastMove "+lastMove);
			System.out.println("movesList "+movesList);
			//			if(round>100)
			//			printMaps();
		}
		updateCurrentPosition(lastMove);
		//	if(round >149)
		//	printMaps();
		lastTimeUse = (int) (new Date().getTime() - lastStep.getTime());
		return lastMove;
	}


	/*
	 * adjusts the position of the player as per every move 
	 * and sets leftExited and  rightExited variables which record if any player has actually exited 
	 */
	public void updateCurrentPosition(int moveActuallyMade) {

		int xShiftLeft=movesArray[moveActuallyMade][0];
		int yShiftLeft=movesArray[moveActuallyMade][1];
		int xShiftRight=xShiftLeft;
		int yShiftRight=yShiftLeft;

		int lPlayer_X_Position_New=lPlayer_X_Position+xShiftLeft;
		int lPlayer_Y_Position_New=lPlayer_Y_Position+yShiftLeft;
		int rPlayer_X_Position_New=rPlayer_X_Position+xShiftRight;
		int rPlayer_Y_Position_New=rPlayer_Y_Position+yShiftRight;
		if (debug) 
			System.out.println("\trightMap "+rPlayer_Y_Position+"-"+rPlayer_X_Position+"="+rightMap[rPlayer_Y_Position][rPlayer_X_Position]);

		if( leftExited ||leftMap[lPlayer_Y_Position_New][lPlayer_X_Position_New]==1 ) {
			xShiftLeft=0;
			yShiftLeft=0;
		} else {
			lPlayer_X_Position=lPlayer_X_Position_New;
			lPlayer_Y_Position=lPlayer_Y_Position_New;
		}
		//System.out.println("rPlayer_Y_Position_New="+rPlayer_Y_Position_New+" rPlayer_X_Position_New="+rPlayer_X_Position_New);
		if(rightExited || rightMap[rPlayer_Y_Position_New][rPlayer_X_Position_New]==1 ){
			xShiftRight=0;
			yShiftRight=0;
		} else {
			rPlayer_X_Position=rPlayer_X_Position_New;
			rPlayer_Y_Position=rPlayer_Y_Position_New;
		}
		if(leftMap[lPlayer_Y_Position_New][lPlayer_X_Position_New]==2)  {
			if (debug) 
				System.out.println("Exitting in left map in round "+round+" at "+lPlayer_X_Position_New+","+lPlayer_Y_Position_New);
			leftExited=true;
		}
		if(rightMap[rPlayer_Y_Position_New][rPlayer_X_Position_New]==2) {
			if (debug) 
				System.out.println("Exitting in right map in round "+round+" at "+rPlayer_X_Position_New+","+rPlayer_Y_Position_New);
			rightExited=true;
		}
		xShiftRightCumulative+=xShiftRight;
		yShiftLeftCumulative+=yShiftLeft;
		xShiftLeftCumulative+=xShiftLeft;
		yShiftRightCumulative+=yShiftRight;
	}


	/*
	 * This function initializes the 
	 *   - leftMap and rightMap (initial values being set as 4)
	 *   - the players initial position
	 */
	public void setLeftRightMap (int[][] aintViewL, int[][] aintViewR) {
		leftSightRadius=(aintViewL.length-1)/2;
		rightSightRadius=(aintViewR.length-1)/2;
		//System.out.println("leftSightRadius= "+leftSightRadius);
		//System.out.println("rightSightRadius= "+rightSightRadius);

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


	/*
	 * Will update the left and rightMap with every new int[][] aintViewL, int[][] aintViewR
	 *  also sets leftExitPassed and rightExitPassed
	 */
	public void updateLeftRightMap(int[][] aintViewL, int[][] aintViewR) {
		int deltaX,deltaY;
		deltaX=(leftMap[0].length-1)/2-(aintViewL[0].length-1)/2;
		deltaY=(leftMap.length-1)/2 -(aintViewL.length-1)/2;
		for(int y=0;y<aintViewL.length;y++) {
			for(int x=0;x<aintViewL[0].length;x++)  {
				int yIndex=y+deltaY+yShiftLeftCumulative;
				int xIndex=x+deltaX+xShiftLeftCumulative;
				if(yIndex<0 ||yIndex>=maxVirtualMapsize || xIndex<0 ||xIndex>=maxVirtualMapsize) 
					continue;
				if (aintViewL[y][x]==2) {
					if(leftExitPassed==false) {
						//if(debug)
						System.out.println("\t\t\t\t\t\tleftExitPassed "+xIndex+","+yIndex);
						leftExitPassed=true;
						lPlayer_X_Exit=xIndex;lPlayer_Y_Exit=yIndex; 
					}
				}
				leftMap[yIndex][xIndex]=aintViewL[y][x];
				//System.out.print(" "+leftMap[y+deltaY][x+deltaX]);
			}
			//System.out.println("");
		}
		//System.out.println("\n\n");
		deltaX=(rightMap[0].length-1)/2-(aintViewR[0].length-1)/2;
		deltaY=(rightMap.length-1)/2 -(aintViewR.length-1)/2;
		for(int y=0;y<aintViewR.length;y++) {
			for(int x=0;x<aintViewR[0].length;x++)  {
				int yIndex=y+deltaY+yShiftRightCumulative;
				int xIndex=x+deltaX+xShiftRightCumulative;
				if(yIndex<0 ||yIndex>=maxVirtualMapsize || xIndex<0 ||xIndex>=maxVirtualMapsize) 
					continue;
				if (aintViewR[y][x]==2) {
					if(rightExitPassed==false) {
						//if(debug)
						System.out.println("\t\t\t\t\t\trightExitPassed "+xIndex+","+yIndex);
						rightExitPassed=true;
						rPlayer_X_Exit=xIndex;rPlayer_Y_Exit=yIndex; 
					}
				}
				rightMap[yIndex][xIndex]=aintViewR[y][x];
				//System.out.print(" "+aintViewR[y][x]);
			}
			//System.out.println("");
		}
		if (debug) { 

			System.out.println("deltaX="+deltaX);
			System.out.println("deltaY="+deltaY);
			System.out.println("xShiftRightCumulative="+xShiftRightCumulative);
			System.out.println("yShiftRightCumulative="+yShiftRightCumulative);
		}
		//if(debug)
		//printMaps();


	}

	// redundant code
	public void setMovesRandom(int[][] aintViewL, int[][] aintViewR ) {
		movesList=new ArrayList<Integer>();
		movesList.add((round%8)+1);
	}

	/*
	 * This function is called when the moveslist is empty 
	 * It ->
	 *    checks if we can exit simultaneously if we can then it does
	 *    if both the exits are known and map is not fully explored then it explores it
	 *    if map is fully explored then it tries to find path with minimum delay
	 *    if one of the exits is known then it biases the search to the other map
	 */
	public void setMoves(int[][] aintViewL, int[][] aintViewR )
	{
		//state[lPlayer_X_Position][lPlayer_Y_Position][rPlayer_X_Position][rPlayer_Y_Position]=900;
		int lowerBound = lb.getLowerBound(leftMap, rightMap, lPlayer_Y_Exit,lPlayer_X_Exit, rPlayer_Y_Exit,rPlayer_X_Exit);
		if (leftExited) {
			singleBrowse("right",rPlayer_X_Position,rPlayer_Y_Position);
			movesList=new ArrayList<Integer>();
			movesList.addAll(minSingleMovesList);
			return;
		}
		if (rightExited) {
			singleBrowse("left",lPlayer_X_Position,lPlayer_Y_Position);
			movesList=new ArrayList<Integer>();
			movesList.addAll(minSingleMovesList);
			return;
		}
		state.clear();
		state = new HashMap<String,Integer>();

		String stateKey=lPlayer_X_Position+","+lPlayer_Y_Position+","+rPlayer_X_Position+","+rPlayer_Y_Position;
		state.put(stateKey, 900);
		//stateDistance[lPlayer_X_Position][lPlayer_Y_Position][rPlayer_X_Position][rPlayer_Y_Position]=0;
		double maximumTotalUnknown=browse(lPlayer_X_Position,lPlayer_Y_Position,rPlayer_X_Position,rPlayer_Y_Position,false);
		if(debug)
			System.out.println("maximumTotalUnknown  ="+maximumTotalUnknown);

		movesList=new ArrayList<Integer>();
		boolean movesListSet=false;
		if (exitReached()) {
			updateMovesList(lPlayer_X_Exit,lPlayer_Y_Exit,rPlayer_X_Exit,rPlayer_Y_Exit);
			if(debug) {
				System.out.println("next state chosen  as exit");
				System.out.println("movesList"+movesList);	
				System.out.println("exit location "+lPlayer_X_Exit+","+lPlayer_Y_Exit+"---"+rPlayer_X_Exit+","+rPlayer_Y_Exit);	
				printMaps();
			}
			movesListSet=true;

		} else {
			String nextState="";
			if (leftExitFound==true && rightExitFound==true) {
				// exits known but path not known
				// go towards max total
				//System.out.println(lowerBond + "lower bound =================");
				if (maximumTotalUnknown>0.0 && new Date().getTime() - start.getTime() <= TIMELIMIT-lastTimeUse) {
					if(justExplore==0) {
						justExplore=maxJustExplore;
					} else {
						justExplore--;
					}
					if(debug) System.out.println("next state chosen stateBestBoth"+nextState);
					nextState=stateBestBoth;
				} else  {
					//				state = new int[maxVirtualMapsize][maxVirtualMapsize][maxVirtualMapsize][maxVirtualMapsize];
					state = new HashMap<String,Integer>();
					freeMemory();
					stateKey=lPlayer_X_Position+","+lPlayer_Y_Position+","+rPlayer_X_Position+","+rPlayer_Y_Position;
					state.put(stateKey, 900);

					maximumTotalUnknown=browse(lPlayer_X_Position,lPlayer_Y_Position,rPlayer_X_Position,rPlayer_Y_Position,true);
					String[] arrayA=bestDiffExitState.split(",");
					int x1=Integer.parseInt(arrayA[0]); int y1=Integer.parseInt(arrayA[1]); 
					int x2=Integer.parseInt(arrayA[2]); int y2=Integer.parseInt(arrayA[3]);
					updateMovesList(x1,y1,x2,y2);
					movesList.addAll(minSingleMovesList);
					if(debug) {

						System.out.println("bestDiffExitState  ="+bestDiffExitState);
						System.out.println("movesList  ="+movesList);
						System.out.println("minSingleMovesList  ="+minSingleMovesList);
						System.out.println("movesList  ="+movesList);
						System.out.println("maximumTotalUnknown 2 ="+maximumTotalUnknown);
					}
					if(debug) System.out.println("next state chosen bestDiffExitState"+bestDiffExitState);

					movesListSet=true;
				}


			} else {
				// atleast one of the exits unknown
				if (leftExitFound==true) {
					nextState=stateBestRight;
					String[] arrayA=nextState.split(",");
					int unknowns=Integer.parseInt(arrayA[6]);
					if (unknowns<=0) {
						nextState=stateBestBoth;
						arrayA=nextState.split(",");
						unknowns=Integer.parseInt(arrayA[7]);
					}

					if (unknowns<=0) {
						int steps =countStepsToExit("left",lPlayer_X_Position,lPlayer_Y_Position);
						movesList.addAll(singleMovesList);
						movesListSet=true;
						if(debug)
							System.out.println("next state was chosen stateBestRight "+stateBestRight+" but overridden by leftAloneExit  as the right player cant see anything new without left player stepping on the leftExit == movesList "+movesList+"  steps="+steps);
					}else  {
						if(debug) 
							System.out.println("next state chosen stateBestRight"+nextState);
					}

					// go towards max right
				} else {
					if (rightExitFound==true) {
						// go towards max left
						nextState=stateBestLeft;
						//System.out.println("rightExitFound but not left nextState=stateBestLeft="+stateBestLeft);
						String[] arrayA=nextState.split(",");
						int unknowns=Integer.parseInt(arrayA[5]);
						if (unknowns<=0) {
							nextState=stateBestBoth;
							arrayA=nextState.split(",");
							unknowns=Integer.parseInt(arrayA[7]);
						}

						if (unknowns<=0) {
							countStepsToExit("right",rPlayer_X_Position,rPlayer_Y_Position);
							movesList.addAll(singleMovesList);
							movesListSet=true;
							if(debug) System.out.println("next state was chosen stateBestLeft "+stateBestLeft+" but overridden by rightAloneExit  as the left player cant see anything new without right player stepping on the right ");
						}else  {
							if(debug) System.out.println("next state chosen stateBestLeft"+nextState);
						}
					} else {
						nextState=stateBestBoth;
						String[] arrayA=nextState.split(",");
						if (Integer.parseInt(arrayA[7])<=0) {
							System.out.println(" This line should never be printed - It means that exits are not reachable");
						}else  {
							if(debug) System.out.println("next state chosen stateBestBoth"+stateBestBoth);
						}
						// go towards max total
					}
				}
			}
			if(!movesListSet) {
				String[] arrayA=nextState.split(",");
				int x1=Integer.parseInt(arrayA[0]); int y1=Integer.parseInt(arrayA[1]); 
				int x2=Integer.parseInt(arrayA[2]); int y2=Integer.parseInt(arrayA[3]);
				int distanceToBestState=Integer.parseInt(arrayA[4]);
				if(debug) System.out.println("next state chosen "+nextState+" with distance "+distanceToBestState);
				updateMovesList(x1,y1,x2,y2);
			}

		}
		
		lastStep = new Date();
	}


	/*
	 * This is the important function which takes in the current position and tries to browse
	 * through all possible moves
	 * It also takes a boolean as an input (exitSeperately) 
	 * 		which tells it if it should ignore exit cases with delay >0 or not
	 * It returns maxTotalScore(total reachable unknown states)
	 * 			 which is 0 if nothing is left to explore
	 */
	public double browse(int xx1,int yy1,int xx2,int yy2,boolean exitSeperately) {
		int x1,y1,x2,y2,xm1,ym1,xm2,ym2,deltaX,deltaY,leftSame,rightSame,distance;
		distance=0;
		// here distance is measured in moves
		String stateName=xx1+","+yy1+","+xx2+","+yy2+","+distance+",0,0,0",stateKey;
		stateBestLeft=stateName;stateBestRight=stateName;stateBestBoth=stateName;
		double maxLeftScore=0,maxRightScore=0,maxTotalScore=0;
		int leftNext,rightNext,singleStepsRequired=Integer.MAX_VALUE,minSingleStepsRequired=Integer.MAX_VALUE;
		ArrayList<String> queueElements=new ArrayList<String>();
		queueElements.add(xx1+","+yy1+","+xx2+","+yy2+","+distance+",0,0,0");
		int oldDistance =distance;
		while(!queueElements.isEmpty()){
			if (exitReached())
				return maxTotalScore;
			stateName=queueElements.remove(0);
			String[] arrayA=stateName.split(",");
			x1=Integer.parseInt(arrayA[0]); y1=Integer.parseInt(arrayA[1]); x2=Integer.parseInt(arrayA[2]); y2=Integer.parseInt(arrayA[3]);
			distance=Integer.parseInt(arrayA[4]);
			//
			if(distance>oldDistance ) {
				if( justExplore>0) { if(maxTotalScore>0) return maxTotalScore;}
				if( leftExitFound==false && rightExitFound==false) { if(maxTotalScore>0) return maxTotalScore;}
				if( leftExitFound==true  && rightExitFound==false)  { if(maxRightScore>0) return maxTotalScore;}
				if( leftExitFound==false && rightExitFound==true)  { if(maxLeftScore>0) return maxTotalScore; }

				oldDistance++; 
			}
			distance++;
			// System.out.println("\t\t\tbrowsing "+x1+","+y1+","+x2+","+y2+"="+distance);
			// browsing through the possible moves
			setBestMovesOrder( x1, y1, x2, y2);
			//	System.out.println("\n\t\n bestMovesOrder"+bestMovesOrder);
			for(int moves : bestMovesOrder) {
				deltaX=movesArray[moves][0]; deltaY=movesArray[moves][1];
				xm1=x1+deltaX;ym1=y1+deltaY; xm2=x2+deltaX;ym2=y2+deltaY;
				leftSame=0; rightSame=0;
				if(ym1<0 ||ym1>=maxVirtualMapsize || xm1<0 ||xm1>=maxVirtualMapsize)  	leftNext=1 ;  else  leftNext=leftMap[ym1][xm1];
				if(ym2<0 ||ym2>=maxVirtualMapsize || xm2<0 ||xm2>=maxVirtualMapsize)    rightNext=1;  else  rightNext=rightMap[ym2][xm2];
				stateKey=xm1+","+ym1+","+xm2+","+ym2;
				//leftNext=leftMap[ym1][xm1];
				//rightNext=rightMap[ym2][xm2];
				//System.out.println(" stateKey guests  "+stateKey+" leftNext="+leftNext+" rightNext="+rightNext);

				if(leftNext==2 && leftExitFound==false) { leftExitFound=true; System.out.println("\t\t\t\t\t\tleftExitFound "+xm1+","+ym1);}
				if(rightNext==2 && rightExitFound==false) {rightExitFound=true;System.out.println("\t\t\t\t\t\trightExitFound "+xm2+","+ym2);}

				if(leftNext==1 ) { xm1=x1;ym1=y1;leftSame++;}
				if(rightNext==1 )  {xm2=x2;ym2=y2;rightSame++;}

				if(rightNext==4 || leftNext==4) { continue;}


				if(leftSame==1 && rightSame==1) continue;
				stateKey=xm1+","+ym1+","+xm2+","+ym2;
				// System.out.println(" stateKey knocking  "+stateKey);

				if (!state.containsKey(stateKey) ) { 
					if((rightNext==2 &&leftNext!=2) || (rightNext!=2 &&leftNext==2)) { 
						if (!exitSeperately) continue;
						// if the control crosses this line then it means that both the exits ae known and 0 delay solution is not possible
						if(rightNext==2) {
							singleStepsRequired=countStepsToExit("left",xm1,ym1);
							if (debug) { System.out.println("# right at exit "+xm2+","+ym2); System.out.println("# left at  "+xm1+","+ym1);System.out.println("# singleStepsRequired   "+singleStepsRequired);}
						}
						if(leftNext==2) {
							singleStepsRequired=countStepsToExit("right",xm2,ym2);
							if(debug) { System.out.println("# left at exit "+xm1+","+ym1); System.out.println("# right at  "+xm2+","+ym2);System.out.println("# singleStepsRequired   "+singleStepsRequired);}
						}
						if(singleStepsRequired<minSingleStepsRequired) {
							minSingleStepsRequired=singleStepsRequired; minSingleMovesList.clear(); minSingleMovesList.addAll(singleMovesList);
							bestDiffExitState=xm1+","+ym1+","+xm2+","+ym2+","+distance;
							if (debug) System.out.println("singleStepsRequired = "+singleStepsRequired+"  singleMovesList = "+singleMovesList);
						}
						if (debug) { System.out.println("currentState="+xm1+","+ym1+","+xm2+","+ym2+","+distance); System.out.println("singleStepsRequired="+singleStepsRequired); System.out.println("minSingleStepsRequired="+minSingleStepsRequired); System.out.println("bestDiffExitState="+bestDiffExitState);}
					}
					stateKey=xm1+","+ym1+","+xm2+","+ym2; state.put(stateKey, moves*100+leftSame*10+rightSame);
					// System.out.println(" stateKey put "+stateKey);
					//state[xm1][ym1][xm2][ym2]=moves*100+leftSame*10+rightSame;
					int leftUknown=unknownCount(xm1,ym1,"left");
					int rightUknown=unknownCount(xm2,ym2,"right");
					int totalUnknown=leftUknown+rightUknown;

					stateName=xm1+","+ym1+","+xm2+","+ym2+","+distance+","+leftUknown+","+rightUknown+","+totalUnknown;
					if((10000.0*leftUknown/distance )>maxLeftScore)    { maxLeftScore=10000.0*leftUknown/distance  ; stateBestLeft=stateName;}
					if((10000.0*rightUknown/distance )>maxRightScore)  { maxRightScore=10000.0*rightUknown/distance; stateBestRight=stateName;}
					if((10000.0*totalUnknown/distance )>maxTotalScore) { maxTotalScore=10000.0*totalUnknown/distance;stateBestBoth=stateName;}
				} else  {
					continue;
				}

				if(debug) { 
					stateKey=xm1+","+ym1+","+xm2+","+ym2; System.out.println("state "+xm1+","+ym1+","+xm2+","+ym2+"="+state.get(stateKey)+" name="+stateName+" distance ="+distance+" moves="+moves);
				}
				if (exitReached())
					return maxTotalScore;
				queueElements.add(stateName);
				/// queueing the new state to be explored to implement BFS like search 
			}
		}
		return maxTotalScore;
	}

	/*
	 * This is the  function which will be invoked if a player has exited and the sibling has not yet 
	 * found the exit; do it will returns the moves to have the sibling exit
	 * 
	 */
	public double singleBrowse(String side,int xx1,int yy1) {
		int x1,y1,xm1,ym1,deltaX,deltaY,distance=0,destX=xx1,destY=yy1;
		// here distance is measured in moves
		String stateName=xx1+","+yy1+","+distance+",0,0";
		//System.out.println("singleBrowse called side"+side);

		boolean destinationReached=false;
		double maxScore=0;
		int sideNext;
		miniState = new HashMap<String,Integer>();
		String stateKey=xx1+","+yy1;
		miniState.put(stateKey, 0);
		ArrayList<String> miniQueueElements=new ArrayList<String>();
		miniQueueElements.add(stateName);
		int oldDistance =distance;
		while(!miniQueueElements.isEmpty()){
			if (destinationReached)
				break;
			stateName=miniQueueElements.remove(0);
			String[] arrayA=stateName.split(",");

			x1=Integer.parseInt(arrayA[0]); y1=Integer.parseInt(arrayA[1]);
			distance=Integer.parseInt(arrayA[2]);
			//
			boolean exitSighted=side.equals("left")?leftExitPassed:rightExitPassed;
			if(distance>oldDistance && !exitSighted) {
				if(maxScore>0) break; 
				oldDistance++; 
			}
			distance++;
			// browsing through the possible moves
			for(int moves =1;moves<=8;moves++) {
				deltaX=movesArray[moves][0]; deltaY=movesArray[moves][1];
				xm1=x1+deltaX;ym1=y1+deltaY;
				stateKey=xm1+","+ym1;
				if(y1<0 ||y1>=maxVirtualMapsize || x1<0 ||x1>=maxVirtualMapsize)  {
					sideNext=1 ;
				}else {
					sideNext=side.equals("left")?leftMap[ym1][xm1]:rightMap[ym1][xm1];
				}

				if (sideNext==1 || sideNext==4 ) 	continue;

				stateName=xm1+","+ym1+","+distance;
				if(sideNext==2) {
					//System.out.println("\t\t\t\t\t\t lonely "+side+"ExitFound "+xm1+","+ym1);
					miniState.put(stateKey, moves);
					destX=xm1;destY=ym1;
					destinationReached=true;
					maxScore=distance;
					break;
				}
				if (!miniState.containsKey(stateKey) ) { 
					miniState.put(stateKey, moves);
					miniQueueElements.add(stateName);
					int sideUnknown=unknownCount(xm1,ym1,side);
					stateName=xm1+","+ym1+","+distance+","+sideUnknown;
					if((10000.0*sideUnknown/distance )>maxScore)    { maxScore=10000.0*sideUnknown/distance  ;destX=xm1;destY=ym1;}
				}
			}
		}
		singleMovesList=new ArrayList<Integer>();
		stateKey=xx1+","+yy1;
		miniState.put(stateKey, 0);
		updateSingleMovesList(destX,destY);
		minSingleMovesList.clear(); minSingleMovesList.addAll(singleMovesList);
		//System.out.println("minSingleMovesList="+minSingleMovesList);
		return maxScore;
	}

	public void setBestMovesOrder(int x1,int y1,int x2,int y2) {
		bestMovesOrder=new ArrayList<Integer>();
		ArrayList<Integer> movesOrderBeg=new ArrayList<Integer>();
		ArrayList<Integer> movesOrderMid=new ArrayList<Integer>();
		ArrayList<Integer> movesOrderEnd=new ArrayList<Integer>();
		int deltaX,deltaY,xm1,ym1,xm2,ym2,leftSame,rightSame,leftNext,rightNext;
		for(int moves=1;moves<=8;moves++) {
			deltaX=movesArray[moves][0]; deltaY=movesArray[moves][1];
			xm1=x1+deltaX;ym1=y1+deltaY; xm2=x2+deltaX;ym2=y2+deltaY;
			leftSame=0; rightSame=0;
			if(ym1<0 ||ym1>=maxVirtualMapsize || xm1<0 ||xm1>=maxVirtualMapsize)  
				leftNext=1 ; 
			else 
				leftNext=leftMap[ym1][xm1];
			if(ym2<0 ||ym2>=maxVirtualMapsize || xm2<0 ||xm2>=maxVirtualMapsize)  
				rightNext=1;  
			else
				rightNext=rightMap[ym2][xm2];

			if(leftNext==1 || leftNext==4) { leftSame++;}
			if(rightNext==1 || rightNext==4)  {rightSame++;}
			if(leftSame==1 && rightSame==1) continue;

			if ((leftExitFound==true && rightExitFound==true) || (leftExitFound==false && rightExitFound==false) ) {
				if(leftSame==0 && rightSame==0)
					movesOrderBeg.add( moves);
				else
					movesOrderEnd.add(moves);
			}
			if (leftExitFound==true && rightExitFound==false) {
				if(leftSame==0 && rightSame==0)
					movesOrderBeg.add( moves);
				if(leftSame==1 && rightSame==0)
					movesOrderMid.add( moves);
				if(leftSame==0 && rightSame==1)
					movesOrderEnd.add( moves);				

			}
			if (leftExitFound==false && rightExitFound==true) {
				if(leftSame==0 && rightSame==0)
					movesOrderBeg.add( moves);
				if(leftSame==0 && rightSame==1)
					movesOrderMid.add( moves);
				if(leftSame==1 && rightSame==0)
					movesOrderEnd.add( moves);	
			}

		}
		bestMovesOrder.addAll(movesOrderBeg);
		bestMovesOrder.addAll(movesOrderMid);
		bestMovesOrder.addAll(movesOrderEnd);
	}
	/*
	 * It counts the number of steps required by the remaining player when its sibling has exited
	 * also updates the moves (singleMovesList) that will take it to the exit
	 */
	public int countStepsToExit(String side, int xPos, int yPos) {
		int distance=0;
		// here distance is measured in moves
		int destX,destY,x1,y1,deltaX,deltaY,xm1,ym1,sideNext,finalDistance=Integer.MAX_VALUE;
		boolean destinationReached=false;

		if (side=="left") { destX=lPlayer_X_Exit; destY=lPlayer_Y_Exit; 
		} else { destX=rPlayer_X_Exit; destY=rPlayer_Y_Exit; }

		//System.out.println("countStepsToExit side="+side+" xPos="+xPos+" yPos="+yPos+"  destX,destY="+destX+","+destY);

		String stateName=xPos+","+yPos+","+distance;
		ArrayList<String> miniQueueElements=new ArrayList<String>();
		miniQueueElements.add(stateName);
		//		miniState = new int[maxVirtualMapsize][maxVirtualMapsize];
		miniState = new HashMap<String,Integer>();
		String stateKey=xPos+","+yPos;
		miniState.put(stateKey, 0);

		while(!miniQueueElements.isEmpty()){
			if (destinationReached)
				break;
			stateName=miniQueueElements.remove(0);
			String[] arrayA=stateName.split(",");
			x1=Integer.parseInt(arrayA[0]); y1=Integer.parseInt(arrayA[1]); 
			distance=Integer.parseInt(arrayA[2]);
			distance++;

			for(int moves=1;moves<=8;moves++) {
				deltaX=movesArray[moves][0]; deltaY=movesArray[moves][1];
				xm1=x1+deltaX;ym1=y1+deltaY;

				if(ym1<0 ||ym1>=maxVirtualMapsize || xm1<0 ||xm1>=maxVirtualMapsize) sideNext=1;
				else  
					sideNext=side.equals("left")?leftMap[ym1][xm1]:rightMap[ym1][xm1];

					if (sideNext==1 ) 	continue;

					stateKey=xm1+","+ym1;
					if(sideNext==2) {
						miniState.put(stateKey, moves);
						//					miniState[xm1][ym1]=moves;
						if (debug) { System.out.println("here singleExit sideNext  "+sideNext); System.out.println("miniState value "+miniState.get(stateKey)); System.out.println("xm1 "+xm1+"   ym1 "+ym1); System.out.println("destX "+destX+"   destY "+destY);System.out.println("distance "+distance);}
						destinationReached=true;
						finalDistance=distance;
						break;
					}

					//if (stateDistance[xm1][ym1][xm2][ym2]>distance || state[xm1][ym1][xm2][ym2]==0 ) {
					//stateKey=xm1+","+ym1;
					if (!miniState.containsKey(stateKey) ) { 
						miniState.put(stateKey, moves);
						//if (debug) {System.out.println(" 1 miniState value "+miniState.get(stateKey)); System.out.println(" 1 miniState stateName "+stateName); System.out.println(" 1 xm1 "+xm1+"   ym1 "+ym1); }
						stateName=xm1+","+ym1+","+distance;
						miniQueueElements.add(stateName);
					} else continue;
			}
		}
		singleMovesList=new ArrayList<Integer>();
		stateKey=xPos+","+yPos;

		miniState.put(stateKey, 0);
		updateSingleMovesList(destX,destY);
		return finalDistance;

	}

	/*
	 * will update singleMovesList
	 */
	public void updateSingleMovesList(int x1,int y1) {
		String  stateKey=x1+","+y1;
		//System.out.println("input to updateSingleMovesList "+x1+","+y1);
		int moveState = miniState.get(stateKey);
		//System.out.println(" input move state begin"+moveState);
		int moves;
		int xm1=x1,ym1=y1,deltaX,deltaY;
		while (moveState!=0) {
			moves=moveState;
			//System.out.println(" pos begin  "+xm1+","+ym1);
			//System.out.println(" move state begin"+moves);
			singleMovesList.add(0,moves );
			deltaX=movesArray[moves][0];
			deltaY=movesArray[moves][1];
			xm1=xm1-deltaX;
			ym1=ym1-deltaY;
			stateKey=xm1+","+ym1;
			//System.out.println(" \tprevious stateKey "+stateKey+","+miniState.get(stateKey));
			moveState = miniState.get(stateKey);
			//System.out.println(" \t\t\t\t singleMovesList "+singleMovesList);
		} 
	}

	public int unknownCount(int xu, int yu, String side) {
		int unknownGrids=0;
		//		int sightRadius = side.equals("right")?rightSightRadius:leftSightRadius;
		int sightRadius = 1;
		for(int y=yu-sightRadius;y<=yu+sightRadius;y++) {
			for(int x=xu-sightRadius;x<=xu+sightRadius;x++)  {
				if(y<0 ||y>=maxVirtualMapsize || x<0 ||x>=maxVirtualMapsize)
					continue;
				if (side.equals("right")) {
					if (rightMap[y][x]==4) 
						unknownGrids++;
				} else {
					if (leftMap[y][x]==4) 
						unknownGrids++;
				}
			}
		}
		//System.out.println("unknown counts "+xu+","+yu+"-"+side+"="+unknownGrids);
		return unknownGrids;
	}

	/*
	 * This bactracks to set the path used to reach the state given as input from the current location
	 * -- updates movesList
	 */
	public void updateMovesList(int x1,int y1,int x2,int y2 ) {
		String  stateKey=x1+","+y1+","+x2+","+y2;

		int moveState = state.get(stateKey);
		//int moveState = state[x1][y1][x2][y2];
		int moves;
		if(round>10) 
			x1+=0;
		int xm1=x1,ym1=y1,xm2=x2,ym2=y2,deltaX,deltaY;
		while (moveState!=900 && moveState!=0) {
			moves=moveState/100;
			movesList.add(0,moves );
			deltaX=movesArray[moves][0];
			deltaY=movesArray[moves][1];
			int leftBit=(moveState%100)/10;
			int rightBit=moveState%10;
			if (leftBit==0) {
				xm1=x1-deltaX;
				ym1=y1-deltaY;
				x1=xm1;y1=ym1;
			}
			if (rightBit==0) {
				xm2=x2-deltaX;
				ym2=y2-deltaY;
				x2=xm2;y2=ym2;
			}
			stateKey=xm1+","+ym1+","+xm2+","+ym2;

			moveState = state.get(stateKey);
			//moveState = state[xm1][ym1][xm2][ym2];
			//updateMovesList(xm1,ym1,xm2,ym2);
		} 
	}

	/*
	 *  If simultaneous exit is possible 
	 */
	public boolean exitReached() {
		if(leftExitFound==true && rightExitFound==true) {
			String  stateKey=lPlayer_X_Exit+","+lPlayer_Y_Exit+","+rPlayer_X_Exit+","+rPlayer_Y_Exit;
			//			if (state[lPlayer_X_Exit][lPlayer_Y_Exit][rPlayer_X_Exit][rPlayer_Y_Exit]!=0)
			if (state.containsKey(stateKey))
				return true;
		}
		return false;
	}

	// trivial for debugging
	public void printMaps() {
		//if (debug) {
		System.out.println("\n\nLEFT MAP");
		System.out.print(" + |");
		for(int x=0;x<leftMap[0].length;x++)  {
			System.out.print(" "+x%10);
		}
		System.out.println("");
		for(int x=0;x<=leftMap[0].length;x++)  {
			System.out.print(" _");
		}
		System.out.println("");
		for(int y=0;y<leftMap.length;y++) {
			System.out.print(" "+y%10+" |");
			for(int x=0;x<leftMap[0].length;x++)  {
				if(y== lPlayer_Y_Position && x== lPlayer_X_Position) 
					System.out.print(" *");
				else
					System.out.print(" "+leftMap[y][x]);
			}
			System.out.println("");
		}

		System.out.println("\n\nRIGHT MAP");
		System.out.print(" + |");
		for(int x=0;x<rightMap[0].length;x++)  {
			System.out.print(" "+x%10);
		}
		System.out.println("");
		for(int x=0;x<=rightMap[0].length;x++)  {
			System.out.print(" _");
		}
		System.out.println("");
		for(int y=0;y<rightMap.length;y++) {
			System.out.print(" "+y%10+" |");

			for(int x=0;x<rightMap[0].length;x++)  {
				if(y== rPlayer_Y_Position && x== rPlayer_X_Position) 
					System.out.print(" *");
				else
					System.out.print(" "+rightMap[y][x]);
			}
			System.out.println("");
		}
		System.out.println("maps printed");
		//}
	}

	// to free memory
	public void freeMemory  () {
		Runtime r = Runtime.getRuntime();
		r.gc();
	}
}

/*
 * TTD
 * lower bound of exit
 * giving high priority to the neighbours of exit 
 * imp file
 * XXnot the best soln  -id oldDistance >something thenlimit next 
 */