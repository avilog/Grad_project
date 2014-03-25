package flashSim;


public class Memory implements Comparable<Memory>{
	
	
	private int stat;
	private int stat2=0;
	private long size;
	private int host;
	private int address;

	
	public Memory(int stat, long size
			, int host,int address) {
		super();
		this.stat = stat;
		this.size = size;
		this.host = host;
		this.address = address;

	}
	public Memory(int stat, int stat2, long size
			, int host,int address) {
		super();
		this.stat = stat;
		this.size = size;
		this.host = host;
		this.stat2 = stat2;
		this.address = address;
	}
	public int getAddress() {
		return address;
	}
	public int getStat() {
		return stat;
	}
	public void setStat(int stat) {
		this.stat = stat;
	}
	public int getStat2() {
		return stat2;
	}
	public void setStat2(int stat2) {
		this.stat2 = stat2;
	}
	public long getSize() {
		return size;
	}
	public void setSize(long size) {
		this.size = size;
	}
	public int getHost() {
		return host;
	}
	public void setHost(int host) {
		this.host = host;
	}
	/*@Override
	public int compareTo(Memory m2) {
		
		return (( (this.getSize()-m2.getSize())>=(long)0) ? 1 : -1);
	}*/
	@Override
	public String toString() {
		
		return "Size:"+getSize();
	}
	public int compareTo(Memory compareA) {
		 

		//up order
		return     this.getStat() - compareA.getStat();
 
	}
}
