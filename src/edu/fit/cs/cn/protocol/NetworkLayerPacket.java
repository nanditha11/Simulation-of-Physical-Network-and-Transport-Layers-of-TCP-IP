package edu.fit.cs.cn.protocol;

import java.util.logging.Level;
import java.util.logging.Logger;

import edu.fit.cs.cn.util.Constants;
import edu.fit.cs.cn.util.LoggingService;
import edu.fit.cs.cn.util.Util;

/**
 * <b>NetworkLayerPacket.java</b><BR>
 * Usage: This class is a template of the Network Layer packet.<BR> 
 * 
 * @author Group7
 */
public class NetworkLayerPacket {

	private static final Logger LOGGER = LoggingService.getLogger(Constants.CN_PROJECT_LOGGER);
	
	//Size of the packet header
    public static final int HEADER_SIZE = 1 + 1 + 2 + 2 + 2 + 1 + 1 + 2 + 4 + 4;
    
    /**
     * Packet parameters and their size 
     */
    private static final int VERSION_HLEN_SIZE = 1;//Version size
    private static final int TOS_SIZE = 1;//Type of Service flag size    
    private static final int LENGTH = 2;
    private static final int IDENT_SIZE = 2;
    private static final int FLAGS_OFFSET_SIZE = 2;
    private static final int TTL_SIZE = 1;
    private static final int PROTOCOL_SIZE = 1;
    private static final int CHECKSUM_SIZE = 2;
    private static final int SOURCE_ADDRESS_SIZE = 4;
    private static final int DESTINATION_ADDRESS_SIZE = 4;
    private static final int MIN_BODY_SIZE = 46;    
    public byte[] versionHLen = new byte[VERSION_HLEN_SIZE];
    public byte[] tos = new byte[TOS_SIZE];
    public byte[] length = new byte[LENGTH];
    public byte[] ident = new byte[IDENT_SIZE];
    public byte[] flagsOffset = new byte[FLAGS_OFFSET_SIZE];
    public byte[] ttl = new byte[TTL_SIZE];
    public byte[] protocol = new byte[PROTOCOL_SIZE];
    public byte[] checksum = new byte[CHECKSUM_SIZE];
    public byte[] srcIpAddress = new byte[SOURCE_ADDRESS_SIZE];
    public byte[] destIpAddress = new byte[DESTINATION_ADDRESS_SIZE];
    public byte[] body = new byte[MIN_BODY_SIZE];

    public NetworkLayerPacket() {

    }
    /**<BR>
	 * <strong>NetworkLayerPacket Method.</strong><BR>
	 * Usage: Constructor to build network packet using raw input data<BR>
	 * @param data
	 *  
     */
    public NetworkLayerPacket(byte[] data) {
        this();

        int i = 0;
        // Initialize version and header length
        for (int j = 0; j < VERSION_HLEN_SIZE; i++, j++)
            versionHLen[j] = data[i];
        // Initialize TOS
        for (int j = 0; j < TOS_SIZE; i++, j++)
            tos[j] = data[i];
        // Initialize Length field
        for (int j = 0; j < LENGTH; i++, j++)
            length[j] = data[i];
        // Initialize Ident field
        for (int j = 0; j < IDENT_SIZE; i++, j++)
            ident[j] = data[i];
        // Initialize Flags and Offset fields
        for (int j = 0; j < FLAGS_OFFSET_SIZE; i++, j++)
            flagsOffset[j] = data[i];
        // Initialize TTL field
        for (int j = 0; j < TTL_SIZE; i++, j++)
            ttl[j] = data[i];
        // Initialize Protocol field
        for (int j = 0; j < PROTOCOL_SIZE; i++, j++)
            protocol[j] = data[i];
        // Initialize Checksum field
        for (int j = 0; j < CHECKSUM_SIZE; i++, j++)
            checksum[j] = data[i];
        // Initialize Source Address field
        for (int j = 0; j < SOURCE_ADDRESS_SIZE; i++, j++)
            srcIpAddress[j] = data[i];
        // Initialize Destination Address field
        for (int j = 0; j < DESTINATION_ADDRESS_SIZE; i++, j++)
            destIpAddress[j] = data[i];

        // Initialize body
        body = new byte[data.length - i > 0 ? data.length - i : 0];
        for (int j = 0; i < data.length && j < body.length; i++, j++)
            body[j] = data[i];

    }

    /**<BR>
	 * <strong>toByteArray Method.</strong><BR>
	 * Usage: Converts and returns the packet in byte array format. <BR>
	 * @returns data in byte[] array format
	 *  
     */    
    public byte[] toByteArray() {
    	
        // The continuous byte array that will be eventually written on the line
        byte[] byteArr = new byte[HEADER_SIZE + body.length];

        int i = 0;
        for (int j = 0; j < versionHLen.length; i++, j++)
            byteArr[i] = versionHLen[j];
        for (int j = 0; j < tos.length; i++, j++)
            byteArr[i] = tos[j];
        for (int j = 0; j < length.length; i++, j++)
            byteArr[i] = length[j];
        for (int j = 0; j < ident.length; i++, j++)
            byteArr[i] = ident[j];
        for (int j = 0; j < flagsOffset.length; i++, j++)
            byteArr[i] = flagsOffset[j];
        for (int j = 0; j < ttl.length; i++, j++)
            byteArr[i] = ttl[j];
        for (int j = 0; j < protocol.length; i++, j++)
            byteArr[i] = protocol[j];
        for (int j = 0; j < checksum.length; i++, j++)
            byteArr[i] = checksum[j];
        for (int j = 0; j < srcIpAddress.length; i++, j++)
            byteArr[i] = srcIpAddress[j];
        for (int j = 0; j < destIpAddress.length; i++, j++)
            byteArr[i] = destIpAddress[j];
        for (int j = 0; j < body.length; i++, j++)
            byteArr[i] = body[j];

        return byteArr;
    }
    
    public void printPacket() {
    	byte[] bLen = {(byte)body.length};
    	byte[] cLen = {(byte) checksum.length};
    	LOGGER.log(Level.INFO, "\t\t------------------------------------------------------------------------------------");
    	LOGGER.log(Level.INFO, "\t\t|"+Util.bytesToHex(versionHLen)+"|"+Util.bytesToHex(tos)+"|"+Util.bytesToHex(bLen)+"|");
    	LOGGER.log(Level.INFO, "\t\t------------------------------------------------------------------------------------");
    	LOGGER.log(Level.INFO, "\t\t|"+Util.bytesToHex(ident)+"|"+Util.bytesToHex(flagsOffset)+"|");
    	LOGGER.log(Level.INFO, "\t\t------------------------------------------------------------------------------------");
    	LOGGER.log(Level.INFO, "\t\t|"+Util.bytesToHex(ttl)+"|"+Util.bytesToHex(protocol)+"|"+Util.bytesToHex(cLen)+"|");
    	LOGGER.log(Level.INFO, "\t\t------------------------------------------------------------------------------------");
    	LOGGER.log(Level.INFO, "\t\t|"+Util.bytesToHex(srcIpAddress)+"|");
    	LOGGER.log(Level.INFO, "\t\t------------------------------------------------------------------------------------");
    	LOGGER.log(Level.INFO, "\t\t|"+Util.bytesToHex(destIpAddress)+"|");
    	LOGGER.log(Level.INFO, "\t\t------------------------------------------------------------------------------------");
    	LOGGER.log(Level.INFO, "\t\t|"+Util.bytesToHex(checksum)+"|");
    	LOGGER.log(Level.INFO, "\t\t------------------------------------------------------------------------------------");
    	        
    }


}
