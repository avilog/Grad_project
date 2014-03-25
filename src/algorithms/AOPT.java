package algorithms;

import java.util.ArrayList;
import java.util.Properties;

import flashSim.FlashDisk;

/**
 *  optimum class - endless memory
 */
public class AOPT extends Algorithm {

	public AOPT(ArrayList<FlashDisk> fdisks, Integer co, Properties prop) {
		super(fdisks, co,  prop);

		}

	protected void before(){
		size=0;
	}
	protected void after(){
		
	}
	protected void noRoom(){
	
	}
}
