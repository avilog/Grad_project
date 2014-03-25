package algorithms;

import java.util.ArrayList;
import java.util.Properties;
import flashSim.FlashDisk;
import flashSim.Memory;

/**
 *  ALLU only on current host class
 */
public class ALRU extends Algorithm {
		
	public ALRU(ArrayList<FlashDisk> fdisks, Integer co, Properties prop) {
		
		super(fdisks, co,  prop);
		
	}
	
	protected void before(int address, int size){

		stat1 = time;
		stat2 = 0;
	}
	protected void after(){
		
	}
	protected void noRoom(){
			
		exchangeMemoriesVirt();
	}	
	protected void found(){
		
		fdisks.get(i).read(address).setStat(time);
	}
	
	public Memory exchangeMemoriesVirt(){
				
		int sizeB = fdisks.get(i).sizeToBlocks(size);
		
		if(sizeB>fdisks.get(i).getSizeB())
			return null;
		
		int availableB = fdisks.get(i).getAvailableB();
		Canditate can = new Canditate();
		int minBSize = sizeB - availableB;
		fillCanByStatDisk( i,  minBSize, can);
	    
	    if(can.size>=minBSize){
	    	for(Memory mem: can.mems){
				
				fdisks.get(i).free(mem.getAddress());
			}
	    	fdisks.get(i).write(address, size , time, 0);
			//stat.addLog(i, time, "exchangeMemoriesVirt: before="+ get_memory_image(fdisks.get(i).getMemoryList()));
			
	    	return fdisks.get(i).getMemoryList().get(address);
	    }
	  
		//stat.addLog(i, time, "exchangeMemoriesVirt: after = the same - not enough free place");
	     return null;
	}

}
