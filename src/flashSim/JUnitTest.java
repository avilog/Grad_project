package flashSim;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Properties;

import org.junit.Before;
import org.junit.Test;

import algorithms.ARandom;
import algorithms.Algorithm;

public class JUnitTest {

	FlashDisk testd;
	int blockSize = 8;
	Long diskSize =(long) 1;
	@Before
	public void setUp() throws Exception {
		
		testd = new FlashDisk((double)diskSize, blockSize, 1);//1gb disk size, 8kb bblock size, host = 1
	}

	@Test
	public void testSimple() {
		testd.getAvailableB();
		long sizeToWrite =(long)( (1L<<30) - 1000 - 8192);//one free block+100- free bytes in last block

		int souldbe = 131072 ;//=(2^30)/((double)8*2^10)
		assertTrue("Avilable blocks "+testd.getAvailableB(), souldbe==testd.getAvailableB());
		 		
		assertTrue("sizeToBlocks "+sizeToWrite+" "+testd.sizeToBlocks(sizeToWrite), testd.sizeToBlocks(sizeToWrite)==(souldbe-1));
		
		assertTrue("isAvailable", testd.isAvailable(sizeToWrite));

		 testd.write(1,sizeToWrite, 0);	
		 
		assertTrue("Avilable blocks "+testd.getAvailableB(), 1==testd.getAvailableB());
		
		testd.free(1);
		assertTrue("Avilable blocks "+testd.getAvailableB(), souldbe==testd.getAvailableB());

		

	}
	
	@Test
	public void testRandom() {
		
		int souldbe = 131072 ;//=(2^30)/((double)8*2^10)

		assertTrue("Avilable blocks "+testd.getAvailableB(), souldbe==testd.getAvailableB());

		ArrayList<FlashDisk>  fdisks = new ArrayList<FlashDisk>();
		fdisks.add(0,testd);
		Algorithm a = new ARandom(fdisks, 1, new Properties());
		long sizeToWrite =(long)( (1L<<30));
		String[] array = {"WA","1","D","1",sizeToWrite+"","swapper"};
		a.run( array, 0);
		String[] array2  = {"WA","1","D","2",sizeToWrite+"","swapper"};
		a.run( array2, 0);
	}
	
	@Test
	public void testOrder() {
		FlashDisk  fdisk = new FlashDisk(100D,8,1);
		fdisk.write(1, 1L, 1, 0);
		fdisk.write(2, 1L, 2, 0);
		fdisk.write(3, 1L, 0, 0);
		fdisk.write(4, 1L, 5, 0);
		fdisk.write(5, 1L, 3, 0);
		List<Memory> memoryListStat = 
				new ArrayList<Memory>(fdisk.getMemoryList().values());

		Collections.sort( memoryListStat );
		for(Memory mem: memoryListStat)
			System.out.println(mem.getStat());
	}

}
