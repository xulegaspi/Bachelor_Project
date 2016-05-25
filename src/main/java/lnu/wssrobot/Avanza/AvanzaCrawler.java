/**
 * DNCrawler.java
 * 27 feb 2016
 */
package lnu.wssrobot.Avanza;

import lnu.wssrobot.Fund;
import lnu.wssrobot.JsonObject;
import lnu.wssrobot.Util;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.concurrent.BlockingQueue;

/**
 * Extracting fund data from website: https://www.avanza.se/fonder/lista.html
 * 
 * This is rather straight forward since each page in the list has their own url
 * and we can move from page 1 to 2 by simply switch from 1 to 2 in the url.
 * 
 * Basic idea
 * ===========
 * 1. Initialize by traversing all pages to find (and save) current price for each fund
 * 2. Every N:th minute
 *    2.1 Traverse all pages to find the price for each fund
 *    2.2 If fund price changed ==> save new price
 *    
 * Save ==> store in map id2fund and add to output queue jsonQueue   
 *
 * @author jlnmsi
 *
 */
public class AvanzaCrawler extends Thread {
	private final int IterationSleep = 10;  // Time between iterations in minutes
	private final BlockingQueue<JsonObject> jsonQueue;  // Our output channel
	private HashMap<Integer, Fund> id2fund = new HashMap<Integer, Fund>();

	public AvanzaCrawler(BlockingQueue<JsonObject> queue) {
		super("AvanzaCrawlerThread");
		jsonQueue = queue;
		System.out.println("Enter "+this.getClass().getName());
	}
	
	@Override
	public void run() {
		
		// Initialize all funds with a current value
		System.out.println("\n"+ Util.getCurrentTime()+": Initializing all funds");
		ArrayList<Fund> allFunds = traverseAllFunds();
		System.out.println(Util.getCurrentTime());
		for (Fund f : allFunds) {
			int id = f.getID();
			id2fund.put(id, f);
			Util.addToQueue(jsonQueue, f);
		}
		System.out.println("Fund count: "+allFunds.size());
		
		// Repeat forever
		while (true) {
			Util.sleep(IterationSleep*60*1000);
			System.out.println("\n"+ Util.getCurrentTime()+": Starting a new iteration");
			
			allFunds = traverseAllFunds();
			int newCount = 0;
			for (Fund newFund : allFunds) {
				int id = newFund.getID();
				Fund oldFund = id2fund.get(id);
				
				if (oldFund == null) {
					System.out.println("New fund found: "+newFund.toJsonString());
					id2fund.put(id, newFund);
					Util.addToQueue(jsonQueue, newFund);
					newCount++;
				}
				else if (!oldFund.equals(newFund)) {
					id2fund.put(id, newFund);
					Util.addToQueue(jsonQueue, newFund);
					newCount++;
				}
			}
			if (newCount > 0)
				System.out.println("Fund count: "+allFunds.size()+", Updated funds: "+newCount);
			else
				System.out.println("Fund count: "+allFunds.size());
		}
	}
	
	private ArrayList<Fund> traverseAllFunds() {
		String firstPart = "https://www.avanza.se/fonder/lista.html?disableSelection=false&name=&page=";
		String secondPart = "&sortField=NAME&sortOrder=ASCENDING&activeTab=overview";
		//String page = "https://www.avanza.se/fonder/lista.html?disableSelection=false&name=&page=1&sortField=NAME&sortOrder=ASCENDING&activeTab=overview";
		
		int pageNumber = 0; boolean keepOn = true; 
		ArrayList<Fund> allFunds = new ArrayList<Fund>();
		while (keepOn) {
			pageNumber++;
			String page = firstPart+pageNumber+secondPart;  // Same url but different page number
			Document doc = null;
			try {
				doc = Jsoup.connect(page).get();
				// Each element representing a certain fund contains the attribute "data-oid"
				// ==> we use "data-oid" to reconize fund elements
				Elements funds = doc.getElementsByAttribute("data-oid");
				int size = funds.size();
				if (size > 0) {       // size=0 ==> no more pages available
					for (Element row : funds) {
						Fund f = parseRow(row);
						allFunds.add(f);
					}
				}
				else {
					keepOn = false; 
				}
			} 
			catch (SocketTimeoutException ex) {
        		System.out.println("\tTIME-OUT for page "+ pageNumber);
        		
        	}
			catch (IOException e) {
				e.printStackTrace();
			}
		}
		//System.out.println("Fund pages parsed: "+(pageNumber-1)+", Fund count: "+allFunds.size());
		return allFunds;
	}
	
	private String urlPrefix = "https://www.avanza.se";
	private Fund parseRow(Element fundRow) {
		//System.out.println(fundRow);
		//System.exit(-1);
		
		// Find fund id
		String dataOID = fundRow.attr("data-oid");
		int id = Integer.parseInt(dataOID);
		
		// Find fund name and url
		Element name = (fundRow.getElementsByAttributeValueContaining("class", "fundListName")).get(0);
		//System.out.println(name);
		Element anchor = (name.getElementsByAttribute("href")).get(0);
		String url = urlPrefix+anchor.attr("href");
		String title = anchor.attr("title");
		String text = anchor.text();		
		
		// Find current price
		Element nav = (fundRow.getElementsByAttributeValueContaining("class", "fundListNav")).get(0);
		String navPrice = nav.text();
		double price = getDouble( navPrice);
		
		// Keep track of this potential error
		if (!title.equals(text)) {
			System.err.println(title+"\t"+text);
			System.exit(-1);
		}
		// Set time stamp
		long timeStamp = System.currentTimeMillis();
		
		// Avanza is always presenting the price in SEK
		String currency = "SEK";
		
		Fund fund =  new Fund(id,title,url,price,currency,timeStamp);
		//System.out.println(fund);
		//System.exit(-1);
		return fund;
		
	}
	/*
	 * Utility methods
	 * 
	 */
	private double getDouble(String s) {
		// Replace , with , and remove whitespace
		String str = "";
		for (int i=0; i<s.length(); i++) {
			char c = s.charAt(i);
			if (c == ',')
				str += '.';
			else if (Character.isDigit(c))
				str += c;
		}
		//System.out.println(s+" --> "+str);
		return Double.parseDouble(str);
	}

}
