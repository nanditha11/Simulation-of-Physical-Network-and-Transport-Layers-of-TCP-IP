package edu.fit.cs.cn.util;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
/**
 * <b>ARPTable.java</b><BR>
 * Usage: Class to store the ARP table in <IPAddress,MacAddress> key value format.<BR> 
 * 
 * @author Group7
 */
public class ARPTable {

	//ARP table to store <IPAddress,MacAddress> mapping
    private static Map<byte[], byte[]> arpTableMap = new HashMap<byte[], byte[]>();

	/**<BR>
	 * <strong>addEntry Method.</strong><BR>
	 * Usage: Adds a new entry<IPAddress,MacAddress> to the ARP table<BR>
	 * @param <IPAddress,MacAddress> 
	 * @return void
	 *  
     */    
    public static void addEntry(byte[] ipAddres, byte[] macAddress) {
        arpTableMap.put(ipAddres, macAddress);
    }

    /**<BR>
	 * <strong>getMacAddressByIpAddress Method.</strong><BR>
	 * Usage: Searches for mac address by ip address<BR>
	 * @param ipAddress
	 * @return macAddress
	 *  
     */    
    public static byte[] getMacAddressByIpAddress(byte[] ipAddress) {
        for(byte[] ip : arpTableMap.keySet()) {
            if(Arrays.equals(ip, ipAddress)) {
                return arpTableMap.get(ip);
            }
        }
        return null;
    }
}
