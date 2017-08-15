package edu.fit.cs.cn.main;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.fit.cs.cn.entities.Transmission;
import edu.fit.cs.cn.node.Node;
import edu.fit.cs.cn.util.Constants;
import edu.fit.cs.cn.util.FPConfigLoader;
import edu.fit.cs.cn.util.LoggingService;
import edu.fit.cs.cn.util.NWInterface;
import edu.fit.cs.cn.util.Util;

/**
 * <b>FPMain.java</b><BR>
 * Usage: This class  kicks off the emulation of medium and nodes.<BR>
 * @author Group7
 */

public class FPMain {

	//Date formatter
	public static SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH.mm.ss.SSS");

	//Start time of the medium
	public static long START_TIME;

	//Temporary variable to turn off the node threads.
	//public static long TIME_OUT;

	//Logger to write the key messages of the project execution status to a log file
	private static final Logger LOGGER = LoggingService.getLogger(Constants.CN_PROJECT_LOGGER);

	public static List<Transmission> transmissionList = new ArrayList<Transmission>();


	/**<BR>
	 * <strong>main Method.</strong><BR>
	 * Usage: Reads input config file, initializes the network configuration and starts up nodes to communicate.
	 * @param args
	 */    
	public static void main(String[] args) {

		try{
			if(args == null || args.length !=1){
				System.out.println();
				LOGGER.log(Level.SEVERE, "Invalid input arguements");
			}else{
				FPConfigLoader fpConfigLoader = new FPConfigLoader();
				fpConfigLoader.load(args[0]);
				fpConfigLoader.buildRoutingTables();

				/*for(String key : Util.nodeMap.keySet()) {
					Node node = Util.nodeMap.get(key);
					node.start();node.join();
				}*/

				LOGGER.log(Level.INFO, "Time\tNode/Medium\tEvent");
				START_TIME = System.currentTimeMillis();
				long currTime = System.currentTimeMillis();
				for (Iterator<Transmission> iterator = transmissionList.iterator(); iterator.hasNext();) {
					final Transmission transmission = iterator.next();
					while(true) {
						currTime = System.currentTimeMillis();
						long diffTime = currTime - FPMain.START_TIME;
						if( diffTime >= transmission.getTimeStamp()) {
							Runnable runnable = new Runnable() {
								@Override
								public void run() {
									Node sourceNode = Util.nodeMap.get(transmission.getSourceNode());
									Node destNode = Util.nodeMap.get(transmission.getDestNode());
									NWInterface nwInterface = destNode.getInterfaceList().get(0);
									try {
										sourceNode.sendToTransportLayer(transmission, nwInterface.getIpAddress());
									} catch (Exception e) {
										LOGGER.log(Level.SEVERE, "Exception while sending packet from "+sourceNode.getName() +" to "+destNode.getName(), e);
									}
								}
							};

							Thread thread = new Thread(runnable);
							thread.start();
							iterator.remove();
							break;
						}
					}
				}

			}
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, "Exception in main method: ", e);
		}	

	}
}
