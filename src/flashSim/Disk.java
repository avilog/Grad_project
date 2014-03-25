package flashSim;

public interface Disk {
	
	public Memory write(Integer address, long size, int stat);
	
	Memory read(Integer index);
}
