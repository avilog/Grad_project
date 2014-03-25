package flashSim;

import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.util.Properties;

public class Stat {

	public PrintWriter lOut,lHitOut;
	public int count=0, current=0;
	private String  name;
	int sumHit = 0, sumhitO =0, sumMis=0;
	private boolean files = true;;
	private int size, time = 0;
	

	public Stat(int size, int maxLines, String name, Properties prop) {
		
        if(Integer.parseInt(prop.get("flashsim.output.files").toString())!=1)
      	  files = false;
        
        this.size = size;
        
		this.name = name;

		/*if(files){
			try {
				lOut = new PrintWriter(name+".txt");
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
		}*/
		try {
			lHitOut = new PrintWriter(name+"hits.txt");
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public void save(int index, FlashDisk disk, int time){
		if(index==0){
			sumHit = disk.getCacheHit();
			sumhitO =disk.getCacheHitO();
			sumMis=disk.getCacheMiss();
		}
		else{
			sumHit += disk.getCacheHit();
			sumhitO +=disk.getCacheHitO();
			sumMis +=disk.getCacheMiss();
		}
		if(index == (size-1))	{
			lHitOut.println(time+","+sumHit+","+sumhitO+","+sumMis);
			this.time = time;
		}
	}

	public void addLog(int index, int time, String log){
		
		//if(files)
		//	lOut.println(time+")i="+index+" "+log);
	}
	
	public String getName() {
		return name;
	}
	public int getTime() {
		return time;
	}
}
