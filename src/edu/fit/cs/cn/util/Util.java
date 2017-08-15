package edu.fit.cs.cn.util;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.PriorityBlockingQueue;

import edu.fit.cs.cn.link.Medium;
import edu.fit.cs.cn.node.Node;

/**
 * <b>Util.java</b><BR>
 * Usage: This class maintains the node map and transmission queue.<BR> 
 * Also offers methods to convert data from int to byte and vice versa.<BR>
 * @author Group7
 */
public class Util {
	
	//Logger to write the key messages of the project execution status to a log file
	//private static final Logger LOGGER = LoggingService.getLogger(Constants.CN_PROJECT_LOGGER);

	//Node map to store the nodes in the network configuration
    public static Map<String, Node> nodeMap = new HashMap<String, Node>();
    
    //Node Visit 
    public static Map<String, Boolean> nodeVisitMap = new HashMap<String, Boolean>();
        
    //Medium map to store the mediums in the network configuration
    public static Map<String, Medium> mediumMap = new HashMap<String, Medium>();
    
    //Transmission Queue
    public static BlockingQueue<Packet> blockingQueue = new PriorityBlockingQueue<Packet>(100);
    
    final protected static char[] hexArray = "0123456789ABCDEF".toCharArray();
    
    /**<BR>
     * 
	 * <strong>intToByteArr</strong><BR>
	 * Usage: Converts integer value to byte array.<BR> 
	 * @param int
	 * @return byte array
	 * 
	 */
    public static byte[] intToByteArr(int n) {
        byte[] arr = new byte[2];
        int i = 0;
        while (n > 0 && i < 2) {
            arr[i] = (byte) (n % 256);
            n = n >> 8;
            i++;
        }

        return arr;
    }
    
    /**<BR>
     * 
	 * <strong>byteArrToInt13</strong><BR>
	 * Usage: Converts byte array back to integer.<BR> 
	 * @param byte array
	 * @return int
	 * 
	 */
    public static int byteArrToInt13(byte[] arr) {
        int i = arr[1] & 0x1F;
        i = i << 8;
        if (arr[0] < 0)
            i |= 256 + arr[0];
        else
            i |= arr[0];
        return i;
    }
    
    /**<BR>
     * 
	 * <strong>bytesToHex</strong><BR>
	 * Usage: Converts byte array back to Hex.<BR> 
	 * @param byte array
	 * @return String
	 * 
	 */
    public static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
    
    
    /**
     * Convert raw IP address to string.
     *
     * @param rawBytes raw IP address.
     * @return a string representation of the raw ip address.
     */
    public static String getIpAddressInStringFormat(byte[] ipAdressByteArr) {
        int i = 4;
        StringBuilder ipAddress = new StringBuilder(18);
        for (byte raw : ipAdressByteArr)
        {
        	ipAddress.append(raw & 0xFF);
            if (--i > 0)
            {
            	ipAddress.append(".");
            }
        }
 
        return ipAddress.toString();
    }   
    /**
     * Convert raw Mac address to string.
     *
     * @param rawBytes raw Mac address.
     * @return a string representation of the raw Mac address.
     */
    public static String getMacAddressInStringFormat(byte[] macAdressByteArr) {
        int i = 6;
        StringBuilder macAddress = new StringBuilder(18);
        for (byte raw : macAdressByteArr)
        {
        	macAddress.append(String.format("%02x", raw));
        	
            if (--i > 0)
            {
            	macAddress.append(":");
            }
        }
 
        return macAddress.toString();
    }    
    /**
     * Calculates NW address of a subnet
     *
     * @param aIPAddress - A IP address of in the subnet.
     * @return mask - Mask of the medium. 
     */
    public static byte[] calculateNWAddress(byte[] aIPAddress,byte[] mask) throws Exception{
    	
        byte[] nwAddress = new byte[4];
        
        for (int index=0;index<aIPAddress.length;index++)
        {
        	nwAddress[index] = (byte)(aIPAddress[index] & mask[index]);
        	
        }
 
        return nwAddress;
    }
    public static void resetNodesVisitStatus() throws Exception{
    	nodeVisitMap.clear();
    	for(String nodeName : nodeMap.keySet()){
    		nodeVisitMap.put(nodeName, new Boolean(false));
    	}
    }
    public static void setNodeVisited(String nodeName) throws Exception{
    	nodeVisitMap.put(nodeName,new Boolean(true));
    }  
    public static boolean isNodeVisited(String nodeName) throws Exception{
    	return ((Boolean)nodeVisitMap.get(nodeName)).booleanValue();
    }  

}
