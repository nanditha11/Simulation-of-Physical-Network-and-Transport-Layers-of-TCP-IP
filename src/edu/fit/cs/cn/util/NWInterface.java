/**
 * 
 */
package edu.fit.cs.cn.util;

import java.util.Arrays;

import edu.fit.cs.cn.link.Medium;

/**
 * @author krishna
 *
 */
public class NWInterface {
	
	private Medium medium;
	private int bandWidth;
	private String iFaceName;	
	private byte[] ipAddress;
	private byte[] macAddress;
	private byte[] mask;
	private byte[] networkAddress;
	
	/**
	 * @return the medium
	 */
	public Medium getMedium() {
		return medium;
	}
	/**
	 * @param medium the medium to set
	 */
	public void setMedium(Medium medium) {
		this.medium = medium;
	}

	/**
	 * @return the iFaceName
	 */
	public String getiFaceName() {
		return iFaceName;
	}
	/**
	 * @param iFaceName the iFaceName to set
	 */
	public void setiFaceName(String iFaceName) {
		this.iFaceName = iFaceName;
	}
	/**
	 * @return the ipAddress
	 */
	public byte[] getIpAddress() {
		return ipAddress;
	}
	/**
	 * @param ipAddress the ipAddress to set
	 */
	public void setIpAddress(byte[] ipAddress) {
		this.ipAddress = ipAddress;
	}
	/**
	 * @return the macAddress
	 */
	public byte[] getMacAddress() {
		return macAddress;
	}
	/**
	 * @param macAddress the macAddress to set
	 */
	public void setMacAddress(byte[] macAddress) {
		this.macAddress = macAddress;
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
	 * @return the bandWidth
	 */
	public int getBandWidth() {
		return bandWidth;
	}
	/**
	 * @param bandWidth the bandWidth to set
	 */
	public void setBandWidth(int bandWidth) {
		this.bandWidth = bandWidth;
	}
	
	/**
	 * @return the networkAddress
	 */
	public byte[] getNetworkAddress() {
		return networkAddress;
	}
	
	/**
	 * @param networkAddress the networkAddress to set
	 */
	public void setNetworkAddress(byte[] networkAddress) {
		this.networkAddress = networkAddress;
	}
	
	@Override
	public String toString() {
		return "NWInterface [iFaceName=" + iFaceName + ", ipAddress="
				+ Arrays.toString(ipAddress) + ", macAddress="
				+ Arrays.toString(macAddress) + ", mask="
				+ Arrays.toString(mask) + ", medium=" + medium + ", bandWidth="
				+ bandWidth + "]";
	}
	

}
