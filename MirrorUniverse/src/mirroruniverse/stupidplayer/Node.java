package mirroruniverse.stupidplayer;

public class Node {
	private int[] edges = new int[8];
	private byte lx;
	private byte ly;
	private byte rx;
	private byte ry;
	
	// we dont really need this edgeWeight array since we can code 
	// the getEdgeWeight() method to return a large value for all 
	// edges out of the sink and one otherwise
	private byte[] edgeWeights = new byte[8];
	
	public static final int UNEXPLORED = (125 << 24)+ ((125 & 0xFF) << 16)+ 
			((125 & 0xFF) << 8)+ (125 & 0xFF);
	
	public static void checkRange(int x){
		assert(x > -129 && x < 128);
	}
	
	protected Node(byte lx, byte ly, byte rx, byte ry) {
		this.lx = (byte)lx;
		this.ly = (byte)ly;
		this.rx = (byte)rx;
		this.ry = (byte)ry;
		for(byte i = 0; i < 8; i++){
			edges[i] = UNEXPLORED;
		}
	}
	
	public byte getNumUnexploredNeighbors(){
		byte count = 0;
		for(byte i = 0; i < 8; i++){
			if(edges[i] == UNEXPLORED) count++;
		}
		return count;
	}
	
	public int getDir(int target){
		for(int e = 0; e < 8; e++){
			if(edges[e] == target) return e+1;
		}
		return -1;
	}
	
	public void addEdge(int dir, int target){
		addEdge(dir, target, (byte)1);
	}
	
	public void addEdge(int dir, int target, byte edgeWeight){
		assert(dir > 0 && dir < 9);
		edges[dir-1] = target;
		edgeWeights[dir-1] = edgeWeight;
	}
	
	public static int getHash(byte lx, byte ly, byte rx, byte ry){
		return (ry << 24)+ ((rx & 0xFF) << 16)+ ((ly & 0xFF) << 8)+ (lx & 0xFF);
	}
	
	public static byte[] getBytes(int hash){
		return new byte[] {(byte)(hash),
                (byte)(hash >>> 8),
                (byte)(hash >>> 16),
                (byte)(hash >>> 24)};
	}

	@Override
	public boolean equals(Object obj) {
		return this.hashCode() == obj.hashCode();
	}

	@Override
	public int hashCode() {
		return (ry << 24)+ ((rx & 0xFF) << 16)+ ((ly & 0xFF) << 8)+ (lx & 0xFF);
	}

	@Override
	public String toString() {
		return "{("+lx+","+ly+");("+rx+","+ry+")}";
	}

	public byte getLx() {
		return lx;
	}

	public byte getLy() {
		return ly;
	}

	public byte getRx() {
		return rx;
	}

	public byte getRy() {
		return ry;
	}
	
	public int getEdgeWeight(int dir){
		return edgeWeights[dir-1];
	}

	public int[] getEdges() {
		return edges;
	}
	
	
	
}
