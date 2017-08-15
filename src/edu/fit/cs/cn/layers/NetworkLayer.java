package edu.fit.cs.cn.layers;

import java.nio.ByteBuffer;
import java.util.Arrays;

import edu.fit.cs.cn.node.Node;
import edu.fit.cs.cn.protocol.NetworkLayerPacket;
import edu.fit.cs.cn.util.NWInterface;
import edu.fit.cs.cn.util.Util;

/**
 * <b>NetworkLayer.java</b><BR>
 * Usage: This class is a implementation of a NetworkLayer.<BR> 
 * It performs following functionalities<BR> 
 * 1. Receives data from its upper layer i.e, Transport Layer<BR>
 * 2. Constructs NetworkLayerPacket by appending source ip and destination ip to the the received data.<BR>
 * 3. Sends NetworkLayerPacket to its bottom layer i.e, Data Layer.<BR>
 * 4. Receives data from its bottom layer i.e, Data Layer.<BR>
 * 5. Parses, constructs NetworkLayerPacket and sends NetworkLayerPacket to its upper layer i.e, Transport Layer<BR>
 * 
 * @author Group7
 */

public class NetworkLayer {

	//private static final Logger LOGGER = LoggingService.getLogger(Constants.CN_PROJECT_LOGGER);
	
    private Node node;

    public NetworkLayer(Node node) {
        this.node = node;
    }

    public void sendToTransportLayer(NetworkLayerPacket networkLayerPacket) {
    	//LOGGER.log(Level.INFO, "Node-"+node.getNodeName() + " --> Sending Packet to Transport Layer ");
        node.getTransportLayer().receiveFromNetworkLayer(networkLayerPacket);
    }

    public void receiveFromTransportLayer(NetworkLayerPacket networkLayerPacket, byte[] destAddress, long timeStamp) throws Exception {
    	//LOGGER.log(Level.INFO, "Node-"+node.getNodeName() + " --> Received Packet from Transport Layer ");
        networkLayerPacket.destIpAddress = destAddress;
        networkLayerPacket.checksum = calculateChecksum(networkLayerPacket.body);

        //LOGGER.log(Level.INFO, "Node-"+node.getNodeName() + " --> Constructed Network Layer Packet with received data, source ip "+Util.getIpAddressInStringFormat(node.getIpAddress())+" and destination ip "+Util.getIpAddressInStringFormat(destAddress));
        byte[] gatewayAddress = node.getRoutingTable().getNextHop(networkLayerPacket.destIpAddress);
        sendToDataLinkLayer(networkLayerPacket, gatewayAddress, timeStamp);

    }

    public void sendToDataLinkLayer(NetworkLayerPacket networkLayerPacket, byte[] destGateway, long timeStamp) throws Exception {
    	//LOGGER.log(Level.INFO, "Node-"+node.getNodeName() + " --> Sending Packet to DataLink Layer ");
        node.getDataLinkLayer().receiveFromNetworkLayer(networkLayerPacket, destGateway, timeStamp);
    }

    public void receiveFromDataLinkLayer(byte[] data, long timeStamp) throws Exception {
    	//LOGGER.log(Level.INFO, "Node-"+node.getNodeName() + " --> Received Packet from DataLink Layer ");
        NetworkLayerPacket networkLayerPacket = new NetworkLayerPacket(data);
        if (Arrays.equals(networkLayerPacket.destIpAddress, node.getActiveInterface().getIpAddress())) {
        	networkLayerPacket.printPacket();
            sendToTransportLayer(networkLayerPacket);
        } else {
            //LOGGER.log(Level.WARNING, "Packet doesn't belong to this IP: " + Arrays.toString(node.getIpAddress()));
        	// Forward to respective interface;
        	for(NWInterface nwInterface : node.getInterfaceList()) {
        		byte[] nwAddress = Util.calculateNWAddress(networkLayerPacket.destIpAddress, nwInterface.getMask());
        		if(Arrays.equals(nwInterface.getNetworkAddress(), nwAddress)) {
        			node.setActiveInterface(nwInterface);
        			break;
        		}
        	}
            receiveFromTransportLayer(networkLayerPacket, networkLayerPacket.destIpAddress, timeStamp);
        }
    }

    /**
     * Calculate the Internet Checksum of a buffer (RFC 1071 - http://www.faqs.org/rfcs/rfc1071.html)
     * Algorithm is
     * 1) apply a 16-bit 1's complement sum over all octets (adjacent 8-bit pairs [A,B], final odd length is [A,0])
     * 2) apply 1's complement to this final sum
     * <p/>
     * Notes:
     * 1's complement is bitwise NOT of positive value.
     * Ensure that any carry bits are added back to avoid off-by-one errors
     *
     * @param buf The message
     * @return The checksum
     */
    public byte[] calculateChecksum(byte[] buf) {
        int length = buf.length;
        int i = 0;

        long sum = 0;
        long data;

        // Handle all pairs
        while (length > 1) {
            // Corrected to include @Andy's edits and various comments on Stack Overflow
            data = (((buf[i] << 8) & 0xFF00) | ((buf[i + 1]) & 0xFF));
            sum += data;
            // 1's complement carry bit correction in 16-bits (detecting sign extension)
            if ((sum & 0xFFFF0000) > 0) {
                sum = sum & 0xFFFF;
                sum += 1;
            }

            i += 2;
            length -= 2;
        }

        // Handle remaining byte in odd length buffers
        if (length > 0) {
            // Corrected to include @Andy's edits and various comments on Stack Overflow
            sum += (buf[i] << 8 & 0xFF00);
            // 1's complement carry bit correction in 16-bits (detecting sign extension)
            if ((sum & 0xFFFF0000) > 0) {
                sum = sum & 0xFFFF;
                sum += 1;
            }
        }

        // Final 1's complement value correction to 16-bits
        sum = ~sum;
        sum = sum & 0xFFFF;
        ByteBuffer buffer = ByteBuffer.allocate(2);
        buffer.putShort((short) sum);
        return buffer.array();

    }
}
