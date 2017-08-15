package edu.fit.cs.cn.layers;

import edu.fit.cs.cn.main.FPMain;
import edu.fit.cs.cn.node.Node;
import edu.fit.cs.cn.protocol.DataLinkLayerFrame;
import edu.fit.cs.cn.protocol.NetworkLayerPacket;
import edu.fit.cs.cn.util.Constants;
import edu.fit.cs.cn.util.LoggingService;
import edu.fit.cs.cn.util.NWInterface;
import edu.fit.cs.cn.util.Util;

import java.util.Arrays;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <b>TransportLayer.java</b><BR>
 * Usage: This class is a implementation of a TransportLayer.<BR> 
 * It performs following functionalities<BR> 
 * 1. Receives data from its upper layer<BR>
 * 2. Fragments the data into smaller chunks based on the MTU.<BR>
 * 3. Sends smaller chunks to its bottom layer i.e, Network Layer.<BR>
 * 4. Receives data from its bottom layer i.e, Network Layer.<BR>
 * 5. Sends data to its upper layer<BR> 
 * @author Group7
 */

public class TransportLayer {

	private static final Logger LOGGER = LoggingService.getLogger(Constants.CN_PROJECT_LOGGER);

    private Node node;

    public TransportLayer(Node node) {
        this.node = node;
    }

    public void sendToASPLayer(NetworkLayerPacket networkLayerPacket) {
        //LOGGER.log(Level.INFO, "Node-"+node.getNodeName() + " --> Sending Packet to Upper Layer");
        node.receiveFromTransportLayer(networkLayerPacket);
    }

    public void receiveFromASPLayer(long timeStamp, byte[] data, byte[] destAddress) throws Exception {
    	NWInterface nwInterface = node.getInterfaceList().get(0);
    	node.setActiveInterface(nwInterface);
        int remainingSegmentSize = nwInterface.getMedium().getMTU() - NetworkLayerPacket.HEADER_SIZE - DataLinkLayerFrame.HEADER_N_CRC_SIZE;
        //int totalFileSize = data.length;
        int parts = data.length / remainingSegmentSize;
        int size = data.length % remainingSegmentSize;
        LOGGER.log(Level.INFO, timeStamp+"\t"+node.getNodeName() + "\tFragments JOB1 into "+parts+" parts each of "+size+" KB");
        int counter = 0;
        boolean isStarted = true;
        while (data.length > 0) {
            NetworkLayerPacket networkLayerPacket = new NetworkLayerPacket();
            networkLayerPacket.srcIpAddress = nwInterface.getIpAddress();
            byte[] temp = null;
            if (isStarted) {
                isStarted = false;
                networkLayerPacket.flagsOffset = Util.intToByteArr(0);
                
            } else {
                networkLayerPacket.flagsOffset = Util.intToByteArr(data.length);
               
            }
            if (data.length < remainingSegmentSize) {
                temp = new byte[data.length];
                temp = Arrays.copyOfRange(data, 0, data.length);
                networkLayerPacket.flagsOffset[1] &= (byte) 0xDF;
                networkLayerPacket.body = temp;
            } else {
                temp = new byte[remainingSegmentSize];
                temp = Arrays.copyOfRange(data, 0, remainingSegmentSize);
                networkLayerPacket.flagsOffset[1] |= (byte) 0x20;
                networkLayerPacket.body = temp;
            }
            LOGGER.log(Level.INFO, timeStamp+"\t"+node.getNodeName()+"\tStarts Transmitting J1F1 (Frame "+(counter++)+" of Job ) to "+Util.bytesToHex(destAddress));
            sendToNetworkLayer(networkLayerPacket, destAddress, timeStamp);
            long currTime = System.currentTimeMillis();
			long diffTime = currTime - FPMain.START_TIME;
            LOGGER.log(Level.INFO, diffTime+"\t"+node.getNodeName()+"\tFinishes Transmitting "+temp.length+" KB");
            data = Arrays.copyOfRange(data, temp.length, data.length);
        }
    }

    public void sendToNetworkLayer(NetworkLayerPacket networkLayerPacket, byte[] destAddress, long timeStamp) throws Exception {
    	//LOGGER.log(Level.INFO, "Node-"+node.getNodeName() + " --> Sending Packet to Network Layer ");
        node.getNetworkLayer().receiveFromTransportLayer(networkLayerPacket, destAddress, timeStamp);
    }

    public void receiveFromNetworkLayer(NetworkLayerPacket networkLayerPacket) {
    	//LOGGER.log(Level.INFO, "Node-"+node.getNodeName() + " --> Received Packet from Network Layer");
        sendToASPLayer(networkLayerPacket);
    }


}
