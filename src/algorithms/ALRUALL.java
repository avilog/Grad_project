package algorithms;

import java.util.ArrayList;
import java.util.Properties;
import flashSim.FlashDisk;
import flashSim.Memory;

public class ALRUALL extends Algorithm{

	int epoch = 0;//virtual times synced

	public ALRUALL(ArrayList<FlashDisk> fdisks, Integer co, Properties prop) {
		
		super(fdisks, co,  prop);

	}
	
	protected void before(int address, int size){
		
		epoch++;//cache miss - increment epoch
			
		stat1 = time;
		stat2 = epoch;
	}
	protected void after(){
		
	}
	protected void noRoom(){
			
		exchangeMemoriesVirtEpochIm();
	}	
	protected void found(){
		
		fdisks.get(i).read(address).setStat(time);
		fdisks.get(i).read(address).setStat2(epoch);

	}
			
	public ArrayList<Canditate> getCanditates(){
		
		ArrayList<Canditate> canditates = new ArrayList<Canditate>();
				
		int sizeB = fdisks.get(i).sizeToBlocks(size);

	    for(int j =0 ;j<fdisks.size(); j++){//find candidates from all hosts

			int availableB = fdisks.get(j).getAvailableB();
			Canditate can = new Canditate();
			int minSizeB = sizeB - availableB;
			fillCanByStatDisk( j,  minSizeB, can);
					 
		    if(can.size>=minSizeB){
		    	if(can.count>0){
		    		can.time /= can.count;
		    		can.epoch /= can.count;
		    	}
		    	canditates.add(can);
		    }
	    }
	    
	      return canditates;
	}
	void exchangeMemoriesVirtEpochIm(){
		
		ArrayList<Canditate> canditates = getCanditates();
		
		if(canditates.size()>0){//get best by epoch - size is tie breaker
			
			int  minEpoch=-1;
			Canditate chosen = null;
			
			for(Canditate can: canditates){
				
				if(chosen==null ||
						(can.epoch<minEpoch )||
						//(can.epoch==minEpoch &&  can.time<chosen.time) ||
						(can.epoch==minEpoch && can.size<chosen.size) ){
				
					chosen = can;
					minEpoch = can.epoch;
				}
				
			}
			
			if(chosen!=null){
				
				int minHost = chosen.host;
						
				for(Memory mem: chosen.mems){

					fdisks.get(minHost).free(mem.getAddress());
				}
				
				fdisks.get(minHost).write(address, size , time, epoch);
		    	Memory mem = fdisks.get(minHost).getMemoryList().get(address);
		    	
		    	if(i!= minHost)
		    		fdisks.get(i).WriteO(address, mem);	 
				
			}

	    	
			
		}
		
		
	}
}
