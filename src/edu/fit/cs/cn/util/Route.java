/**
 * 
 */
package edu.fit.cs.cn.util;

/**
 * @author krishna
 *
 */
public class Route{
	
	byte[] nwAddress;
	byte[] mask;
	Object nextHop;
	int metric;
	
	public Route(byte[] ipAddress, byte[] mask, Object nextHop, int metric) {			
		this.nwAddress = ipAddress;
		this.mask = mask;
		this.nextHop = nextHop;
		this.metric = metric;
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

	/**
	 * @return the nextHop
	 */
	public Object getNextHop() {
		return nextHop;
	}

	/**
	 * @param nextHop the nextHop to set
	 */
	public void setNextHop(Object nextHop) {
		this.nextHop = nextHop;
	}

	/**
	 * @return the metric
	 */
	public int getMetric() {
		return metric;
	}

	/**
	 * @param metric the metric to set
	 */
	public void setMetric(int metric) {
		this.metric = metric;
	}
	
}
