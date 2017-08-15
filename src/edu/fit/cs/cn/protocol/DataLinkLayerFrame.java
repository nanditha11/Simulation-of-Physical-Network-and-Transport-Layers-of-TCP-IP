package edu.fit.cs.cn.protocol;

import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.CRC32;

import edu.fit.cs.cn.util.Constants;
import edu.fit.cs.cn.util.LoggingService;
import edu.fit.cs.cn.util.Util;
/**
 * <b>DataLinkLayerFrame.java</b><BR>
 * Usage: This class is a template of the Data Link Layer frame.<BR> 
 * 
 * @author Group7
 */
public class DataLinkLayerFrame {
	
	//Logger to write the key messages of the project execution status to a log file	
	private static final Logger LOGGER = LoggingService.getLogger(Constants.CN_PROJECT_LOGGER);


    /**
     * Frame parameters and their size 
     */
	
    private static final int CRC_SIZE_4 = 4;
    //the minimum allowed size of the body is 46bytes.
    private static final int MIN_BODY_SIZE_46 = 46;
    private static final int TYPE_SIZE_2 = 2;
    private static final int ADDRESS_SIZE_6 = 6;
    private static final int PREAMBLE_SIZE_8 = 8;
    // preamble is an alternating sequence of 0 and 1 bits
    final byte[] preamble = new byte[PREAMBLE_SIZE_8];
    public byte[] destMacAddress = new byte[ADDRESS_SIZE_6];
    public byte[] srcMacAddress = new byte[ADDRESS_SIZE_6];
    public byte[] type =  new byte[TYPE_SIZE_2];
    //the minimum allowed size of the body is 46bytes.
    public byte[] body = new byte[MIN_BODY_SIZE_46];
    public byte[] crc =  new byte[CRC_SIZE_4];

    public static final int HEADER_N_CRC_SIZE = 8 + 6 + 6 + 2 + 4;

    public DataLinkLayerFrame() {
    	
        // initialize preamble to an alternating 0 1 sequence
        for(int i = 0; i < preamble.length; i++)
            preamble[i] = (byte) 0xAA;
    }

    public DataLinkLayerFrame(byte[] data) {
        this();
        
        // TODO what should be done in the case where data.length is less that HEADER_N_CRC_SIZE

        int i = 0;
        //Initialize preamble
        for(int j = 0; j < PREAMBLE_SIZE_8; i++, j++)
            preamble[j] = data[i];
        //Initialize destination address
        for(int j = 0; j < ADDRESS_SIZE_6; i++, j++)
            destMacAddress[j] = data[i];
        //Initialize destination address
        for(int j = 0; j < ADDRESS_SIZE_6; i++, j++)
            srcMacAddress[j] = data[i];
        //Initialize destination address
        for(int j = 0; j < TYPE_SIZE_2; i++, j++)
            type[j] = data[i];
        //Initialize body
        int bodySize = data.length - HEADER_N_CRC_SIZE > 0 ? data.length - HEADER_N_CRC_SIZE : 0;
        body = new byte[bodySize];
        for(int j = 0; j < body.length; i++, j++)
            body[j] = data[i];
        // Initialize crc
        for(int j = 0; j < CRC_SIZE_4; i++, j++)
            crc[j] = data[i];
    }

    public void setCrc(byte[] crc) {
        this.crc = crc;
    }

    public void calculateAndSetCRC() {
        setCrc(calculateCRC());
    }

    private byte[] toByteArrayWithoutCRC() {
        byte[] byteArr = new byte[HEADER_N_CRC_SIZE - CRC_SIZE_4 + body.length];

        int i = 0;
        //Initialize preamble
        for(int j = 0; j < preamble.length; i++, j++)
            byteArr[i] = preamble[j];
        for (int j = 0; j < destMacAddress.length; i++, j++)
            byteArr[i] = destMacAddress[j];
        for (int j = 0; j < srcMacAddress.length; i++, j++)
            byteArr[i] = srcMacAddress[j];
        for(int j = 0; j < type.length; i++, j++)
            byteArr[i] = type[j];
        for(int j = 0; j < body.length; i++, j++)
            byteArr[i] = body[j];
        return byteArr;
    }

    private byte[] calculateCRC() {
        CRC32 crc32 = new CRC32();
        crc32.update(this.toByteArrayWithoutCRC());
        long c = crc32.getValue();
        // long to 4 byte, since crc32 uses only LSB 4 bytes of long.
        byte[] crcArr = new byte[CRC_SIZE_4];

        // MSB out of 4 bytes in c stored in crcArr[0]
        for(int i = crcArr.length-1; i >= 0; i--) {
            crcArr[i] = (byte) c;
            c = c >> 8;
        }

        return crcArr;
    }

    public boolean verifyCRC() {
        byte[] calc = calculateCRC();

        if(calc.length != crc.length)
            return false;

        for(int i = 0; i < CRC_SIZE_4; i++)
            if(calc[i] != crc[i])
                return false;

        return true;
    }

    public byte[] toByteArray() {
        byte[] byteArr = new byte[HEADER_N_CRC_SIZE + body.length];

        int i = 0;
        //Initialize preamble
        for(int j = 0; j < preamble.length; i++, j++)
            byteArr[i] = preamble[j];
        for (int j = 0; j < destMacAddress.length; i++, j++)
            byteArr[i] = destMacAddress[j];
        for (int j = 0; j < srcMacAddress.length; i++, j++)
            byteArr[i] = srcMacAddress[j];
        for(int j = 0; j < type.length; i++, j++)
            byteArr[i] = type[j];
        for(int j = 0; j < body.length; i++, j++)
            byteArr[i] = body[j];
        for(int j = 0; j < crc.length; i++, j++)
            byteArr[i] = crc[j];

        return byteArr;
    }

	/**
	 * 
	 */
	public void printPacket() {
		LOGGER.log(Level.INFO, "\t\t\t------------------------------------------------------------------------------------");
		LOGGER.log(Level.INFO, "\t\t\t|\t"+Util.bytesToHex(srcMacAddress)+"|");
		LOGGER.log(Level.INFO, "\t\t\t------------------------------------------------------------------------------------");
		LOGGER.log(Level.INFO, "\t\t\t|\t"+Util.bytesToHex(destMacAddress)+"|");
		LOGGER.log(Level.INFO, "\t\t\t------------------------------------------------------------------------------------");
		LOGGER.log(Level.INFO, "\t\t\t|\t"+Util.bytesToHex(type)+"|");
		LOGGER.log(Level.INFO, "\t\t\t-----------------------------------------------");
	}

}
