package edu.fit.cs.cn.node;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.concurrent.BlockingQueue;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.fit.cs.cn.entities.Transmission;
import edu.fit.cs.cn.layers.DataLinkLayer;
import edu.fit.cs.cn.layers.NetworkLayer;
import edu.fit.cs.cn.layers.PhysicalLayer;
import edu.fit.cs.cn.layers.TransportLayer;
import edu.fit.cs.cn.link.Medium;
import edu.fit.cs.cn.main.FPMain;
import edu.fit.cs.cn.protocol.NetworkLayerPacket;
import edu.fit.cs.cn.util.Constants;
import edu.fit.cs.cn.util.LoggingService;
import edu.fit.cs.cn.util.NWInterface;
import edu.fit.cs.cn.util.Packet;
import edu.fit.cs.cn.util.Route;
import edu.fit.cs.cn.util.Util;

/**
 * <b>Node.java</b><BR>
 * Usage: This class is a implementation of a node<BR> 
 * which can connect to medium, transmit messages and receive messages<BR> 
 * using the 5 layers described in assignment 2<BR>
 * 
 * @author Group7
 */
public class Node extends Thread{

	private static final Logger LOGGER = LoggingService.getLogger(Constants.CN_PROJECT_LOGGER);

	/**
	 * Node paramters
	 */
	private String nodeName;//Node name    
	private List<NWInterface> interfaceList = new ArrayList<NWInterface>();
	private boolean visited = false;

	private boolean isRouter;
	//private RoutingTable routingTable;//Routing table of this node
	private RoutingTable routingTable;//Routing table of this node

	//Create layers for this node
	private TransportLayer transportLayer = new TransportLayer(this);
	private NetworkLayer networkLayer = new NetworkLayer(this);
	private DataLinkLayer dataLinkLayer = new DataLinkLayer(this);
	private PhysicalLayer physicalLayer = new PhysicalLayer(this);

	//List of transmissions of this node
	private List<Transmission> transmissionList = new ArrayList<Transmission>();

	//Generated file name to which received file will be written
	private String outputFilename;

	private NWInterface activeInterface;


	/**<BR>
	 * <strong>Node Method.</strong><BR>
	 * Usage: constructor to create node object and<BR>
	 * assign node name, ipAddress and mac address.<BR> 
	 * @param nodeName
	 * @param nwAddress
	 * @param macAddress
	 */    
	public Node(String nodeName) {
		this.nodeName = nodeName;
	}    


	/**<BR>
	 * <strong>run Method.</strong><BR>
	 * Usage: Key method of Node thread. It performs following operations<BR>
	 * 1. Sends the input file to top layer on the this node's layer stack
	 * 2. Invokes a receive method to receive data from medium to this node's physical layer. 
	 */    
/*	public void run() {

		LOGGER.log(Level.INFO, this.toString()+"");
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				while (true) {
					try {
						receive(Util.blockingQueue.take(), Util.blockingQueue);
					} catch (InterruptedException e) {
						LOGGER.log(Level.SEVERE, "InterruptedException while picking packet from Blocking Queue", e);
					} catch (Exception e) {
						LOGGER.log(Level.SEVERE, "Exception while picking packet from Blocking Queue", e);
					}
				}
			}
		};

		Thread thread = new Thread(runnable);
		//thread.start();
	}*/


	/**<BR>
	 * <strong>receive Method.</strong><BR>
	 * Usage: Receives data from the medium<BR> 
	 * @param message
	 * @param blockingQueue
	 * 
	 */    
	public void receive(Packet packet, BlockingQueue<Packet> blockingQueue) throws Exception{
		physicalLayer.receiveFromMedium(packet, blockingQueue, System.currentTimeMillis());
	}

	/**<BR>
	 * <strong>receive Method.</strong><BR>
	 * Usage: Receives data from the medium<BR> 
	 * @param message
	 * @param blockingQueue
	 * 
	 */    
	public void receive(final Packet packet) throws Exception{
		Runnable runnable = new Runnable() {
			@Override
			public void run() {
				try {
					long currTime = System.currentTimeMillis();
					long diff = System.currentTimeMillis() - FPMain.START_TIME;
					physicalLayer.receiveFromMedium(packet, Util.blockingQueue, diff);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		};
		
		Thread t = new Thread(runnable);
		t.start();

	}

	/**<BR>
	 * <strong>receiveFromTransportLayer Method.</strong><BR>
	 * Usage: Receives data from Transport layer<BR> 
	 * @param data
	 * 
	 */
	public void receiveFromTransportLayer(NetworkLayerPacket networkLayerPacket) {

		//LOGGER.log(Level.INFO, "Node-"+nodeName+ " --> Received data from Transport Layer");
		byte[] flagsOffset = networkLayerPacket.flagsOffset;

		//Convert the Byte array to decimal value
		//long offsetDec = 0;
		boolean flag = false;
		if ((int)(flagsOffset[1] & (byte)0x20) != 0){
			//Flag is set
			flag = true;
		}
		int offset = Util.byteArrToInt13(flagsOffset);
		//String filename = Constants.OP_FILES_PATH+Constants.A2_OP_FILE_NAME_PREFIX+LoggingService.
		//	fileNameformatter.format(new Date())+Constants.A2_OP_FILE_NAME_SUFFIX;
		//String filename = "outputfile_"+Main.formatter.format(new Date());

		if (flag == false && offset == 0){	//Unfragmented Packet

			writeChunkToFile(networkLayerPacket,false);
			LOGGER.log(Level.INFO, "Node-"+nodeName+ " --> Completed writing to the output file");

		}
		if (flag == true && offset == 0){	//The 1st packet (Fragmented)

			//create the file, and write the 1st packet (append)
			writeChunkToFile(networkLayerPacket,false);

		}
		if (flag == true && offset > 0){	//The Nth packet (Fragmented)

			//open the file, and write the Nth packet (append)
			writeChunkToFile(networkLayerPacket,true);
		}
		if (flag == false && offset > 0){	//The last Packet (Fragmented)
			//open the file, and write the last packet (append)
			writeChunkToFile(networkLayerPacket,true);
			LOGGER.log(Level.INFO, "Node-"+nodeName+ " --> Completed writing to the output file");

		}
	}

	/**<BR>
	 * <strong>sendToTransportLayer Method.</strong><BR>
	 * Usage: sends data and destination address to Transport layer.<BR> 
	 * @param filePath
	 * @param destAddress
	 */
	public void sendToTransportLayer(Transmission transmission, byte[] destAddress) throws Exception{
		BufferedReader bufferedReader = null;
		try {
			bufferedReader = new BufferedReader(new FileReader(transmission.getFilePath()));
			StringBuffer stringBuffer = new StringBuffer();
			String line = null;
			while((line = bufferedReader.readLine()) != null) {
				stringBuffer.append(line.trim()+"\n");
			}

			byte[] fileData = stringBuffer.toString().getBytes();
			LOGGER.log(Level.INFO, transmission.getTimeStamp()+"\t"+transmission.getSourceNode()+"\tStarts JOB "+fileData.length+" KB");
			this.transportLayer.receiveFromASPLayer(transmission.getTimeStamp(), fileData, destAddress);
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "IOException", e);
		} catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception", e);
		} finally {
			if(bufferedReader != null)
				try {
					bufferedReader.close();
				} catch (IOException e) {}
		}

	}


	/**<BR>
	 * <strong>writeChunkToFile Method.</strong><BR>
	 * Usage: Writes data to a output file.<BR> 
	 * @param NetworkLayerPacket  
	 * @param append
	 */    
	private void writeChunkToFile(NetworkLayerPacket frame, boolean append) {
		File file;
		FileOutputStream  out = null;
		try {
			if (!append){

				this.outputFilename=  Constants.OP_FILES_PATH+Constants.A2_OP_FILE_NAME_PREFIX+LoggingService.
						fileNameformatter.format(new Date())+Constants.A2_OP_FILE_NAME_SUFFIX;

				LOGGER.log(Level.INFO, "Node-"+nodeName+ " --> Started writing first fragment received to new output file "+outputFilename);

				file = new File(outputFilename);
				file.createNewFile();
				out = new FileOutputStream(file);
			}else{
				LOGGER.log(Level.INFO, "Node-"+nodeName+ " --> Appending the next received fragment to output file "+outputFilename);
				file = new File(outputFilename);
				out = new FileOutputStream(file, true);
			}
			out.write(frame.body);
			out.flush();
			out.close();
		} catch (IOException e) {
			LOGGER.log(Level.SEVERE, "IOException while writing data to file", e);
			System.err.println("Node: "+getNodeName());
			e.printStackTrace();
		}catch (Exception e) {
			LOGGER.log(Level.SEVERE, "Exception while writing data to file", e);
			System.err.println("Node: "+getNodeName());
			e.printStackTrace();
		}


	}

	public String getNodeName() {
		return nodeName;
	}

	public boolean isRouter() {
		return isRouter;
	}

	public void addTransmission(Transmission transmission) {
		this.transmissionList.add(transmission);
	}

	public List<Transmission> getTransmissionList() {
		return transmissionList;
	}

	/**
	 * @return the activeInterface
	 */
	public NWInterface getActiveInterface() {
		return activeInterface;
	}

	/**
	 * @param activeInterface the activeInterface to set
	 */
	public void setActiveInterface(NWInterface activeInterface) {
		this.activeInterface = activeInterface;
	}


	public void addInterface(NWInterface Iface) {
		this.interfaceList.add(Iface);
		if(this.interfaceList.size() > 1) {
			isRouter = true;
		}
	}

	public List<NWInterface> getInterfaceList() {
		return interfaceList;
	}
	public String getConnectedInterface(String mediumName) throws Exception{
		for(NWInterface nwInterface: interfaceList){
			Medium medium = nwInterface.getMedium();
			if((medium.getName()).equalsIgnoreCase(mediumName))
				return (String)nwInterface.getiFaceName();
		}
		return null;
	}
	public byte[] getIpAddress(String mediumName) throws Exception{
		for(NWInterface nwInterface: interfaceList){
			if(((Medium)nwInterface.getMedium()).getName().equalsIgnoreCase(mediumName))
				return (byte[])nwInterface.getIpAddress();
		}
		return null;
	} 
	/**
	 * @return the routingTable
	 */
	 public RoutingTable getRoutingTable() {
		 return routingTable;
	 }

	 /**
	  * @param routingTable the routingTableNew to set
	  */
	 public void setRoutingTable(RoutingTable routingTable) {
		 this.routingTable = routingTable;
	 }

	 public TransportLayer getTransportLayer() {
		 return transportLayer;
	 }

	 public NetworkLayer getNetworkLayer() {
		 return networkLayer;
	 }

	 public DataLinkLayer getDataLinkLayer() {
		 return dataLinkLayer;
	 }

	 public PhysicalLayer getPhysicalLayer() {
		 return physicalLayer;
	 }

	 @Override
	 public String toString() {
		 return "Node [nodeName=" + nodeName + ", interfaceList="
				 + interfaceList + "]";
	 }


	 /**<BR>
	  * <strong>RoutingTable Method.</strong><BR>
	  * Usage: This inner class maintains the routing table of this node.<BR> 
	  */ 

	 /* public class RoutingTable {

        private HashMap<ByteBuffer, byte[]> routerConfiguration = new HashMap<ByteBuffer, byte[]>();

        public void addConfiguration(byte[] ipAddress, byte[] gatewayAddress) {
            routerConfiguration.put(ByteBuffer.wrap(ipAddress), gatewayAddress);
        }

        public byte[] getGatewayAddressByIpAddress(byte[] ipAddress) {
            if(routerConfiguration.containsKey(ByteBuffer.wrap(ipAddress)))
                return routerConfiguration.get(ByteBuffer.wrap(ipAddress));
            return null;
        }
    }*/

	 public class RoutingTable {

		 private List<Route> routingTable = new ArrayList<Route>();

		 public void addRoute(byte[] ipAddress,byte[] mask, Object nextHop, int metric) {
			 routingTable.add(new Route(ipAddress,mask,nextHop,metric));
		 }


		 //Forwarding Algorithm
		 public byte[] getNextHop(byte[] dstIPAddress) throws Exception{

			 Route bestRoute = null;
			 for(Route route: routingTable){
				 byte[] dstNWAddress = Util.calculateNWAddress(dstIPAddress,route.getMask());
				 if(Arrays.equals(route.getNwAddress(),dstNWAddress)){

					 if(route.getNextHop() instanceof String){
						 return dstIPAddress;
					 }else{
						 if(bestRoute==null) bestRoute = route;
						 else{
							 if(bestRoute.getMetric()>route.getMetric())
								 bestRoute = route;
						 }
					 }
				 }
			 }
			 if(bestRoute==null) return null;
			 else return (byte[])bestRoute.getNextHop();

		 }        

	 }

	 /**
	  * @return the visited
	  */
	 public boolean isVisited() {
		 return visited;
	 }

	 /**
	  * @param visited the visited to set
	  */
	 public void setVisited(boolean visited) {
		 this.visited = visited;
	 }

}
