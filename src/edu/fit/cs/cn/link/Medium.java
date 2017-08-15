package edu.fit.cs.cn.link;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import edu.fit.cs.cn.node.Node;
import edu.fit.cs.cn.util.Packet;

/**
 * <b>Medium.java</b><BR>
 * Usage: This class is a simulation of real world network medium.<BR>
 * 
 * @author Group7
 */
public class Medium {

    private String name;
    private int MTU;
    private int RTU;
    private boolean isBusy;
    private byte[] buffer; // buffer of the medium
    private int bufferLength = 0; // number of char used

    private List<Node> registeredNodeList = new ArrayList<Node>();
    private Set<String> connectedNodeSet = new HashSet<String>();
    
    private byte[] nwAddress = null;
    private byte[] mask = null;

    public Medium(String name){
        this.name = name;
    }

    public void openConnection(String nodeId){
        this.connectedNodeSet.add(nodeId);
        this.isBusy = true;
    }

    /**
	 * @return the registeredNodeList
	 */
	public List<Node> getRegisteredNodeList() {
		return registeredNodeList;
	}

	public synchronized void fillBuffer(byte c) throws Exception{
        synchronized (this) {
            if(buffer == null)
                buffer = new byte[MTU];
            buffer[bufferLength] = c;
            bufferLength++;
        }
    }
	
	public synchronized void fillBuffer(byte[] bytes) throws Exception{
        synchronized (this) {
            if(buffer == null)
                buffer = new byte[MTU];
            
            buffer = bytes;
            bufferLength += bytes.length;
            //buffer[bufferLength] = c;
            //bufferLength++;
        }
    }

    /**<BR>
     * <strong>addNode Method.</strong><BR>
     * Usage: Add/Register the given node with medium.<BR>
     * @param node
     *
     */
    public void addNode(Node node) {
        registeredNodeList.add(node);//add node to the registered node list
    }


    /**<BR>
     * <strong>flush Method.</strong><BR>
     * Usage: if buffer available, invokes the @transmit method<BR>
     *
     */
    public synchronized void flush() throws Exception{
        synchronized (this) {
            if(bufferLength > 0) {
                if(bufferLength < MTU) {
                    byte[] b  = new byte[bufferLength];
                    System.arraycopy(buffer, 0, b, 0, bufferLength);
                    transmit(b);
                }else
                    transmit(buffer);
            }
        }
    }

    /**<BR>
     * <strong>transmit Method.</strong><BR>
     * Usage: This method is called by connected node to transmit message.<BR>
     * After successfully reading the message buffer,
     * receive method is called of every node is called to broadcast message.
     * @param message
     *
     */
    public synchronized void transmit(byte[] message) throws Exception{

    	Packet packet = new Packet(message, Packet.getNextPriority());
    	
        for(Node node : registeredNodeList) {
            //Check if sender node and receiver does not match
            if(!node.getNodeName().equals(getConnectedNode())) {
                node.receive(packet);//call receive method of noder
            }
        }
        //Util.blockingQueue.add(packet);

        buffer = null;
        bufferLength = 0;
        isBusy = false;
    }

    public String getConnectedNode() {
        for (Iterator<String> iterator = connectedNodeSet.iterator(); iterator.hasNext();) {
            String nodeId = iterator.next();
            return nodeId;
        }
        return null;
    }

    public int getMTU() {
        return MTU;
    }

    public void setMTU(int MTU) {
        this.MTU = MTU;
    }

    public int getRTU() {
        return RTU;
    }

    public void setRTU(int RTU) {
        this.RTU = RTU;
    }

    public boolean isBusy() {
        return isBusy;
    }

    public void setIsBusy(boolean isBusy) {
        this.isBusy = isBusy;
    }

	/**
	 * @return the name
	 */
	public String getName() {
		return name;
	}

	/**
	 * @param name the name to set
	 */
	public void setName(String name) {
		this.name = name;
	}

	/**
	 * @return the nwAddress
	 */
	public byte[] getNwAddress() {
		return nwAddress;
	}

	/**
	 * @param nwAddress the nwAddress to set
	 */
	public void setNwAddress(byte[] nwAddress) {
		this.nwAddress = nwAddress;
	}

	/**
	 * @return the mask
	 */
	public byte[] getMask() {
		return mask;
	}

	/**
	 * @param mask the mask to set
	 */
	public void setMask(byte[] mask) {
		this.mask = mask;
	}
}
