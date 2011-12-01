/**
 * 
 */
package mirroruniverse.g3player;

/**
 * NOTE: short int is used since -32,768 to 32,767 will be enough
 */
public class PointPair {
	
	private short leftx;
	private short lefty;
	private short rightx;
	private short righty;

	public PointPair(int leftx, int lefty, int rightx, int righty) {
		this.leftx = (short) leftx;
		this.lefty = (short) lefty;
		this.rightx = (short) rightx;
		this.righty = (short) righty;
	}

	public short getLeftx() {
		return leftx;
	}

	public void setLeftx(int lx) {
		this.leftx = (short)lx;
	}
	
	public void updateLeftx(int lx) {
		this.leftx += (short)lx;
	}

	public short getLefty() {
		return lefty;
	}

	public void setLefty(int lefty) {
		this.lefty = (short) lefty;
	}
	
	public void updateLefty(int lefty) {
		this.lefty += (short) lefty;
	}

	public short getRightx() {
		return rightx;
	}

	public void setRightx(int rightx) {
		this.rightx = (short) rightx;
	}
	
	public void updateRightx(int rightx) {
		this.rightx += (short) rightx;
	}

	public short getRighty() {
		return righty;
	}

	public void setRighty(int righty) {
		this.righty = (short) righty;
	}
	
	public void updateRighty(int righty) {
		this.righty += (short) righty;
	}
	
	@Override
	public String toString() {
		return "[(" + leftx + "," + lefty + ");(" + rightx + "," + righty
				+ ")]";
	}

	public static void main(String[] args) {
		PointPair pp = new PointPair(1, 2, 3, 4);
		System.out.println(pp);
	}

	@Override
	public boolean equals(Object obj) {
		if(!(obj instanceof PointPair)) return false;
		PointPair p = (PointPair) obj;
		return p.leftx == leftx && p.lefty == lefty && p.rightx == rightx && p.righty == righty;
	}

	public void update(PointPair target, PointPair source) {
		updateLeftx(target.getLeftx()-source.getLeftx());
		updateLefty(target.getLefty()-source.getLefty());
		updateRightx(target.getRightx()-source.getRightx());
		updateRighty(target.getRighty()-source.getRighty());
	}
	
	
}
