package flashSim;   


//package es.efor.plandifor.demo;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Properties;

import javax.imageio.ImageIO;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.JTextArea;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYSplineRenderer;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.data.xy.*;
import org.jfree.ui.*;

import algorithms.Algorithm;

public class GraphResult extends ApplicationFrame
{
		
	private static final long serialVersionUID = 1L;
	private static boolean window = true, files = true;
	private static String summary;
	static int otherCache=10;
	static int sameCache=1;
	static int sharedDisk=100;
	
		static class DemoPanel extends JPanel
      {

			static int lines;
			
			private static final long serialVersionUID = 1L;
			private XYDataset data1;
			private JPanel logPanel = new JPanel();
			private JTextArea storyArea;
			ArrayList<Algorithm> algos;
              private XYDataset createSampleData()
              {
            	  XYSeriesCollection xyseriescollection = null;
            	  XYSeries xyseries;
            	  for(Algorithm algo : algos){
            		  
                      xyseries = new XYSeries(algo.getSimpleName());
               
                	  BufferedReader br = null;
					try {
						br = new BufferedReader(new 
								FileReader(algo.stat.getName()+"hits.txt"));
					} catch (FileNotFoundException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
						return null;
					}
                	  
                	  String line ;
                	  int maxLines = 1000,lines = algo.stat.getTime(),jumpSize = lines/maxLines;
                	  try {
						while ( (line = br.readLine())!=null ){
							  
							  String[] parts = line.split(",");
							  int time,sum,sumO;
							  time = Integer.parseInt(parts[0]);
							  sum = Integer.parseInt(parts[1]);
							  sumO = Integer.parseInt(parts[2]);
							  
							  if((time%jumpSize)<2 || time>(lines-2)){
							  //System.out.println("time="+time+",sum="+(sum+sumO));
								  xyseries.add(time, sum+sumO);
							  }

						  }
					} catch (NumberFormatException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
                	  
                  
            	  if(xyseriescollection==null)
            		  xyseriescollection  = new XYSeriesCollection(xyseries);
                else xyseriescollection.addSeries(xyseries);

            	  }
            	  xyseriescollection.addSeries(new XYSeries(""));
                                  
                      return xyseriescollection;
              }

              public void createContentPane (){                  
  
                  
               if(files){
	  				try {
						PrintWriter out = new PrintWriter("summary.txt");
						 out.println(summary);
						 out.close();
					} catch (FileNotFoundException e) {
						e.printStackTrace();
					}
               }
  				if(window){
            	  logPanel = new JPanel();
            	  //totalGUI.setBackground(Color.blue);
            	  logPanel.setLocation(220, 10);
            	  logPanel.setSize(50, 50);

                  storyArea = new JTextArea("Summary\n"+summary, 5, 30);
                  storyArea.setEditable(false);
                  storyArea.setLineWrap(true);
                  storyArea.setWrapStyleWord(true);
                                                    
                  
                  logPanel.add(storyArea);
                  
                  // Finally we return the JPanel.
                  logPanel.setOpaque(true);
  				}
              }
              private JTabbedPane createContent()
              {

            	  
                      JTabbedPane jtabbedpane = new JTabbedPane();
                      jtabbedpane.add("Hits:", createChartPanel1());
                      jtabbedpane.add("Times:", createChartPanel2());
                      createContentPane ();
                      jtabbedpane.add("Summary:", logPanel);
                      
                         
                      return jtabbedpane;
              }

              private ChartPanel createChartPanel1()
              {
                      NumberAxis numberaxis = new NumberAxis("Advance");
                      numberaxis.setAutoRangeIncludesZero(false);
                      NumberAxis numberaxis1 = new NumberAxis("Hit");
                      numberaxis1.setAutoRangeIncludesZero(false);
                      XYSplineRenderer xysplinerenderer = new XYSplineRenderer();
                      XYPlot xyplot = new XYPlot(data1, numberaxis, numberaxis1, xysplinerenderer);
                      xyplot.setBackgroundPaint(Color.lightGray);
                      xyplot.setDomainGridlinePaint(Color.white);
                      xyplot.setRangeGridlinePaint(Color.white);
                      xyplot.setAxisOffset(new RectangleInsets(4D, 4D, 4D, 4D));
                      JFreeChart jfreechart = new JFreeChart("Hit Graph", JFreeChart.DEFAULT_TITLE_FONT, xyplot, true);
                      jfreechart.setBackgroundPaint(Color.white);
                      
                   if(files){ 
                	   
	                      try{
	                  	    saveToFile(jfreechart,"graph.jpg",500,300,100);
	                  	}catch(Exception e){
	                  	    e.printStackTrace();
	                  	} 
                   }
                      ChartPanel chartpanel = new ChartPanel(jfreechart, false);

                      return chartpanel;
              }
              private ChartPanel createChartPanel2()
              {
            	  // Create a simple Bar chart
            	  DefaultCategoryDataset dataset = new DefaultCategoryDataset();
            	  for(Algorithm algo : algos){
            	  
                	  int sum = 0,sumO = 0, sumMiss = 0;
                	  for(int i = 0 ; i< algo.getFdisks().size() ; i++){
                		  sum+=algo.getFdisks().get(i).getCacheHit();
                		  sumO+=algo.getFdisks().get(i).getCacheHitO();
                		  sumMiss+=algo.getFdisks().get(i).getCacheMiss();
                	  }

                	  dataset.setValue(sum*sameCache +sumO*otherCache+sumMiss*sharedDisk, "Time", algo.getSimpleName());
                  //
            	  }
            	  JFreeChart jfreechart = ChartFactory.createBarChart3D("total times", 
            	  "Algoritham", "time(ms)", dataset, 
            	  PlotOrientation.VERTICAL, true, true, false);
            	  
                      
                   if(files){ 
                	   
	                      try{
	                  	    saveToFile(jfreechart,"Times.jpg",500,300,100);
	                  	}catch(Exception e){
	                  	    e.printStackTrace();
	                  	} 
                   }
                      ChartPanel chartpanel = new ChartPanel(jfreechart, false);

                      return chartpanel;
              }
              public DemoPanel(ArrayList<Algorithm> algos)
              {
                      super(new BorderLayout());
            	      this.algos =algos;
                      data1 = createSampleData();
                      add(createContent());
              }
      }


      public GraphResult(Properties prop, ArrayList<Algorithm> algos, String summary)
      {
              super("Flash simulator");
              //this.options = options;
              if(Integer.parseInt(prop.get("flashsim.output.window").toString())!=1)
            	  window = false;
              
              if(Integer.parseInt(prop.get("flashsim.output.files").toString())!=1)
            	  files = false;
              
              GraphResult.summary = summary;
              
              otherCache = Integer.parseInt(prop.get("flashsim.times.otherCache").toString());
              sameCache = Integer.parseInt(prop.get("flashsim.times.sameCache").toString());
              sharedDisk = Integer.parseInt(prop.get("flashsim.times.sharedDisk").toString());
            		  
              JPanel jpanel = createDemoPanel(algos);
              getContentPane().add(jpanel);
      }

      public static JPanel createDemoPanel(ArrayList<Algorithm> algos)
      {
              return new DemoPanel(algos);
      }
      public static void saveToFile(JFreeChart chart,
    		    String aFileName,
    		    int width,
    		    int height,
    		    double quality)
    		    throws FileNotFoundException, IOException
    		    {
    		        BufferedImage img = draw( chart, width*2, height*2 );
    		 
    		        File fos = new File(aFileName);
    		        ImageIO.write(img, 
                            "jpg",
                            fos) ;
                   
    		    }
      protected static BufferedImage draw(JFreeChart chart, int width, int height)
      {
          BufferedImage img =
          new BufferedImage(width , height,
          BufferedImage.TYPE_INT_RGB);
          Graphics2D g2 = img.createGraphics();
                         
          chart.draw(g2, new Rectangle2D.Double(0, 0, width, height));
   
          g2.dispose();
          return img;
      }
}
