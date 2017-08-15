package edu.fit.cs.cn.layers;

import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.fit.cs.cn.link.Medium;
import edu.fit.cs.cn.node.Node;
import edu.fit.cs.cn.protocol.DataLinkLayerFrame;
import edu.fit.cs.cn.util.Constants;
import edu.fit.cs.cn.util.LoggingService;
import edu.fit.cs.cn.util.Packet;

/**
 * <b>PhysicalLayer.java</b><BR>
 * Usage: This class is a implementation of a PhysicalLayer.<BR> 
 * It performs following functionalities<BR> 
 * 1. Receives data from its upper layer i.e, DataLink Layer<BR>
 * 2. Sends the data to connected medium.<BR>
 * 3. Receives data from medium.<BR>
 * 4. Sends data to its upper layer i.e, DataLink Layer<BR>
 * 
 * @author Group7
 */
public class PhysicalLayer {

	private static final Logger LOGGER = LoggingService.getLogger(Constants.CN_PROJECT_LOGGER);

    private Node node;

    public PhysicalLayer(Node node) {
        this.node = node;
    }

    public void sendToMedium(DataLinkLayerFrame dataLinkLayerFrame, long timeStamp) throws Exception {
    	//LOGGER.log(Level.INFO, "Node-"+node.getNodeName() + " --> Sending Packet to Medium ");
        Medium medium = node.getActiveInterface().getMedium(); 
        LOGGER.log(Level.INFO,timeStamp+"\t"+medium.getName()+"\tEnters BUSY state");
        medium.openConnection(node.getNodeName());
        medium.fillBuffer(dataLinkLayerFrame.toByteArray());
        /*byte[] data = dataLinkLayerFrame.toByteArray();
        for (byte b : data) {
            try {
                medium.fillBuffer(b);
                Thread.sleep(medium.getRTU());
            } catch (InterruptedException e) {
            }
        }*/
        medium.flush();
    }

    public void receiveFromMedium(Packet packet, BlockingQueue<Packet> blockingQueue, long timeStamp) throws Exception {
    	//LOGGER.log(Level.INFO, "Node-"+node.getNodeName() + " --> Received Packet from Medium ");
        sendToDataLinkLayer(packet, blockingQueue, timeStamp);
    }

    public void sendToDataLinkLayer(Packet packet, BlockingQueue<Packet> blockingQueue, long timeStamp) throws Exception {
    	//LOGGER.log(Level.INFO, "Node-"+node.getNodeName() + " --> Sending Packet to DataLink Layer");
        node.getDataLinkLayer().receiveFromPhysicalLayer(packet, blockingQueue, timeStamp);
    }

    public void receiveFromDataLinkLayer(DataLinkLayerFrame dataLinkLayerFrame, long timeStamp) throws Exception {
    	//LOGGER.log(Level.INFO, "Node-"+node.getNodeName() + " --> Received Packet from DataLink Layer ");
    	Medium medium = node.getActiveInterface().getMedium();
    	while(true) {
    		if (!medium.isBusy()) {
    			sendToMedium(dataLinkLayerFrame, timeStamp);
    			break;
    		}
    		//LOGGER.log(Level.INFO, medium +" is busy");
    	}
    }
}

