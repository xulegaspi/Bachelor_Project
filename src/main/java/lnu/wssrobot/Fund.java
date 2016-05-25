/**
 * Fund.java
 * 28 feb 2016
 */
package lnu.wssrobot;

import org.codehaus.jackson.JsonGenerationException;
import org.codehaus.jackson.JsonNode;
import org.codehaus.jackson.map.JsonMappingException;
import org.codehaus.jackson.map.ObjectMapper;

import java.io.IOException;

/**
 * @author jlnmsi
 *
 */
public class Fund implements JsonObject {
	private final int fundID;
	private final String fundName;
	private final String fundUrl;
	private double currentPrice;
	private long discoveryTime;
	private final String currency;
	private String datum;
	
	public Fund(int id, String name, String url, double price, String currencyCode, long time, String date) {
		fundID = id;
		fundName = name;
		fundUrl = url;
		currentPrice = price;
		currency = currencyCode;
		discoveryTime = time;
		datum = date;
	}

	public Fund(int id, String name, String url, double price, String currencyCode, long time) {
		fundID = id;
		fundName = name;
		fundUrl = url;
		currentPrice = price;
		currency = currencyCode;
		discoveryTime = time;
		datum = "";
	}

	public Fund() {
		fundID = 0;
		fundName = "";
		fundUrl = "";
		currentPrice = 0;
		currency = "";
		discoveryTime = 0;
		datum = "";
	}

	public void setCurrentPrice(double new_price) { currentPrice = new_price; }
	public void setDatum(String new_datum) { datum = new_datum; }
	public int getID() { return fundID; }
	public String getFundName() { return fundName; }
	public String getFundUrl() { return fundUrl; }
	public double getCurrentPrice() { return currentPrice; }
	public long getDiscoveryTime() { return discoveryTime; }
	public void setDiscoveryTime(long time) { discoveryTime = time;	}
	public String getCurrencyCode() { return currency; }
	public String getDatum() { return datum; }
	
	@Override
	public String toString() {
		return fundID +"\t"+ fundName +"\t"+ fundUrl +"\t"+ currentPrice +"\t"+ currency +"\t"+ discoveryTime;
	}
	
	@Override
	public boolean equals(Object o) {
		if (o instanceof Fund) {
			Fund other = (Fund) o;
			return fundID==other.fundID && currentPrice==other.currentPrice; 
		}
		return false;
	}
	
	/*
	 * Implementing the JsonObject interface.
	 * 
	 */
	public String toJsonString() {
		ObjectMapper mapper = new ObjectMapper();
		String jsonString = null;
		try {
			jsonString =  mapper.writeValueAsString(this);
		} catch (JsonGenerationException e) {
			e.printStackTrace();
		} catch (JsonMappingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		return jsonString;
	}
	
	public JsonNode toJsonNode() {
		ObjectMapper mapper = new ObjectMapper();
		JsonNode jsonNode =  mapper.convertValue(this, JsonNode.class);	
		return jsonNode;
	}
	

}
