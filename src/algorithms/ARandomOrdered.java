package algorithms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import flashSim.FlashDisk;
import flashSim.Memory;

public class ARandomOrdered extends Algorithm{

	public ARandomOrdered(ArrayList<FlashDisk> fdisks, Integer co,Properties prop) {
		
		super(fdisks, co,  prop);

	}

	protected void before(){
		
	}
	protected void noRoom(){
		
		exchangeMemoriesRandomOrdered();
				
	
	}
	protected void after(){
		
	
	}
	public Memory exchangeMemoriesRandomOrdered(){
		
		int sizeB = fdisks.get(i).sizeToBlocks(size);
		
		if(sizeB>fdisks.get(i).getSizeB())
			return null;
		
	//	stat.addLog(i, time, "exchangeMemoriesRandomOrdered: before= size: to write-"+sizeB 
	//			+"avilable: "+fdisks.get(i).getAvailableB()+"total:"+fdisks.get(i).getSizeB()+ get_memory_image(fdisks.get(i).getMemoryList()));

		int index = fdisks.get(i).getSmallestSizeB(sizeB, true);
		
		
		if(index<0){ //havnt deleted = nothing in the right size

			int indexm = (sizeB<=60) ? sizeB : 60;
	    	
			for(int j = indexm; j >=0 && !fdisks.get(i).isAvailableB(sizeB) ; j--){

			    Set<Map.Entry<Integer,Memory>> set = fdisks.get(i).getMemBysizeB().get(j).entrySet();
			    Iterator<Map.Entry<Integer,Memory>> iter = set.iterator();
				    
			      while(iter.hasNext()) {//find the min in that host

			          Map.Entry<Integer,Memory> me = (Map.Entry<Integer,Memory>)iter.next();
			          
						if(fdisks.get(i).isAvailableB(sizeB))
							break;
						iter.remove();							

						fdisks.get(i).free(me);
				}
			}
	
		}
			
		if(fdisks.get(i).isAvailableB(sizeB)){
	    	
	    	fdisks.get(i).write(address, size , stat1);
	    	
		//	stat.addLog(i, time, "exchangeMemoriesRandomOrdered: after="+ get_memory_image(fdisks.get(i).getMemoryList()));

	    	return fdisks.get(i).getMemoryList().get(address);
	    }
		stat.addLog(i, time, "exchangeMemoriesRandomOrdered: after = the same - not enough free place");

		     return null;
	}

}
