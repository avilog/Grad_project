package flashSim;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Properties;
import algorithms.*;
import org.jfree.ui.RefineryUtilities;

public class FlashSim {

	// traceline parts
	 public enum traceLine {
		   TYPE, TIMESTAMP, ACTION, LBA, SIZE,NAME
		 }


	public static void main(String[] args) {
		
		Properties prop = new Properties();
		ArrayList<Algorithm> algos = new ArrayList<Algorithm>();
		int hostsN = 0;
		ArrayList<BufferedReader> brs = new ArrayList<BufferedReader>();
		int maxLines = 100;
		int finished=0;
		
		//loading the properties
		try {
			prop.load(new FileInputStream("flashSim.properties"));
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		//Initialization 
		
		hostsN = Integer.parseInt(prop.get("flashsim.hosts.num").toString());
		maxLines = Integer.parseInt(prop.get("flashsim.hosts.maxlines").toString());		
		String[] algosa = prop.get("flashsim.globalalgos").toString().split(",");
		Algorithm.maxLines = maxLines;
		
		for(int i =0; i< hostsN;i++){
			try {
				BufferedReader br = new BufferedReader(new 
						FileReader(prop.get("flashsim.host"+(i+1)+".trace").toString()));
				int linestart = Integer.parseInt(prop.get("flashsim.host"+(i+1)+".startline").toString());
				if(linestart>0)
					for (int j = 0; j < linestart && br.ready();j++ ) { br.readLine();}
				brs.add(i, br);
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	
		
		//The chosen algorithms to be tested
		for(int i = 0; i < algosa.length; i++){
			
			ArrayList<FlashDisk>  fdisks = new ArrayList<FlashDisk>();
			
			for(int j =0; j< hostsN;j++){
				
				fdisks.add(j, new FlashDisk(Double.parseDouble(prop.get("flashsim.host"+(j+1)+".flashsize").toString()),
						Integer.parseInt(prop.get("flashsim.host"+(j+1)+".blocksize").toString()), j));
			}
			
			String[] aparts = algosa[i].split("-");
			int co = Integer.parseInt(aparts[1]);
			String classname = "algorithms.A"+aparts[0];
			Algorithm a = null;
			
			try {
				
				a =  (Algorithm) Class.forName(classname).
						getDeclaredConstructor(ArrayList.class, Integer.class, Properties.class).newInstance(fdisks,co,prop);
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (SecurityException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}
			
			algos.add(i, a);
		}
		
		//The actual simulation! 
		while (maxLines-->0 && finished!=1){//if at least one has finished
			
			for(int i = 0; i< hostsN;i++){//process one line for every host
				
				try {
					String line;
					boolean readLine = false;

					while(!readLine){
											
						line = brs.get(i).readLine();
						
						if(line==null){
							
							finished=1;
							break;
						}
														 
						 // process the line.
						String[] parts = line.split(",");
						if (parts.length>5){
							String action = parts[traceLine.ACTION.ordinal()];
							

							if(action.trim().compareTo("C")>=0){ ;//we only care if its a new request
							readLine = true;
							
								for(int j =0; j< algosa.length;j++){
		
									algos.get(j).run(parts, i);
								}
						 	}
							
							}
					}
					
					
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
	
			}
		}
        StringBuilder sumarry = new StringBuilder();
        String sep = System.getProperty("line.separator");
		//print the statistics - the results
		for(int j =0; j< algosa.length;j++){
			
			sumarry.append(sep+sep+algos.get(j).getSimpleName());
			sumarry.append(algos.get(j).printStat());
		//	algos.get(j).stat.lOut.close();
			algos.get(j).stat.lHitOut.close();
		}
		

              
		for(int j =0; j< algosa.length;j++){//print the winners
			
			String desc;
			switch(j){
				case 0: desc="Golden medal!";break;
				case 1: desc="Silver medal!";break;
				case 2: desc="Bronze medal!";break;
				default: desc = (j+1)+"";
			}
			sumarry.append(sep+sep+desc+": "+algos.get(j).getSimpleName());
			sumarry.append(sep+algos.get(j).getTotal_cache_hit());
		}
		for(int i = 0; i< hostsN;i++){//close the traces files
			
			try {
				brs.get(i).close();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		if(Integer.parseInt(prop.get("flashsim.output.console").toString())==1){
			System.out.print(sumarry.toString());
		}
		Collections.sort(algos);//sort by total regular cache hit - descending order
		
		  GraphResult newGraph = new GraphResult(prop, algos,sumarry.toString());
		  if(Integer.parseInt(prop.get("flashsim.output.window").toString())==1){
	    	  newGraph.pack();
	          RefineryUtilities.centerFrameOnScreen(newGraph);
	          newGraph.setVisible(true);
		}
	}

}
