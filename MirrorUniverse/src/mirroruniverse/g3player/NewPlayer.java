package mirroruniverse.g3player;

import mirroruniverse.sim.Player;

public class NewPlayer implements Player {
	
	public static final int U = 3, D = 7, R = 1, L = 5, LU = 4, RU = 2, LD = 6,
			RD = 8;
	public static final boolean printGraph = false;
	public static final int maxSize = 20;
	
	protected int[][] leftMap;
	protected int[][] rightMap;
	protected int leftx, lefty, rightx,righty;
	
	protected boolean leftExitFound = false, rightExitFound =false;
	/*
	 * ControllerStrategy analyzes the current map, and determine keep exploring or exit
	 */
	protected ControllerStrategy controllerStrategy;
	protected ExploreStrategy exploreStrategy;
	protected ExitStrategy exitStrategy;
	protected int lastAction;
	
	public NewPlayer(){
		leftMap = new int[maxSize*2][maxSize*2];
		rightMap = new int[maxSize*2][maxSize*2];
		controllerStrategy = new ControllerStrategy();
		exploreStrategy = new ExploreStrategy();
		exitStrategy = new ExitStrategy();
	}
	
	
	@Override
	public int lookAndMove(int[][] aintViewL, int[][] aintViewR) {

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
		// TODO Auto-generated method stub
		
	}

}
