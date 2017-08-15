package edu.fit.cs.cn.util;


/**
 * Packet class to store byte array and priority
 * in the priority queue
 */
public class Packet implements Comparable<Packet>{
	
    private static int counter = 0; //keeps track of last priority
    private byte[] data; // packet data
    private int priority; //packet priority
    
    public Packet(byte [] data , int priority){
        this.data = data;
        this.priority = priority;
    }
    
    public Packet() {
    }
    
    /**
     * @return - packet body
     */
    public byte [] getData(){
        return this.data;
    }
    
    /**
     * @return Packet priority
     */
    public int getPriority(){
        return this.priority;
    }

    /* (non-Javadoc)
     * @see java.lang.Comparable#compareTo(java.lang.Object)
     */
    @Override
    public int compareTo (Packet packet) {
        return this.getPriority () - packet.getPriority ();
    }
    
    /**
     * @return next priority
     */
    public synchronized static int getNextPriority(){
        return counter++;
    }

	@Override
	public String toString() {
		return "Packet [priority="
				+ priority + "]";
	}
    
    
}
