package edu.fit.cs.cn.layers;

import edu.fit.cs.cn.node.Node;
import edu.fit.cs.cn.protocol.DataLinkLayerFrame;
import edu.fit.cs.cn.protocol.NetworkLayerPacket;
import edu.fit.cs.cn.util.ARPTable;
import edu.fit.cs.cn.util.Constants;
import edu.fit.cs.cn.util.LoggingService;
import edu.fit.cs.cn.util.NWInterface;
import edu.fit.cs.cn.util.Packet;
import edu.fit.cs.cn.util.Util;

import java.util.Arrays;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * <b>DataLinkLayer.java</b><BR>
 * Usage: This class is a implementation of a DataLinkLayer.<BR> 
 * It performs following functionalities<BR> 
 * 1. Receives data from its upper layer i.e, Network Layer<BR>
 * 2. Constructs DataLinkLayer frame by appending source mac and destination mac to the the received data.<BR>
 * 3. Sends DataLinkLayer frame to its bottom layer i.e, Physical Layer.<BR>
 * 4. Receives data from its bottom layer i.e, Physical Layer.<BR>
 * 5. Validates, parses, constructs DataLinkLayer frame and sends the frame to its upper layer i.e, Network Layer<BR>
 * 
 * @author Group7
 */

public class DataLinkLayer {

	//Logger to write the key messages of the project execution status to a log file	
	private static final Logger LOGGER = LoggingService.getLogger(Constants.CN_PROJECT_LOGGER);

	private Node node;

	public DataLinkLayer(Node node) {
		this.node = node;
	}

	public void sendToNetworkLayer(byte[] data, long timeStamp) throws Exception {
		//LOGGER.log(Level.INFO, "Node-"+node.getNodeName()+ " --> Sending Packet to Network Layer ");
		node.getNetworkLayer().receiveFromDataLinkLayer(data, timeStamp);
	}

	public void receiveFromNetworkLayer(NetworkLayerPacket networkLayerPacket, byte[] destGateway, long timeStamp) throws Exception {
		DataLinkLayerFrame dataLinkLayerFrame = new DataLinkLayerFrame();
		dataLinkLayerFrame.body = networkLayerPacket.toByteArray();
		dataLinkLayerFrame.srcMacAddress = node.getActiveInterface().getMacAddress();
		dataLinkLayerFrame.destMacAddress = ARPTable.getMacAddressByIpAddress(destGateway);
		dataLinkLayerFrame.printPacket();
		networkLayerPacket.printPacket();
		
		dataLinkLayerFrame.calculateAndSetCRC();
		//LOGGER.log(Level.INFO, "Node-"+node.getNodeName() + " --> Constructed DataLink Layer Frame with received packet, source mac "+Util.getMacAddressInStringFormat(dataLinkLayerFrame.srcMacAddress)+" and destination mac "+Util.getMacAddressInStringFormat(dataLinkLayerFrame.destMacAddress));
		sendToPhysicalLayer(dataLinkLayerFrame, timeStamp);
	}

	public void sendToPhysicalLayer(DataLinkLayerFrame dataLinkLayerFrame, long timeStamp) throws Exception {
		//LOGGER.log(Level.INFO, "Node-"+node.getNodeName()+ " --> Sending Packet to Physical Layer ");
		node.getPhysicalLayer().receiveFromDataLinkLayer(dataLinkLayerFrame, timeStamp);
	}

	public void receiveFromPhysicalLayer(Packet packet, BlockingQueue<Packet> blockingQueue, long timeStamp) throws Exception {

		DataLinkLayerFrame dataLinkLayerFrame = new DataLinkLayerFrame(packet.getData());

		//LOGGER.log(Level.INFO, "Node-"+node.getNodeName()+ " --> Received Packet from Physical Layer ");
		boolean isMacMatched = false;
		for(NWInterface nwInterface : node.getInterfaceList()) {
			if(Arrays.equals(nwInterface.getMacAddress(), dataLinkLayerFrame.destMacAddress)) {
				isMacMatched = true;
				node.setActiveInterface(nwInterface);
				break;
			}
		}
		
		if (isMacMatched) {
			if (!dataLinkLayerFrame.verifyCRC()) {
				LOGGER.log(Level.INFO, timeStamp+"\t"+node.getNodeName()+"\t Receives Packet (Accepted Frame - "+Util.bytesToHex(dataLinkLayerFrame.destMacAddress)+" , Checksum: Check, CRC: Failed)");
				return;
			}
			LOGGER.log(Level.INFO, timeStamp+"\t"+node.getNodeName()+"\t Receives Packet (Accepted Frame - "+Util.bytesToHex(dataLinkLayerFrame.destMacAddress)+" , Checksum: Check, CRC: Check)");
			dataLinkLayerFrame.printPacket();
			sendToNetworkLayer(dataLinkLayerFrame.body, timeStamp);
		} else {
			LOGGER.log(Level.INFO, timeStamp+"\t"+node.getNodeName()+"\t Receives Packet (Dropped Frame - "+Util.bytesToHex(dataLinkLayerFrame.destMacAddress)+" )");
			blockingQueue.add(packet);
		}

	}
}
