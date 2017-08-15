package edu.fit.cs.cn.entities;

/**
 * <b>Transmission.java</b><BR>
 * Usage: Entity class to store each transmission<BR>
 *
 * @author Group7
 */

public class Transmission {

	private long timeStamp;//Timestamp of transmission 
	private String sourceNode;//Source Node ID
	private String filePath;//File Path
	private String destNode;//Destination Node ID
	
	/**<BR>
	 * <strong>Transmission Method.</strong><BR>
	 * Usage: Constructor to create Transmission object for each transmission read from config file.<BR>  
	 * @param timeStamp
	 * @param sourceNode
	 * @param filePath
	 * @param destNode
	 * 
	 */
	public Transmission(long timeStamp, String sourceNode, String destNode, String filePath ) {
		this.timeStamp = timeStamp;
		this.sourceNode = sourceNode;
		this.filePath = filePath;
		this.destNode = destNode;
	}
	
	
	/**
	 * @return the timeStamp
	 */
	public long getTimeStamp() {
		return timeStamp;
	}


	/**
	 * @param timeStamp the timeStamp to set
	 */
	public void setTimeStamp(long timeStamp) {
		this.timeStamp = timeStamp;
	}


	/**
	 * @return the sourceNode
	 */
	public String getSourceNode() {
		return sourceNode;
	}


	/**
	 * @param sourceNode the sourceNode to set
	 */
	public void setSourceNode(String sourceNode) {
		this.sourceNode = sourceNode;
	}

	/**
	 *
	 * @return filePath
	 */
	public String getFilePath() {
		return filePath;
	}

	/**
	 *
	 * @param filePath
	 */
	public void setFilePath(String filePath) {
		this.filePath = filePath;
	}

	/**
	 * @return the destNode
	 */
	public String getDestNode() {
		return destNode;
	}


	/**
	 * @param destNode the destNode to set
	 */
	public void setDestNode(String destNode) {
		this.destNode = destNode;
	}

	
	@Override
	public String toString() {
		return "Transmission [timeStamp=" + timeStamp + ", sourceNode="
				+ sourceNode + ", fileName=" + filePath + ", destNode="
				+ destNode + "]";
	}
	
}
