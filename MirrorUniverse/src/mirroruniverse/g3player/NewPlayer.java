package mirroruniverse.g3player;

import mirroruniverse.sim.Player;

public class NewPlayer implements Player {
	
	public static final int U = 3, D = 7, R = 1, L = 5, LU = 4, RU = 2, LD = 6,
			RD = 8;
	public static final boolean printGraph = false;
	public static final int maxSize = 20;
	public static final int unknown = -1, exit = 2, path = 0, obstacle = 1;
	
	
	protected int[][] leftMap;
	protected int[][] rightMap;
	protected int leftx, lefty, rightx,righty;
	protected int leftViewSize,rightViewSize;
	
	protected boolean leftExitFound = false, rightExitFound =false;
	/*
	 * ControllerStrategy analyzes the current map, and determine keep exploring or exit
	 */
	protected ControllerStrategy controllerStrategy;
	protected ExploreStrategy exploreStrategy;
	protected ExitStrategy exitStrategy;
	protected int lastAction;
	protected int round = 0;
	protected int dx,dy;// this should be updated by Strategy class when instruction is given
	
	public NewPlayer(){
		leftMap = new int[maxSize][maxSize];
		rightMap = new int[maxSize][maxSize];
		for(int i=0;i<maxSize;i++){
			for(int j=0;j<maxSize;j++){
				leftMap[i][j] = rightMap[i][j] = unknown;
			}
		}
		controllerStrategy = new ControllerStrategy();
		exploreStrategy = new ExploreStrategy();
		exitStrategy = new ExitStrategy();
	}
	
	
	@Override
	public int lookAndMove(int[][] aintViewL, int[][] aintViewR) {
		round ++;
		if(round==1){
			this.leftViewSize = aintViewL.length;
			this.rightViewSize = aintViewR.length;
		}

		this.updateMap(aintViewL,aintViewR);
		if(controllerStrategy.keepExplore(this) == true){
			lastAction = exploreStrategy.getMove(this);
			return lastAction;
		}
		else{
			lastAction = exitStrategy.getMove(this);
			return lastAction;
		}
		
	}


	private void updateMap(int[][] aintViewL, int[][] aintViewR) {
		
		
	}

}
