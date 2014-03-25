package flashSim;


import java.util.ArrayList;
import java.util.BitSet;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;


/**
*   simulise simple flash disk  
 */
public class FlashDisk implements Disk{
	
	//memoryListO = memory list in other hosts
	private HashMap<Integer, Memory> memoryList, memoryListO;
	private ArrayList<Memory> memoryListStat;
	ArrayList<FlashDisk> fdisks;


	private long blocksize;
 

	private int host;
	public int getHost() {
		return host;
	}

	private int cacheHit = 0, cacheHitO = 0, cacheMiss = 0,lastCache = 1;//0==other
	private int availableB = 0, sizeB = 0;
	 //Avi - test  for efficiency. memBysize[i] = all with size blocksize*(i+1)
	 //(sizesB & (1<<i)>0 -> there are memories with size = blocksize * (i+1)
	 //in practice we can use a regular array with fixed size of 63 (and each one is a hash map)
	 private BitSet sizesB=new BitSet(61);
	 private ArrayList<HashMap<Integer, Memory>> memBysizeB = new ArrayList<HashMap<Integer, Memory>>(61);
	 
	/**
	*   base constructor
	 *
	 * @memSize memSize  in mib
	 * @blocksize blockSize in kib
	 * @host host index
	 */
	public FlashDisk(Double memSize, int blocksize, int host) {
		
		super();
		this.blocksize = blocksize  * (1L<<10);
		this.host = host;
		this.availableB =  this.sizeB = sizeToBlocks((long)(memSize * (1L<<20)));
		this.memoryList = new HashMap<Integer, Memory>();
		this.memoryListO = new HashMap<Integer, Memory>();
		memoryListStat = new ArrayList<Memory> ();
		
		for(int j = 0; j <= 60 ; j++)
			memBysizeB.add(j, new HashMap<Integer, Memory>());
	}
	public ArrayList<FlashDisk> getFdisks() {
		return fdisks;
	}
	public void setFdisks(ArrayList<FlashDisk> fdisks) {
		this.fdisks = fdisks;
	}
	public ArrayList<Memory> getMemoryListStat() {
		return memoryListStat;
	}

	 public long getBlocksize() {
		return blocksize;
	}
	public ArrayList<HashMap<Integer, Memory>> getMemBysizeB() {
		return memBysizeB;
	}
	public int getSizeB() {
		return (int) sizeB;
	}
	public int getAvailableB() {
		return (int) availableB;
	}
	 public void incCacheHit() {
		 if(lastCache==1)
			cacheHit++;
		 else cacheHitO++;
	}
	 public void incCacheMiss() {
		 cacheMiss++;
	}
	 public int getCacheHitO() {
		return cacheHitO;
	}
	 public int getCacheHit() {
		return cacheHit;
	}

	public int getCacheMiss() {
		return cacheMiss;
	}
	 public HashMap<Integer, Memory> getMemoryList() {
		return memoryList;
	}
	 public HashMap<Integer, Memory> getMemoryListO() {
		return memoryListO;
	}
	 public int sizeToBlocks(Long size) {
		return (int) Math.ceil(size / (double)blocksize);
	}
	 public boolean isAvailable(Long size) {
		return (availableB>=sizeToBlocks(size));
	}
	 public boolean isAvailableB(int sizeB) {
		return (availableB>=sizeB);
	}
		/**
	*   get the smallest size of memory that >- size. 60 = atleast 60 blocks
	 */
	 
	 public int getSmallestSizeB(int minBlocks, boolean toDelete) {
		 
		 int index = -1;
		 
		if( minBlocks < 60 && isAvailableB(minBlocks)){
				
			index = sizesB.nextSetBit(minBlocks);
			
		}
		else{
			
			if(isAvailableB(minBlocks) && memBysizeB.get(60).size()>0) index= 60;
			
			else index= -1;
		}
		
		if(index!=-1 && toDelete){
			 
			this.free(memBysizeB.get(index).entrySet().iterator().next());
		}
			
		return index;
		
	}

	 
		/**
	*   invariant - address not exsit - use read to check before
	 */
	 
	public Memory write(Integer address, long size, int stat, int stat2){

	    Memory mem=null;
	    
	    if(isAvailable(size)){

	    	availableB= availableB - sizeToBlocks(size);
	    
	    	mem = new Memory(stat, stat2,
					size, host, address);
	    	this.memoryList.put(address,
	    			mem);
	    	
	    	//memoryListStat.add(mem);
	    	//Collections.sort(memoryListStat);
	    	//add to memory by sizes array
	    	
	    	int sizeB = sizeToBlocks(size);

	    	int indexm = (sizeB<=60) ? sizeB : 60;
	    	memBysizeB.get(indexm).put(address,mem);
	    	 sizesB.set(indexm);
	    				
	    }
		
		
		return mem;
	}
	
	public Memory read(Integer address){
		
		Memory mem = null;
		mem = this.memoryList.get(address);
		lastCache = 1;
		
		if(mem==null){
			lastCache = 0;
			for(FlashDisk fdisk:fdisks){
				mem = this.memoryListO.get(address*31+fdisk.getHost());				
				if(mem!=null) break;
			}
		}
		
		return mem;
		
	}
			/**
	*   add written memory from other host
	 */
	
	public Memory WriteO(int address, Memory mem ){
	
		if(mem!=null)
			this.memoryListO.put(mem.getAddress()*31+mem.getHost(),
					mem);
		
		return mem;
	}
				/**
	*   if memory exsit in another host it will only be deleted from this  host addreses
	 */
	
	public Memory free(int address, Memory mem){
		if(mem != null){

			if( mem.getHost()==host ){//in that host
				    int sizeB = sizeToBlocks(mem.getSize());
					this.availableB=this.availableB+sizeB;
					this.memoryList.remove(address);
					
					for(FlashDisk fdisk:fdisks){
						if(fdisk.getHost()!=host)
							fdisk.freeO(mem);
					}
					
					//remove from memory sizes array
			    	int indexm = (sizeB<=60) ? sizeB : 60;
			    	memBysizeB.get(indexm).remove(address);
			    	if(memBysizeB.get(indexm).size()==0)
			    		sizesB.clear(indexm);
				
			}
			else this.memoryListO.remove(mem.getAddress()*31+mem.getHost());
			
		}
		return mem;
	}
	public Memory free(int address){
		
		Memory mem = read(new Integer(address));
		if(mem!=null)
		return free(address, mem);
		else return null;
	}
	public void freeO(Memory mem){
		
		this.memoryListO.remove(mem.hashCode());
	}
	public Memory free(Map.Entry<Integer,Memory> meme){
		
		//Memory mem = read(address);
		Memory mem = meme.getValue();
		if(mem != null){

			if( mem.getHost()==host ){//in that host
				    int sizeB = sizeToBlocks(mem.getSize());
					this.availableB=this.availableB+sizeB;
					this.memoryList.remove(meme.getKey());
					

					for(FlashDisk fdisk:fdisks){
						if(fdisk.getHost()!=host)
							fdisk.freeO(mem);
					}
					
					//remove from memory sizes array
			    	int indexm = (sizeB<=60) ? sizeB : 60;
			    	memBysizeB.get(indexm).remove(meme.getKey());
			    	if(memBysizeB.get(indexm).size()==0)
			    		sizesB.clear(indexm);
				
			}
			else this.memoryListO.remove(mem.getAddress()*31+mem.getHost());
			
		}
		return mem;
		
	}

	@Override
	public Memory write(Integer address, long size, int stat) {
		// TODO Auto-generated method stub
		return write(address,  size,  stat,0);
	}

}

