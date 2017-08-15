package edu.fit.cs.cn.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Stack;
import java.util.logging.Level;
import java.util.logging.Logger;

import edu.fit.cs.cn.entities.Transmission;
import edu.fit.cs.cn.link.Medium;
import edu.fit.cs.cn.main.FPMain;
import edu.fit.cs.cn.node.Node;
import edu.fit.cs.cn.node.Node.RoutingTable;


/**
 * <b>FPConfigLoader.java</b><BR>
 * Usage: This class performs following functionalities:<BR>
 * 1. Parses the config file
 * 2. Creates Node, Medium(Subnet) and records data transmission
 * 3. Builds routing table for all the nodes.
 * @author Group7
 */
public class FPConfigLoader {

	private static final Logger LOGGER = LoggingService.getLogger(Constants.CN_PROJECT_LOGGER);
	
	public void load(String filePath) throws Exception{

		File configFile=null;
		String currentSection = null;
		Node node = null;
        String line = null;
        int lineNumber = 0;
        String[] arr = null;
		int iFaceNum = 0;
		BufferedReader bufferedReader = null;
		try{
			configFile = new File(filePath);
			bufferedReader = new BufferedReader(new FileReader(configFile));
            
    		LOGGER.log(Level.INFO, "Started reading from configuration file ..");
            //Loop through each line of the file
            while((line = bufferedReader.readLine()) != null){
            	            	
            	lineNumber++;
            	//check if empty line or starts with comment
            	if(line.trim().equals("") || line.startsWith("#")) continue;
            	//check if line contains comment
            	if(line.contains("#")){
            		line = line.substring(0,line.indexOf("#"));
            	}
            	
            	if(line.startsWith(Constants.SEC_MEDIUM_NAME+"_START")){
            		LOGGER.log(Level.INFO, "Started reading mediums from configuration file ..");
            		currentSection = Constants.SEC_MEDIUM_NAME;
            	}else if(line.startsWith(Constants.SEC_MEDIUM_NAME+"_END")){
            		LOGGER.log(Level.INFO, "Completed reading mediums from configuration file.");
            	}else if(line.startsWith(Constants.SEC_NODE_NAME+"_START")){
            		LOGGER.log(Level.INFO, "Started reading nodes from configuration file ..");
            		currentSection = Constants.SEC_NODE_NAME;
            	}else if(line.startsWith(Constants.SEC_NODE_NAME+"_END")){
            		LOGGER.log(Level.INFO, "Completed reading nodes from configuration file.");
            	}else if(line.startsWith(Constants.SEC_DATA_NAME+"_START")){
            		LOGGER.log(Level.INFO, "Started reading data from configuration file ..");
            		currentSection = Constants.SEC_DATA_NAME;
            	}else if(line.startsWith(Constants.SEC_DATA_NAME+"_END")){
            		LOGGER.log(Level.INFO, "Completed reading data from configuration file.");
            	}else{
            		
            		/**
            		 * Medium Section
            		 * 
            		 **/
            		if(currentSection.equalsIgnoreCase(Constants.SEC_MEDIUM_NAME)){
            			
            			//Read Medium name
            			arr = line.trim().split(":");
            			Medium medium = new Medium(arr[1].trim());
            			
            			lineNumber++;
            			
            			//Read Medium MTU from next line
            			line = bufferedReader.readLine();
            			//check if line contains comment
                    	if(line.contains("#")){
                    		line = line.substring(0,line.indexOf("#"));
                    	}
            			arr = line.trim().split(":");
            			medium.setMTU(Integer.parseInt(arr[1].trim()));
            			Util.mediumMap.put(medium.getName(), medium);
            			
            		}

            		/**
            		 * Node Section
            		 * 
            		 **/
            		else if(currentSection.equalsIgnoreCase(Constants.SEC_NODE_NAME)){

            			//Read Node name
            			arr = line.trim().split(":");
            			NWInterface nodeNWIface = new NWInterface();
            			
            			if(arr[0].trim().equalsIgnoreCase("NAME")){
            				node = new Node(arr[1].trim());
            				iFaceNum= 1;
            				continue;
            			}
            			
            			//Node interface properties
            			if(arr[0].trim().startsWith(node.getNodeName())){
            				          

            				nodeNWIface.setiFaceName("IF"+iFaceNum);
            				//Read IP Address
            				String[] tempAddress = arr[1].trim().split("\\.");
            				byte[] tempAddressByte = {(byte)Integer.parseInt(tempAddress[0]),(byte)Integer.parseInt(tempAddress[1]),
            						(byte)Integer.parseInt(tempAddress[2]),(byte)Integer.parseInt(tempAddress[3])};
            				nodeNWIface.setIpAddress(tempAddressByte);
            				
                        	//Read Mask
            				
            				lineNumber++;            				
            				line = bufferedReader.readLine();
            				
                			//check if line contains comment
                        	if(line.contains("#")){
                        		line = line.substring(0,line.indexOf("#"));
                        	}
                			arr = line.trim().split(":");
                			String[] tempMask = arr[1].trim().split("\\.");
            				byte[] tempMaskByte = {(byte)Integer.parseInt(tempMask[0]),(byte)Integer.parseInt(tempMask[1]),
            						(byte)Integer.parseInt(tempMask[2]),(byte)Integer.parseInt(tempMask[3])};
            				nodeNWIface.setMask(tempMaskByte);
            				

                        	//Read Mac
            				
            				lineNumber++;            				
            				line = bufferedReader.readLine();
            				
                			//check if line contains comment
                        	if(line.contains("#")){
                        		line = line.substring(0,line.indexOf("#"));
                        	}                        	
                			arr = line.trim().split(":");
                			String[] tempMac = arr[1].trim().split(" ");
            				byte[] tempMacByte = {(byte)Integer.parseInt(tempMac[0],16),(byte)Integer.parseInt(tempMac[1],16),
            						(byte)Integer.parseInt(tempMac[2],16),(byte)Integer.parseInt(tempMac[3],16),
            						(byte)Integer.parseInt(tempMac[4],16), (byte)Integer.parseInt(tempMac[5],16)};
            				nodeNWIface.setMacAddress(tempMacByte); 
            				ARPTable.addEntry(nodeNWIface.getIpAddress(), tempMacByte);
            				
            				//Read Band width
            				lineNumber++;            				
            				line = bufferedReader.readLine();
                			//check if line contains comment
                        	if(line.contains("#")){
                        		line = line.substring(0,line.indexOf("#"));
                        	}
                        	
                			arr = line.trim().split(":");
                			nodeNWIface.setBandWidth(Integer.valueOf(arr[1].trim()));
            				

                        	//Read connected medium
                			lineNumber++;
                			line = bufferedReader.readLine();
                			//check if line contains comment
                        	if(line.contains("#")){
                        		line = line.substring(0,line.indexOf("#"));
                        	}                        	
                			arr = line.trim().split(":");
                			
                			Medium medium = Util.mediumMap.get(arr[1].trim());
                			
                			if(medium.getNwAddress()==null){
                				
                				medium.setNwAddress(Util.calculateNWAddress(tempAddressByte, tempMaskByte));
                				nodeNWIface.setNetworkAddress(medium.getNwAddress());
                				medium.setMask(tempMaskByte);
                				//System.out.println("nw address"+Util.getIpAddressInStringFormat(medium.getNwAddress()));
                			}
                			
                			
                			nodeNWIface.setMedium(medium);
                			
                			//add the interface and its properties to node
                			node.addInterface(nodeNWIface);
                			
                			//Add the node to the medium as well
                			medium.addNode(node);
                			
                			//add node to node map
                			Util.nodeMap.put(node.getNodeName(),node);
                			
                			iFaceNum++;
            				
            			}else{
            				throw new Exception();
            			}
            			
            		}     

            		/**
            		 * data Section
            		 * 
            		 **/            		
            		else if(currentSection.equalsIgnoreCase(Constants.SEC_DATA_NAME)){
            			if(line.startsWith("#")) {
            				continue;
            			}
            			arr = line.trim().split("\t");
            			Transmission transmission = new Transmission(Long.valueOf(arr[0]), arr[1], arr[2], arr[3]);
            			FPMain.transmissionList.add(transmission);
            		}

            	}
            }
            LOGGER.log(Level.INFO, "Number of lines read:"+lineNumber);
			//System.out.println("Number of lines"+lineNumber);
            //buildRoutingTables();
			
		}catch (FileNotFoundException e) {
            LOGGER.log(Level.SEVERE, "Configuration File not found", e);
        }catch (IOException e) {
            LOGGER.log(Level.SEVERE, "Exception while loading configuration file", e);
        }catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Exception while reading configuration file", e);
            e.printStackTrace();
        }finally{
        	bufferedReader.close();
        }
		
	}
	
	public void buildRoutingTables() throws Exception{
		
		try{
			LOGGER.log(Level.INFO, "Building routing tables for all nodes ..");
			
			/**
			 * 1. Loop through each node V in the node map
			 */
			Node nodeV= null;
			RoutingTable nodeVRoutingTable = null;
			Medium medium = null;
			String iFace = null;
			byte[] nodeAdjIPAddress =null;
			int metric;
			for(String nodeName:Util.nodeMap.keySet()){
				
				LOGGER.log(Level.INFO, "Routing table of Node:"+nodeName);
				LOGGER.log(Level.INFO, "Network Address\tMask\t\tNext Hop\tMedium(Subnet)\tMetric");
				nodeV = Util.nodeMap.get(nodeName);
				nodeVRoutingTable = nodeV.new RoutingTable();
				
				/**
				 * 2. Loop through each medium
				 */
				for(String mediumName: Util.mediumMap.keySet()){
					
					medium = (Medium)Util.mediumMap.get(mediumName);
	
					//If node is directly connected to the medium 
					//then return the interface
					iFace = nodeV.getConnectedInterface(mediumName);
					
					if(iFace!=null){
						//zero hops as it is directly connected
						nodeVRoutingTable.addRoute(medium.getNwAddress(),medium.getMask(),iFace,0);
						LOGGER.log(Level.INFO, Util.getIpAddressInStringFormat(medium.getNwAddress())
								+"\t"+Util.getIpAddressInStringFormat(medium.getMask())
								+"\t"+iFace
								+"\t\t"+mediumName
								+"\t\t"+0);
					}
					//Else search for a route between nodeV and medium
					else{
						
						//nodeAdjIPAddressList = searchRoute(nodeV,medium);
						for(Map<String,Object> adjNodeMap: searchRoute(nodeV,medium)){
							nodeAdjIPAddress = (byte[])adjNodeMap.get(Constants.IP);
							metric = (int)adjNodeMap.get(Constants.METRIC);
							if(nodeAdjIPAddress!=null){
								nodeVRoutingTable.addRoute(medium.getNwAddress(),medium.getMask(),nodeAdjIPAddress,metric);
								LOGGER.log(Level.INFO, Util.getIpAddressInStringFormat(medium.getNwAddress())
									+"\t"+Util.getIpAddressInStringFormat(medium.getMask())
									+"\t"+Util.getIpAddressInStringFormat(nodeAdjIPAddress)
									+"\t"+mediumName
									+"\t\t"+metric);
							}
						}
					}
					
				}
				//set the routing table to node
				nodeV.setRoutingTable(nodeVRoutingTable);
			}
		}catch(Exception e){
			LOGGER.log(Level.SEVERE, "Exception in building routing tables", e);
            e.printStackTrace();
		}
	}
	private List<Map<String,Object>> searchRoute(Node srcNode, Medium dstMedium) throws Exception{
		
		List<Map<String,Object>> adjNodeIpAddressList = new ArrayList<Map<String,Object>>();
		Map<String,Object> adjNodeMap = null;
		Medium srcMedium = null;
		int numHops = 0;
		
		//Loop through each interface of source node
		for(NWInterface nwIface: srcNode.getInterfaceList()){
			
			srcMedium = nwIface.getMedium();
			
			//Loop through all the nodes of this interface/medium
			for(Node node:srcMedium.getRegisteredNodeList()){
				
				Util.resetNodesVisitStatus();
				Util.setNodeVisited(srcNode.getNodeName());
				numHops =  dfs(node,dstMedium);
				if(numHops>0){
					//adjNode = node;
					adjNodeMap = new HashMap<String, Object>();
					adjNodeMap.put(Constants.IP,node.getIpAddress(srcMedium.getName()));
					adjNodeMap.put(Constants.METRIC,numHops);
					adjNodeIpAddressList.add(adjNodeMap);
					//found = true;
					//break;
				}
			}
		}
	
		Util.resetNodesVisitStatus();
		return adjNodeIpAddressList;
	}	
	
	private int dfs(Node srcNode, Medium dstMedium) throws Exception{
	
		if(srcNode.getConnectedInterface(dstMedium.getName())!=null) return 1;
		else if(Util.isNodeVisited(srcNode.getNodeName())) return -1;
		else{
			Node childNode = null;
			Stack<Node> stack = new Stack<Node>();
			stack.push(srcNode);
			Util.setNodeVisited(srcNode.getNodeName());
			int numHops = 1;
			while(!stack.isEmpty()){
				
				childNode = getUnvisitedChildNode(stack.peek());
	
				if(childNode!=null){
					numHops++;
					if(childNode.getConnectedInterface(dstMedium.getName())!=null) return numHops;
					stack.push(childNode);
					Util.setNodeVisited(childNode.getNodeName());
				}else{
					numHops--;
					stack.pop();
				}
				
			}
			return -1;
		}
	}
	private Node getUnvisitedChildNode(Node srcNode) throws Exception
	{
		Medium srcMedium = null;
		
		for(NWInterface nwIface: srcNode.getInterfaceList()){
			
			srcMedium = nwIface.getMedium();
			
			for(Node node:srcMedium.getRegisteredNodeList()){
				
				if(!Util.isNodeVisited(node.getNodeName())){
					return node;
				}
			}
		}
	
	   return null;
	}


}
