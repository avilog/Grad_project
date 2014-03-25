package algorithms;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Properties;
import flashSim.FlashDisk;
import flashSim.Memory;
import flashSim.Stat;

/**
 *  base algorithm class
 */
public  class Algorithm implements Comparable<Algorithm>{

	 public enum traceLine {
		   TYPE, TIMESTAMP, ACTION, LBA, SIZE,NAME
		 }
	short STATINIT = 1<<8;
	ArrayList<FlashDisk>  fdisks = new ArrayList<FlashDisk>();
	int total_cache_hit=0, total_other_cache_hit=0, total_cache_miss=0, total_updated = 0;;
	int i=0,stat1=0, stat2= 0, co=0, othercache=0; 
	int address= 0;
	Memory mem;
	long size=0;
	String[] params;
	int count=0, time=-1;
	public Stat stat;
	public static int maxLines;
	
	/**
	*   base constructor
	 *
	 * @fdisks array of FlashDisk objects
	 * @co is cooperative? ( 1 = yes )
	 * @prop algorithm properties
	 */
	public Algorithm(ArrayList<FlashDisk> fdisks,Integer co, Properties prop) {

		super();
		this.co = co;
		
		this.fdisks = fdisks;
		for(FlashDisk fdisk:fdisks)
			fdisk.setFdisks(fdisks);
		
		stat = new Stat(fdisks.size(), maxLines, getSimpleName(), prop);
	}
	public ArrayList<FlashDisk> getFdisks() {
		return fdisks;
	}
		/**
	*   run for single line = memory
	 *
	 * @paramsp exploded line
	 * @i line index
	 */
	public  int run(String[] paramsp,int i){
		
		params = paramsp;
		address = (int)((Long.parseLong(params[traceLine.LBA.ordinal()].trim())));
		String type = params[traceLine.TYPE.ordinal()].trim();
		String action = params[traceLine.ACTION.ordinal()].trim();
		this.i = i;
		
		if(i==0){
			time++;
		}
		if(action.compareTo("D")!=0 ) {//if not a new event to the drive
			stat.save(i, fdisks.get(i),
					time);
			return 0;
		}
		
		mem=fdisks.get(i).read(address);
		//System.out.println(Long.parseLong(params[traceLine.TIMESTAMP.ordinal()])+getSimpleName() +address+ mem+"reading");

		if(co==1 && mem==null)	{//if cooperative algo. check if exsit in other hosts caches
			
			for(int k=0; k< fdisks.size() && mem==null; k++){
				
				mem=fdisks.get(i).read(address);
			}
			if(mem!=null){
				fdisks.get(i).WriteO(address, mem);
				mem=fdisks.get(i).read(address);
			}
		}
		if(mem==null){

			 size = Long.parseLong(params[traceLine.SIZE.ordinal()].trim());
			

			if(type.compareTo("RA")==0 || type.compareTo("R")==0 || type.compareTo("RM")==0){//read

				fdisks.get(i).incCacheMiss();
				count++;
			}
			
			before();
			mem = fdisks.get(i).write(address, size , stat1, stat2);
			if(co==1 && mem==null)	{//if cooperative algo. check if רםםצ exsit in other hosts caches
				
				for(int k=0; k< fdisks.size() && mem==null; k++){
					
					mem = fdisks.get(i).write(address, size , stat1, stat2);
				}
				if(mem!=null){
					fdisks.get(i).WriteO(address, mem);
					mem=fdisks.get(i).read(address);
				}
			}
			if(mem==null){

				//no place here, lets try to write to another disk

				noRoom();
			}
	

			
			after();
				
			
		}
		else{
			found();
			if(type.compareTo("RA")==0 || type.compareTo("R")==0|| type.compareTo("RM")==0){//read
				
				stat.addLog(i, time, "Cache hit");
				fdisks.get(i).incCacheHit();
				
				count++;
				
			}

		}

		
		stat.save(i, fdisks.get(i),
				time);
			
		return 1;
		
	}
	protected void found(){
		
	}
	protected void before(){
		
	}
	protected void after(){
		
	}
	protected void noRoom(){
		
		//this memory is stored in another flash disk, we will 
		//put a reference to it in that disk
		//fdisks.get(i).WriteO(address, mem);
	}

		/**
	*   canditate object for ALRU,ALRUALL  classes
	 */
	public class Canditate{
		
		ArrayList<Memory> mems = new ArrayList<Memory>();
		public int size=0, epoch=0,time=0,host=0,count=0;
	}
	/**
	*   fill canditate object that fit to sizeB in disk
	 */
	public  void fillCanByStatDisk(int disk, int sizeB,Canditate can ){
		
		List<Memory> memoryListStat = 
				new ArrayList<Memory>(fdisks.get(disk).
						getMemoryList().values());
		Collections.sort( memoryListStat );
		
		can.host = disk;
		  for(Memory me: memoryListStat) {
			  
			  if(can.size<sizeB){
				  can.mems.add(me);
				  can.size+=fdisks.get(i).sizeToBlocks(me.getSize());
				  can.time+=me.getStat();
			      can.epoch+=me.getStat2();
				  can.count++;
			  }
			  else break;
				  
		}
	      
	}

		/**
	*   update totals for stat printing later
	 */
	public void updateTotals(){
		
		total_cache_hit = 0;
		total_other_cache_hit = 0;
		total_cache_miss = 0;
		total_updated = 1;
		
		for(int j=0; j<fdisks.size(); j++){
			
			total_cache_hit = total_cache_hit+fdisks.get(j).getCacheHit();
			total_other_cache_hit = total_other_cache_hit+fdisks.get(j).getCacheHitO();
			total_cache_miss = total_cache_miss+fdisks.get(j).getCacheMiss();

		}
		
	}
	public String printStat(){
		
		//printing routine
		StringBuilder sumarry = new StringBuilder();
		String sep = System.getProperty("line.separator");
		total_cache_hit = 0;
		total_other_cache_hit = 0;
		total_cache_miss = 0;
		total_updated = 1;
		for(int j=0; j<fdisks.size(); j++){
			
			total_cache_hit = total_cache_hit+fdisks.get(j).getCacheHit() +fdisks.get(j).getCacheHitO();
			total_other_cache_hit = total_other_cache_hit+fdisks.get(j).getCacheHitO();
			total_cache_miss = total_cache_miss+fdisks.get(j).getCacheMiss();
			
			sumarry.append(sep+"Host "+(j+1)+":");
			sumarry.append(sep+"Cache hit(+ other ) :"+fdisks.get(j).getCacheHit()+fdisks.get(j).getCacheHitO());
			sumarry.append(sep+"Other cache hit:"+fdisks.get(j).getCacheHitO());
			sumarry.append(sep+"Cache miss:"+fdisks.get(j).getCacheMiss());
			
			/*int inusep = 100;
			if((fdisks.get(j).getSizeB() - fdisks.get(j).getAvailableB()) > 0)
				inusep =  ((fdisks.get(j).getSizeB() - fdisks.get(j).getAvailableB())/fdisks.get(j).getSizeB())*100;
			*/
			sumarry.append(sep+"Disk size (blocks):"+fdisks.get(j).getSizeB() + "*" + fdisks.get(j).getBlocksize()+"B");
			sumarry.append(sep+"Available blocks:"+fdisks.get(j).getAvailableB());
			sumarry.append(sep+"-------------------------------------------------------------------");
			
		}
		
		sumarry.append(sep+"total_cache_hit:"+total_cache_hit);
		sumarry.append(sep+"total_other_cache_hit:"+total_other_cache_hit);
		sumarry.append(sep+"total_cache_miss:"+total_cache_miss);
		
		return sumarry.toString();
	}
	public String get_memory_image(HashMap<Integer, Memory> memoryList) {
		
		StringBuilder image = new StringBuilder();
		 
	    Iterator<Map.Entry<Integer,Memory>> iter = memoryList.entrySet().iterator();
		    
	   
	      while(iter.hasNext()) {
	    	  		    	
	          Map.Entry<Integer,Memory> me = (Map.Entry<Integer,Memory>)iter.next();
	          image.append("("+me.getKey()+","+me.getValue()+") ");
	     //     iter.remove();					  
	      }	
	      
		return image.toString();
	}
	
	public int getTotal_cache_hit() {
		
		 if(total_updated==0)//not updated
			 updateTotals();
		 
		return total_cache_hit;
	}
	public int compareTo(Algorithm compareA) {
		 
		int compareCacheH = ((Algorithm) compareA).getTotal_cache_hit(); 

		//descending order
		return   compareCacheH - this.getTotal_cache_hit();
 
	}
	public String  getSimpleName() {
		// TODO Auto-generated method stub
		return this.getClass().getSimpleName().substring(1);
	}
}
