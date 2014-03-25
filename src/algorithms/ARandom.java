package algorithms;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Map;
import java.util.Properties;
import java.util.Set;

import flashSim.FlashDisk;
import flashSim.Memory;

/**
 *  random deletion class
 */
public class ARandom extends Algorithm{

	public ARandom(ArrayList<FlashDisk> fdisks, Integer co, Properties prop) {
		
		super(fdisks, co,  prop);

	}

	protected void before(){
		
	}
	protected void noRoom(){
		
		exchangeMemoriesRandom();
				
	
	}
	protected void after(){
		
	
	}
	public Memory exchangeMemoriesRandom(){
		
		  //  System.out.println("a before "+fdisks.get(i).getAvailableB());

			int sizeB = fdisks.get(i).sizeToBlocks(size);
			
			if(sizeB>fdisks.get(i).getSizeB())
				return null;
			
			//stat.addLog(i, time, "exchangeMemoriesRandom: before= size: to write-"+sizeB 
				//	+"avilable: "+fdisks.get(i).getAvailableB()+"total:"+fdisks.get(i).getSizeB()+ get_memory_image(fdisks.get(i).getMemoryList()));

			
			   Set<Map.Entry<Integer,Memory>> set = fdisks.get(i).getMemoryList().entrySet();
			    Iterator<Map.Entry<Integer,Memory>> iter = set.iterator();
				    
			      while(iter.hasNext() && !fdisks.get(i).isAvailableB(sizeB)) {
			    	  
			          Map.Entry<Integer,Memory> meme = (Map.Entry<Integer,Memory>)iter.next();
			          iter.remove();
			          stat.addLog(i, time,"deleting "+meme);

			          fdisks.get(i).free(meme);
			        
						  
				}

				
			if(fdisks.get(i).isAvailableB(sizeB)){
		    	
		    	fdisks.get(i).write(address, size , stat1, stat2);
		    	
			//	stat.addLog(i, time, "exchangeMemoriesRandom: after="+ get_memory_image(fdisks.get(i).getMemoryList()));
				
		    	return fdisks.get(i).getMemoryList().get(address);
		    }
		//	stat.addLog(i, time, "exchangeMemoriesRandom: after= size: to write-"+sizeB 
		//			+"avilable: "+fdisks.get(i).getAvailableB()+"total:"+fdisks.get(i).getSizeB()+"image:" +get_memory_image(fdisks.get(i).getMemoryList()));


			stat.addLog(i, time, "exchangeMemoriesRandom: after = the same - not enough free place");

			   return null;
		}

}
